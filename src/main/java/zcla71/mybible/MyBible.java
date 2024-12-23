package zcla71.mybible;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.core.exc.StreamWriteException;
import com.fasterxml.jackson.core.util.DefaultIndenter;
import com.fasterxml.jackson.core.util.DefaultPrettyPrinter;
import com.fasterxml.jackson.databind.DatabindException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import zcla71.mybible.model.Bible;
import zcla71.mybible.model.Commentaries;
import zcla71.mybible.model.bible.Books;
import zcla71.mybible.model.bible.BooksAll;
import zcla71.mybible.model.bible.Introductions;
import zcla71.mybible.model.bible.Stories;
import zcla71.mybible.model.bible.Verses;
import zcla71.sqlite.SQLiteDb;

// Documentação: https://docs.google.com/document/d/12rf4Pqy13qhnAW31uKkaWNTBDTtRbNW0s7cM0vcimlA/
@Slf4j
public class MyBible {
    private final File TEMP_DIRECTORY = new File("./temp");
    private URI uri;
    @Getter
    private String url;
    @Getter
    private String downloadedFileName;
    private File zippedFile = null;
    private Collection<File> unzippedFiles = null;

    // Bible Module
    @Getter
    private Bible bible = null;
    // TODO Dictionary Module
    // TODO Subheadings Module
    // TODO Cross References Module
    // Commentaries Module
    @Getter
    private Commentaries commentaries = null;
    // TODO Reading Plan Module
    // TODO Devotions Module

    public MyBible(URI uri, String id) throws MalformedURLException, IOException, InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException, NoSuchFieldException, SQLException {
        this.uri = uri;
        this.url = uri.toString();
        log.info("URI: " + this.uri.toString());
        this.downloadedFileName = getDownloadFileName(this.uri);
        log.info("Downloaded file name: " + this.downloadedFileName);
        Collection<File> apagarAoFinal = new ArrayList<>();
        File jsonDownloadFile = new File("./data/" + id + ".json");
        if (jsonDownloadFile.exists()) {
            log.info("\tJson já gerado.");
        } else {
            try {
                download();
                unzip();
                sql();
                save(jsonDownloadFile);
            } finally {
                apagarAoFinal.add(this.zippedFile);
                apagarAoFinal.addAll(this.unzippedFiles);
                for (File arq : apagarAoFinal) {
                    arq.delete();
                }
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

    private void save(File file) throws StreamWriteException, DatabindException, IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.setSerializationInclusion(Include.NON_NULL);
        objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
        DefaultPrettyPrinter prettyPrinter = new DefaultPrettyPrinter();
        prettyPrinter.indentArraysWith(DefaultIndenter.SYSTEM_LINEFEED_INSTANCE);

        objectMapper.writer(prettyPrinter).writeValue(file, this);
    }

    private void sql() throws SQLException, InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException, NoSuchFieldException {
        String nomeArq = null;
        String baseFileName = this.TEMP_DIRECTORY.toPath().normalize() + "/" + removeExtension(this.downloadedFileName);
        String bibleFileName = baseFileName + ".SQLite3";
        String commentariesFileName = baseFileName + ".commentaries.SQLite3";
        for (File unzippedFile : this.unzippedFiles) {
            String tipo = null;
            if ((tipo == null) && unzippedFile.getPath().equalsIgnoreCase(bibleFileName)) {
                tipo = "bible";
                log.info(tipo);
                this.bible = new Bible();
                nomeArq = unzippedFile.getPath();
                SQLiteDb sqLiteDb = new SQLiteDb(nomeArq);
                try (
                    Connection conn = sqLiteDb.getConnection();
                ) {
                    Collection<String> tableNames = sqLiteDb.getTableNames(conn);
                    for (String tableName : tableNames) {
                        log.info("\t" + tableName);
                        switch (tableName) {
                            case "info":
                                Collection<zcla71.mybible.model.bible.Info> info = sqLiteDb.getData(conn, tableName, zcla71.mybible.model.bible.Info.class);
                                this.bible.setInfo(info);
                                break;

                            case "books":
                                Collection<Books> books = sqLiteDb.getData(conn, tableName, Books.class);
                                this.bible.setBooks(books);
                                break;

                            case "books_all":
                                Collection<BooksAll> booksAll = sqLiteDb.getData(conn, tableName, BooksAll.class);
                                this.bible.setBooksAll(booksAll);
                                break;

                            case "verses":
                                Collection<Verses> verses = sqLiteDb.getData(conn, tableName, Verses.class);
                                this.bible.setVerses(verses);
                                break;

                            case "introductions":
                                Collection<Introductions> introductions = sqLiteDb.getData(conn, tableName, Introductions.class);
                                this.bible.setIntroductions(introductions);
                                break;

                            case "stories":
                                Collection<Stories> stories = sqLiteDb.getData(conn, tableName, Stories.class);
                                this.bible.setStories(stories);
                                break;

                            // Desconhecidos / não documentados
                            case "android_metadata":
                                // Aparecem em alguns arquivos; ignora
                                break;

                            default:
                                log.warn("*** Tabela desconhecida: " + tableName);
                                break;
                        }
                    }
                }
            }
            if ((tipo == null) && unzippedFile.getPath().equalsIgnoreCase(commentariesFileName)) {
                tipo = "commentaries";
                log.info(tipo);
                this.commentaries = new Commentaries();
                nomeArq = unzippedFile.getPath();
                SQLiteDb sqLiteDb = new SQLiteDb(nomeArq);
                try (
                    Connection conn = sqLiteDb.getConnection();
                ) {
                    Collection<String> tableNames = sqLiteDb.getTableNames(conn);
                    for (String tableName : tableNames) {
                        log.info("\t" + tableName);
                        switch (tableName) {
                            case "info":
                                Collection<zcla71.mybible.model.commentaries.Info> info = sqLiteDb.getData(conn, tableName, zcla71.mybible.model.commentaries.Info.class);
                                this.commentaries.setInfo(info);
                                break;

                            case "commentaries":
                                Collection<zcla71.mybible.model.commentaries.Commentaries> commentaries = sqLiteDb.getData(conn, tableName, zcla71.mybible.model.commentaries.Commentaries.class);
                                this.commentaries.setCommentaries(commentaries);
                                break;

                            // Desconhecidos / não documentados
                            case "android_metadata":
                                // Aparecem em alguns arquivos; ignora
                                break;

                            default:
                                log.warn("*** Tabela desconhecida: " + tableName);
                                break;
                        }
                    }
                }
            }
            if (tipo == null) {
                log.warn("Arquivo desconhecido: " + unzippedFile.getPath());
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
