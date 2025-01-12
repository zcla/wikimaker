package zcla71.mybible;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import lombok.extern.slf4j.Slf4j;
import zcla71.mybible.model.Database;

@Slf4j
public class MyBibleZip extends MyBible {
    private File zippedFile = null;
    private Collection<File> unzippedFiles = null;

    public MyBibleZip(URI uri, String id) throws Exception {
        super(uri, id);
    }

    @Override
    protected Database doDownload() throws Exception {
        Database result = new Database();
        result.setUrl(getUri().toString());
        log.info("URI: " + getUri().toString());
        result.setDownloadedFileName(getDownloadFileName(getUri()));
        log.info("Downloaded file name: " + result.getDownloadedFileName());
        result.setTimestamp(LocalDateTime.now());
        Collection<File> apagarAoFinal = new ArrayList<>();

        try {
            download(result);
            unzip();
            sql(result);
        } finally {
            apagarAoFinal.add(this.zippedFile);
            apagarAoFinal.addAll(this.unzippedFiles);
            for (File arq : apagarAoFinal) {
                arq.delete();
            }
        }

        return result;
    }

    private void download(Database database) throws MalformedURLException, IOException {
        this.zippedFile = File.createTempFile(removeExtension(database.getDownloadedFileName()) + ".", ".zip", getTempDirectory());
        super.download(this.zippedFile);
    }

    private void unzip() throws FileNotFoundException, IOException {
        this.unzippedFiles = new ArrayList<>();
        Path targetDir = getTempDirectory().toPath().normalize();
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

    private void sql(Database database) throws SQLException, InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException, NoSuchFieldException {
        String baseFileName = getTempDirectory().toPath().normalize() + "/" + removeExtension(database.getDownloadedFileName());
        String bibleFileName = baseFileName + ".SQLite3";
        String commentariesFileName = baseFileName + ".commentaries.SQLite3";
        for (File unzippedFile : this.unzippedFiles) {
            String tipo = null;
            if ((tipo == null) && unzippedFile.getPath().equalsIgnoreCase(bibleFileName)) {
                tipo = "bible";
                log.info(tipo);
                sqlBible(database, unzippedFile.getPath());
            }
            if ((tipo == null) && unzippedFile.getPath().equalsIgnoreCase(commentariesFileName)) {
                tipo = "commentaries";
                log.info(tipo);
                sqlCommentaries(database, unzippedFile.getPath());
            }
            if (tipo == null) {
                log.warn("Arquivo desconhecido: " + unzippedFile.getPath());
            }
        }
    }
}
