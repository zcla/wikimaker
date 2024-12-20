package zcla71.mybible;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collection;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import lombok.extern.slf4j.Slf4j;

// Documentação: https://docs.google.com/document/d/12rf4Pqy13qhnAW31uKkaWNTBDTtRbNW0s7cM0vcimlA/
@Slf4j
public class MyBible {
	public static void main(String[] args) throws MalformedURLException, IOException, URISyntaxException, SQLException {
		new MyBible(new URI("https://www.ph4.org/_dl.php?back=bbl&a=BAM&b=mybible&c"));
	}

	private URI uri;
	private final File TEMP_DIRECTORY = new File("./temp");
	private String downloadedFileName;
	private File zippedFile = null;
	private Collection<File> unzippedFiles = null;

	public MyBible(URI uri) throws MalformedURLException, IOException, SQLException {
		this.uri = uri;
		log.info("URI: " + this.uri.toString());
		this.downloadedFileName = getDownloadFileName(this.uri);
		log.info("Downloaded file name: " + this.downloadedFileName);
		Collection<File> apagarAoFinal = new ArrayList<>();
		try {
			// download
			download();
			// unzip
			unzip();
			// sql
			sql();
		} finally {
			apagarAoFinal.add(this.zippedFile);
			apagarAoFinal.addAll(this.unzippedFiles);
			for (File arq : apagarAoFinal) {
				arq.delete();
			}
		}
	}

	private void download() throws MalformedURLException, IOException {
		Files.createDirectories(this.TEMP_DIRECTORY.toPath());
		this.zippedFile = File.createTempFile(removeExtension(this.downloadedFileName) + ".", ".zip", this.TEMP_DIRECTORY);
		log.info("Temp file name: " + this.zippedFile.getAbsolutePath());
		try (
			BufferedInputStream in = new BufferedInputStream(this.uri.toURL().openStream());
			FileOutputStream fos = new FileOutputStream(this.zippedFile);
		) {
			byte dataBuffer[] = new byte[1024];
			int bytesRead;
			log.info("Downloading");
			while ((bytesRead = in.read(dataBuffer, 0, 1024)) != -1) {
				fos.write(dataBuffer, 0, bytesRead);
			}
			log.info("Bytes downloaded: " + zippedFile.length());
		}
	}

	private String getDownloadFileName(URI uri) {
		String result = null;
		try {
			URL url = uri.toURL();
			URLConnection con = url.openConnection();
			String fieldValue = con.getHeaderField("Content-Disposition");
			if (fieldValue != null && fieldValue.contains("filename=\"")) {
				result = fieldValue.substring(fieldValue.indexOf("filename=\"") + 10, fieldValue.length() - 1);
			}
		} catch (Exception e) {
			// ignora
		}
		return result;
	}

	private String removeExtension(String fileName) {
		if (fileName == null) {
			return null;
		}
		int pos = fileName.lastIndexOf(".");
		if (pos == -1) {
			return fileName;
		}
		return fileName.substring(0, pos);
	}

	private void sql() throws SQLException {
		String nomeArq = null;
		String expected = this.TEMP_DIRECTORY.toPath().normalize() + "/" + removeExtension(this.downloadedFileName) + ".SQLite3";
		for (File unzippedFile : this.unzippedFiles) {
			if (expected.equalsIgnoreCase(unzippedFile.getPath())) {
				nomeArq = expected;
			} else {
				log.warn("Arquivo ignorado: " + unzippedFile.getPath());
			}
		}

		if (nomeArq == null) {
			throw new RuntimeException("Arquivo não encontrado: " + expected);
		}

		// TODO Listar tabelas; criar classes correspondentes

		try (
			Connection conn = DriverManager.getConnection("jdbc:sqlite:" + nomeArq);
			Statement stat = conn.createStatement();
			ResultSet rs = stat.executeQuery("select * from books");
		) {
			while(rs.next()) {
				System.out.println("book_number = " + rs.getString("book_number"));
				System.out.println("short_name = " + rs.getString("short_name"));
				System.out.println("long_name = " + rs.getString("long_name"));
			}
		}
	}

	private void unzip() throws FileNotFoundException, IOException {
		this.unzippedFiles = new ArrayList<>();
		Path targetDir = this.TEMP_DIRECTORY.toPath().normalize();
		try (
			ZipInputStream zipIn = new ZipInputStream(new FileInputStream(this.zippedFile))
		) {
			for (ZipEntry ze; (ze = zipIn.getNextEntry()) != null; ) {
				Path resolvedPath = targetDir.resolve(ze.getName()).normalize();
				if (!resolvedPath.startsWith(targetDir)) {
					// see: https://snyk.io/research/zip-slip-vulnerability
					throw new RuntimeException("Entry with an illegal path: " 
							+ ze.getName());
				}
				if (ze.isDirectory()) {
					Files.createDirectories(resolvedPath);
				} else {
					Files.createDirectories(resolvedPath.getParent());
					Files.copy(zipIn, resolvedPath, StandardCopyOption.REPLACE_EXISTING);
				}
				this.unzippedFiles.add(resolvedPath.toFile());
			}
		}
	}
}
