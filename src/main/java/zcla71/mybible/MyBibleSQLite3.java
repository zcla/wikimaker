package zcla71.mybible;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Files;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;

import lombok.extern.slf4j.Slf4j;
import zcla71.mybible.model.Bible;
import zcla71.mybible.model.Database;
import zcla71.mybible.model.bible.Books;
import zcla71.mybible.model.bible.BooksAll;
import zcla71.mybible.model.bible.Introductions;
import zcla71.mybible.model.bible.Stories;
import zcla71.mybible.model.bible.Verses;
import zcla71.sqlite.SQLiteDb;
import zcla71.tiddlywiki.TiddlyWiki;
import zcla71.wikimaker.wiki.biblia.TiddlerBiblia;
import zcla71.wikimaker.wiki.biblia.TiddlerCapitulo;
import zcla71.wikimaker.wiki.biblia.TiddlerLivro;
import zcla71.wikimaker.wiki.biblia.TiddlerVersiculo;
import zcla71.wikimaker.wiki.biblia.WikiBiblia;

@Slf4j
public class MyBibleSQLite3 extends MyBible {
    private /*final*/ File TEMP_DIRECTORY; // inicializado em init() senão não fica disponível no construtor
    private String id;
    private URI uri;

    public MyBibleSQLite3(URI uri, String id) throws Exception {
        super(new Object[] { uri, id });
    }

    @Override
    protected void init(Object... arguments) {
        this.uri = (URI) arguments[0];
        this.id = (String) arguments[1];
        this.TEMP_DIRECTORY = new File("./temp");
    }

    @Override
    protected String getId() {
        return this.id;
    }

