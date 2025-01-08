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
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.core.exc.StreamWriteException;
import com.fasterxml.jackson.databind.DatabindException;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import zcla71.mybible.model.Bible;
import zcla71.mybible.model.Commentaries;
import zcla71.mybible.model.bible.Books;
import zcla71.mybible.model.bible.BooksAll;
import zcla71.mybible.model.bible.Introductions;
import zcla71.mybible.model.bible.Stories;
import zcla71.mybible.model.bible.Verses;
import zcla71.sqlite.SQLiteDb;
import zcla71.tiddlywiki.TiddlyWiki;
import zcla71.utils.JacksonUtils;
import zcla71.wikimaker.wiki.biblia.TiddlerBiblia;
import zcla71.wikimaker.wiki.biblia.TiddlerCapitulo;
import zcla71.wikimaker.wiki.biblia.TiddlerLivro;
import zcla71.wikimaker.wiki.biblia.TiddlerVersiculo;
import zcla71.wikimaker.wiki.biblia.WikiBiblia;

// Documentação: https://docs.google.com/document/d/12rf4Pqy13qhnAW31uKkaWNTBDTtRbNW0s7cM0vcimlA/
@Slf4j
@NoArgsConstructor
public class MyBible {
    private final File TEMP_DIRECTORY = new File("./temp");
    private URI uri;
    @Getter
    private String url;
    @Getter
    private LocalDateTime timestamp;
    @Getter
    private String downloadedFileName;
    private File zippedFile = null;
    private Collection<File> unzippedFiles = null;

    // Bible Module
    // ATENÇÃO! Quando implementar algum, não esquecer de incluir em "if (jsonDownloadFile.exists()) {", dentro do construtor
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
        this.timestamp = LocalDateTime.now();
        Collection<File> apagarAoFinal = new ArrayList<>();