    @Override
    protected Database doDownload() throws Exception {
        Database result = new Database();
        result.setUrl(uri.toString());
        log.info("URI: " + this.uri.toString());
        result.setDownloadedFileName(getDownloadFileName(this.uri));
        log.info("Downloaded file name: " + result.getDownloadedFileName());
        result.setTimestamp(LocalDateTime.now());

        File downloaded = null;
        try {
            downloaded = download(result);
            sql(result, downloaded);
        } finally {
            if (downloaded != null) {
                downloaded.delete();
            }
        }

        return result;
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

    private File download(Database database) throws MalformedURLException, IOException {
        Files.createDirectories(this.TEMP_DIRECTORY.toPath());
        File result = File.createTempFile(removeExtension(database.getDownloadedFileName()) + ".", ".SQLite3", this.TEMP_DIRECTORY);
        log.info("Temp file name: " + result.getAbsolutePath());
        try (
            BufferedInputStream in = new BufferedInputStream(this.uri.toURL().openStream());
            FileOutputStream fos = new FileOutputStream(result);
        ) {
            byte dataBuffer[] = new byte[1024];
            int bytesRead;
            log.info("Downloading");
            while ((bytesRead = in.read(dataBuffer, 0, 1024)) != -1) {
                fos.write(dataBuffer, 0, bytesRead);
            }
            log.info("Bytes downloaded: " + result);
        }
        return result;
    }

    private void sql(Database database, File downloaded) throws SQLException, InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException, NoSuchFieldException {
        database.setBible(new Bible());
        SQLiteDb sqLiteDb = new SQLiteDb(downloaded.getPath());
        try (
            Connection conn = sqLiteDb.getConnection();
        ) {
            Collection<String> tableNames = sqLiteDb.getTableNames(conn);
            for (String tableName : tableNames) {
                log.info("\t" + tableName);
                switch (tableName) {
                    case "info":
                        List<zcla71.mybible.model.bible.Info> info = sqLiteDb.getData(conn, tableName, zcla71.mybible.model.bible.Info.class);
                        database.getBible().setInfo(info);
                        break;

                    case "books":
                        List<Books> books = sqLiteDb.getData(conn, tableName, Books.class);
                        database.getBible().setBooks(books);
                        break;

                    case "books_all":
                        List<BooksAll> booksAll = sqLiteDb.getData(conn, tableName, BooksAll.class);
                        database.getBible().setBooksAll(booksAll);
                        break;

                    case "verses":
                        List<Verses> verses = sqLiteDb.getData(conn, tableName, Verses.class);
                        database.getBible().setVerses(verses);
                        break;

                    case "introductions":
                        List<Introductions> introductions = sqLiteDb.getData(conn, tableName, Introductions.class);
                        database.getBible().setIntroductions(introductions);
                        break;

                    case "stories":
                        List<Stories> stories = sqLiteDb.getData(conn, tableName, Stories.class);
                        database.getBible().setStories(stories);
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

    @Override
    protected WikiBiblia makeWiki(Database database) {
        DateTimeFormatter dtfHuman = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

        WikiBiblia wiki = new WikiBiblia(
                database.getName(),
                "Importada [[daqui|" + database.getUrl() + "]] em " + database.getTimestamp().format(dtfHuman) + "."
        );

        // Bíblia
        bibliaToTiddler(database, wiki);

        return wiki;
    }

    private String bibliaToTiddler(Database database, WikiBiblia wiki) {
        StringBuilder sbTexto = new StringBuilder("! Livros");
        // Documentação: "MyBIble 4.4.3 alpha14 or a later version knows about both the books_all and the BOOKS table: it looks for the BOOKS_ALL table first and only if it is not found uses the BOOKS table."
        if (database.getBible().getBooksAll() != null && database.getBible().getBooksAll().size() > 0) {
            List<BooksAll> booksAllses = database.getBible().getBooksAll();
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
                    String title = livroToTiddler(database, booksAll, wiki);
                    sbTexto.append("\n* [[" + name + "|" + title + "]]");
                }
            }
        } else {
            throw new RuntimeException("Implementar livroToTiddler(Books)");
            // for (Books books : this.database.getBible().getBooks()) {
            //     String title = livroToTiddler(biblia, livro, wiki);
            //     sbTexto.append("\n* [[" + books.getLong_name() + "|" + title + "]]");
            // }
        }

        TiddlerBiblia tiddlerBiblia = new TiddlerBiblia(
            database.getName(),
            database.getUrl(),
            database.getTimestamp(),
            sbTexto.toString()
        );
        return wiki.setBiblia(tiddlerBiblia);
    }

    private String livroToTiddler(Database database, BooksAll booksAll, WikiBiblia wiki) {
        log.info("\t\t" + booksAll.getShort_name());

        StringBuilder sbTexto = new StringBuilder("! Capítulos");
        Integer last = null;
        List<Verses> verseses = database.getBible().getVerses().stream().filter(v -> v.getBook_number().equals(booksAll.getBook_number())).toList();
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
                String title = capituloToTiddler(database, booksAll, verses, wiki);
                sbTexto.append("\n* [[" + verses.getChapter().toString() + "|" + title + "]]");
                last = current;
            }
        }

        TiddlerLivro tiddlerLivro = new TiddlerLivro(
            booksAll.getShort_name(),
            booksAll.getLong_name(),
            database.getUrl(),
            database.getTimestamp(),
            sbTexto.toString()
        );
        return wiki.addLivro(tiddlerLivro);
    }

    private String capituloToTiddler(Database database, BooksAll booksAll, Verses versesChapter, WikiBiblia wiki) {
        StringBuilder sbTexto = new StringBuilder();

        List<Verses> verseses = database.getBible().getVerses().stream().filter(v -> v.getBook_number().equals(booksAll.getBook_number()) && v.getChapter().equals(versesChapter.getChapter())).toList();
        verseses = new ArrayList<>(verseses);
        verseses.sort(new Comparator<Verses>() {
            @Override
            public int compare(Verses v1, Verses v2) {
                return v1.getChapter().compareTo(v2.getChapter());
            }
        });
        for (Verses verses : verseses) {
            String title = versiculoToTiddler(database, booksAll, versesChapter, verses, wiki);
            sbTexto.append("^^[[" + verses.getVerse().toString() + "|" + title + "]]^^{{" + title + "}}" + TiddlyWiki.LINE_BREAK);
        }

        TiddlerCapitulo tiddlerCapitulo = new TiddlerCapitulo(
            booksAll.getShort_name(),
            versesChapter.getChapter().toString(),
            database.getUrl(),
            database.getTimestamp(),
            sbTexto.toString()
        );
        return wiki.addCapitulo(tiddlerCapitulo);
    }

    private String versiculoToTiddler(Database database, BooksAll booksAll, Verses versesChapter, Verses verses, WikiBiblia wiki) {
        String numVersiculo = verses.getVerse().toString();

        TiddlerVersiculo tiddlerVersiculo = new TiddlerVersiculo(
            booksAll.getShort_name(),
            versesChapter.getChapter().toString(),
            numVersiculo,
            database.getUrl(),
            database.getTimestamp(),
            verses.getText()
        );
        return wiki.addVersiculo(tiddlerVersiculo);
    }
}