        File jsonDownloadFile = new File("./data/download/" + id + ".json");
        if (jsonDownloadFile.exists()) {
            log.info("\tJson já gerado. Carregando.");
            ObjectMapper objectMapper = JacksonUtils.getObjectMapperInstance();
            JacksonUtils.enableJavaTime(objectMapper);
            MyBible loaded = objectMapper.readValue(jsonDownloadFile, MyBible.class);
            this.url = loaded.url;
            this.timestamp = loaded.timestamp;
            this.downloadedFileName = loaded.downloadedFileName;
            this.bible = loaded.bible;
            this.commentaries = loaded.commentaries;
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

        File wikiOutputFile = new File("./data/wiki/" + id + ".html");
        if (wikiOutputFile.exists()) {
            log.info("\tWiki já gerado.");
            return;
        }

        WikiBiblia wiki = makeWiki();
        log.info("\tSalvando wiki");
        wiki.saveAsWiki(wikiOutputFile);
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
        ObjectMapper objectMapper = JacksonUtils.getObjectMapperInstance();
        JacksonUtils.enableJavaTime(objectMapper);
        objectMapper.writer(JacksonUtils.getPrettyPrinter()).writeValue(file, this);
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
                                List<zcla71.mybible.model.bible.Info> info = sqLiteDb.getData(conn, tableName, zcla71.mybible.model.bible.Info.class);
                                this.bible.setInfo(info);
                                break;

                            case "books":
                                List<Books> books = sqLiteDb.getData(conn, tableName, Books.class);
                                this.bible.setBooks(books);
                                break;

                            case "books_all":
                                List<BooksAll> booksAll = sqLiteDb.getData(conn, tableName, BooksAll.class);
                                this.bible.setBooksAll(booksAll);
                                break;

                            case "verses":
                                List<Verses> verses = sqLiteDb.getData(conn, tableName, Verses.class);
                                this.bible.setVerses(verses);
                                break;

                            case "introductions":
                                List<Introductions> introductions = sqLiteDb.getData(conn, tableName, Introductions.class);
                                this.bible.setIntroductions(introductions);
                                break;

                            case "stories":
                                List<Stories> stories = sqLiteDb.getData(conn, tableName, Stories.class);
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

    @JsonIgnore
    private String getNome() {
        return this.bible.getInfo().stream().filter(i -> i.getName().equals("description")).findFirst().get().getValue();
    }

    private WikiBiblia makeWiki() throws IOException {
        log.info("\tGerando wiki");
        DateTimeFormatter dtfHuman = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

        WikiBiblia wiki = new WikiBiblia(
                this.getNome(),
                "Importada [[daqui|" + this.getUrl() + "]] em " + this.getTimestamp().format(dtfHuman) + "."
        );

        // Bíblia
        bibliaToTiddler(wiki);

        return wiki;
    }

    private String bibliaToTiddler(WikiBiblia wiki) {
        StringBuilder sbTexto = new StringBuilder("! Livros");
        // Documentação: "MyBIble 4.4.3 alpha14 or a later version knows about both the books_all and the BOOKS table: it looks for the BOOKS_ALL table first and only if it is not found uses the BOOKS table."
        if (this.bible.getBooksAll() != null && this.bible.getBooksAll().size() > 0) {
            List<BooksAll> booksAllses = this.bible.getBooksAll();
            booksAllses.sort(new Comparator<BooksAll>() {
                @Override
                public int compare(BooksAll b1, BooksAll b2) {
                    return b1.getBook_number().compareTo(b2.getBook_number());
                }
            });
            for (BooksAll booksAll : booksAllses) {
                if (booksAll.getIs_present()) {
                    String name = booksAll.getTitle();
                    if (name == null) {
                        name = booksAll.getLong_name();
                    }
                    String title = livroToTiddler(booksAll, wiki);
                    sbTexto.append("\n* [[" + name + "|" + title + "]]");
                }
            }
        } else {
            throw new RuntimeException("Implementar livroToTiddler(Books)");
            // for (Books books : this.bible.getBooks()) {
            //     String title = livroToTiddler(biblia, livro, wiki);
            //     sbTexto.append("\n* [[" + books.getLong_name() + "|" + title + "]]");
            // }
        }

        TiddlerBiblia tiddlerBiblia = new TiddlerBiblia(
            this.getNome(),
            this.getUrl(),
            this.getTimestamp(),
            sbTexto.toString()
        );
        return wiki.setBiblia(tiddlerBiblia);
    }

    private String livroToTiddler(BooksAll booksAll, WikiBiblia wiki) {
        log.info("\t\t" + booksAll.getShort_name());

        StringBuilder sbTexto = new StringBuilder("! Capítulos");
        Integer last = null;
        List<Verses> verseses = this.bible.getVerses().stream().filter(v -> v.getBook_number().equals(booksAll.getBook_number())).toList();
        verseses = new ArrayList<>(verseses);
        verseses.sort(new Comparator<Verses>() {
            @Override
            public int compare(Verses v1, Verses v2) {
                return v1.getChapter().compareTo(v2.getChapter());
            }
        });
        for (Verses verses : verseses) {
            Integer current = verses.getChapter();
            if (!current.equals(last)) {
                String title = capituloToTiddler(booksAll, verses, wiki);
                sbTexto.append("\n* [[" + verses.getChapter().toString() + "|" + title + "]]");
                last = current;
            }
        }

        TiddlerLivro tiddlerLivro = new TiddlerLivro(
            booksAll.getShort_name(),
            booksAll.getLong_name(),
            this.getUrl(),
            this.getTimestamp(),
            sbTexto.toString()
        );
        return wiki.addLivro(tiddlerLivro);
    }

    private String capituloToTiddler(BooksAll booksAll, Verses versesChapter, WikiBiblia wiki) {
        StringBuilder sbTexto = new StringBuilder();

        List<Verses> verseses = this.bible.getVerses().stream().filter(v -> v.getBook_number().equals(booksAll.getBook_number()) && v.getChapter().equals(versesChapter.getChapter())).toList();
        verseses = new ArrayList<>(verseses);
        verseses.sort(new Comparator<Verses>() {
            @Override
            public int compare(Verses v1, Verses v2) {
                return v1.getChapter().compareTo(v2.getChapter());
            }
        });
        for (Verses verses : verseses) {
            String title = versiculoToTiddler(booksAll, versesChapter, verses, wiki);
            sbTexto.append("^^[[" + verses.getVerse().toString() + "|" + title + "]]^^{{" + title + "}}" + TiddlyWiki.LINE_BREAK);
        }

        TiddlerCapitulo tiddlerCapitulo = new TiddlerCapitulo(
            booksAll.getShort_name(),
            versesChapter.getChapter().toString(),
            this.getUrl(),
            this.getTimestamp(),
            sbTexto.toString()
        );
        return wiki.addCapitulo(tiddlerCapitulo);
    }

    private String versiculoToTiddler(BooksAll booksAll, Verses versesChapter, Verses verses, WikiBiblia wiki) {
        String numVersiculo = verses.getVerse().toString();

        TiddlerVersiculo tiddlerVersiculo = new TiddlerVersiculo(
            booksAll.getShort_name(),
            versesChapter.getChapter().toString(),
            numVersiculo,
            this.getUrl(),
            this.getTimestamp(),
            verses.getText()
        );
        return wiki.addVersiculo(tiddlerVersiculo);
    }
}
