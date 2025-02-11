package zcla71.mybible;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.URI;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Files;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import zcla71.mybible.model.Bible;
import zcla71.mybible.model.Commentaries;
import zcla71.mybible.model.Database;
import zcla71.mybible.model.bible.Books;
import zcla71.mybible.model.bible.BooksAll;
import zcla71.mybible.model.bible.Introductions;
import zcla71.mybible.model.bible.Stories;
import zcla71.mybible.model.bible.Verses;
import zcla71.sqlite.SQLiteDb;
import zcla71.tiddlywiki.TiddlyWiki;
import zcla71.wikimaker.WikiMaker;
import zcla71.wikimaker.wiki.biblia.TiddlerBiblia;
import zcla71.wikimaker.wiki.biblia.TiddlerCapitulo;
import zcla71.wikimaker.wiki.biblia.TiddlerIntroducaoBiblia;
import zcla71.wikimaker.wiki.biblia.TiddlerIntroducaoLivro;
import zcla71.wikimaker.wiki.biblia.TiddlerLivro;
import zcla71.wikimaker.wiki.biblia.TiddlerTitulo;
import zcla71.wikimaker.wiki.biblia.TiddlerVersiculo;
import zcla71.wikimaker.wiki.biblia.WikiBiblia;

// Documentação: https://docs.google.com/document/d/12rf4Pqy13qhnAW31uKkaWNTBDTtRbNW0s7cM0vcimlA/
@Slf4j
public abstract class MyBible extends WikiMaker<Database> {
    private String id;
    @Getter(AccessLevel.PROTECTED)
    private URI uri;
    private Map<Integer, TiddlerLivro> mapTiddlerLivro;

    protected MyBible(URI uri, String id) throws Exception {
        super(new Object[] { uri, id });
    }

    @Override
    protected String getId() {
        return this.id;
    }

    @Override
    protected void init(Object... arguments) {
        this.uri = (URI) arguments[0];
        this.id = (String) arguments[1];
        mapTiddlerLivro = new HashMap<>();
    }

    @Override
    protected Class<Database> getDownloadClass() {
        return Database.class;
    }

    protected File getTempDirectory() {
        return new File("./temp");
    }

    protected String getDownloadFileName(URI uri) {
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

        // Os headers não colaboram; tenta pela url.
        if (result == null) {
            result = uri.toString();
            result = result.substring(result.lastIndexOf("/") + 1);
        }

        return result;
    }

    protected void download(File file) throws IOException {
        Files.createDirectories(getTempDirectory().toPath());
        log.info("Temp file name: " + file.getAbsolutePath());
        try (
            BufferedInputStream in = new BufferedInputStream(getUri().toURL().openStream());
            FileOutputStream fos = new FileOutputStream(file);
        ) {
            byte dataBuffer[] = new byte[1024];
            int bytesRead;
            log.info("Downloading");
            while ((bytesRead = in.read(dataBuffer, 0, 1024)) != -1) {
                fos.write(dataBuffer, 0, bytesRead);
            }
            log.info("Bytes downloaded: " + file.length());
        }
    }

    protected void sqlBible(Database database, String nomeArq) throws SQLException, InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException, NoSuchFieldException {
        database.setBible(new Bible());
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

    protected void sqlCommentaries(Database database, String nomeArq) throws SQLException, InstantiationException,
            IllegalAccessException, InvocationTargetException, NoSuchMethodException, NoSuchFieldException {
        database.setCommentaries(new Commentaries());
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
                        database.getCommentaries().setInfo(info);
                        break;

                    case "commentaries":
                        Collection<zcla71.mybible.model.commentaries.Commentaries> commentaries = sqLiteDb.getData(conn, tableName, zcla71.mybible.model.commentaries.Commentaries.class);
                        database.getCommentaries().setCommentaries(commentaries);
                        break;

                    case "content_fragments":
                        Collection<zcla71.mybible.model.common.ContentFragments> contentFragments = sqLiteDb.getData(conn, tableName, zcla71.mybible.model.common.ContentFragments.class);
                        database.getCommentaries().setContentFragments(contentFragments);
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

    protected String removeExtension(String fileName) {
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

        // books / booksAll / verses
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
                    sbTexto.append(TiddlyWiki.LINE_BREAK + "* [[" + name + "|" + title + "]]");
                }
            }
        } else {
            throw new RuntimeException("Implementar livroToTiddler(Books)");
            // for (Books books : database.getBible().getBooks()) {
            //     String title = livroToTiddler(database, livro, wiki);
            //     sbTexto.append(TiddlyWiki.LINE_BREAK + "* [[" + books.getLong_name() + "|" + title + "]]");
            // }
        }

        if (database.getBible().getIntroductions() != null && database.getBible().getIntroductions().size() > 0) {
            for (Introductions introductions : database.getBible().getIntroductions()) {
                if (introductions.getBook_number() == 0) { // Introdução da Bíblia toda
                    String title = introducaoBibliaToTiddler(database, introductions, wiki);
                    sbTexto.append(TiddlyWiki.LINE_BREAK + TiddlyWiki.LINE_BREAK + "! [[Introdução|" + title + "]]" + TiddlyWiki.LINE_BREAK + "{{" + title + "}}");
                } else { // Introdução de um livro
                    BooksAll booksAll = database.getBible().getBooksAll().stream().filter(b -> b.getBook_number().equals(introductions.getBook_number())).findFirst().get();
                    String title = introducaoLivroToTiddler(database, booksAll, introductions, wiki);
                    TiddlerLivro tiddlerLivro = mapTiddlerLivro.get(booksAll.getBook_number());
                    tiddlerLivro.setTexto(tiddlerLivro.getTexto() + TiddlyWiki.LINE_BREAK + TiddlyWiki.LINE_BREAK + "! [[Introdução|" + title + "]]" + TiddlyWiki.LINE_BREAK + "{{" + title + "}}");
                }
            }
        }

        // TODO commentaries

        TiddlerBiblia tiddlerBiblia = new TiddlerBiblia(
            database.getName(),
            database.getUrl(),
            database.getTimestamp(),
            sbTexto.toString()
        );
        return wiki.setBiblia(tiddlerBiblia);
    }

    private String introducaoBibliaToTiddler(Database database, Introductions introductions, WikiBiblia wiki) {
        TiddlerIntroducaoBiblia tiddlerIntroducaoBiblia = new TiddlerIntroducaoBiblia(
            database.getUrl(),
            database.getTimestamp(),
            introductions.getIntroduction()
        );
        return wiki.setIntroducaoBiblia(tiddlerIntroducaoBiblia);
    }

    private String introducaoLivroToTiddler(Database database, BooksAll booksAll, Introductions introductions, WikiBiblia wiki) {
        TiddlerIntroducaoLivro tiddlerIntroducaoLivro = new TiddlerIntroducaoLivro(
            database.getUrl(),
            database.getTimestamp(),
            padronizaSigla(booksAll.getShort_name()),
            introductions.getIntroduction()
        );
        return wiki.addIntroducaoLivro(tiddlerIntroducaoLivro);
    }

    private String livroToTiddler(Database database, BooksAll booksAll, WikiBiblia wiki) {
        log.info("\t\t" + booksAll.getShort_name());

        StringBuilder sbTexto = new StringBuilder("! Capítulos" + TiddlyWiki.LINE_BREAK);
        Integer last = null;
        List<Verses> verseses = database.getBible().getVerses().stream().filter(v -> v.getBook_number().equals(booksAll.getBook_number())).toList();
        verseses = new ArrayList<>(verseses);
        verseses.sort(new Comparator<Verses>() {
            @Override
            public int compare(Verses v1, Verses v2) {
                return v1.getChapter().compareTo(v2.getChapter());
            }
        });
        String separator = "";
        for (Verses verses : verseses) {
            Integer current = verses.getChapter();
            if (!current.equals(last)) {
                String title = capituloToTiddler(database, booksAll, verses, wiki);
                sbTexto.append(separator + "[[" + verses.getChapter().toString() + "|" + title + "]]");
                last = current;
                // TODO Criar constante para &bull;
                separator = " &bull; ";
            }
        }

        TiddlerLivro tiddlerLivro = new TiddlerLivro(
            padronizaSigla(booksAll.getShort_name()),
            booksAll.getLong_name(),
            database.getUrl(),
            database.getTimestamp(),
            sbTexto.toString()
        );
        mapTiddlerLivro.put(booksAll.getBook_number(), tiddlerLivro);
        return wiki.addLivro(tiddlerLivro);
    }

    protected String padronizaSigla(String sigla) {
        return sigla;
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
            if (database.getBible().getStories() != null && database.getBible().getStories().size() > 0) {
                Collection<Stories> storieses = database.getBible().getStories().stream()
                        .filter(s -> s.getBook_number().equals(verses.getBook_number())
                                && s.getChapter().equals(verses.getChapter())
                                && s.getVerse().equals(verses.getVerse()))
                        .toList();
                for (Stories stories : storieses) {
                    String title = tituloToTiddler(database, booksAll, stories, wiki);
                    TiddlerTitulo titulo = (TiddlerTitulo) wiki.getTiddlerMap().get(title);
                    if (sbTexto.length() > 0) {
                        sbTexto.append(TiddlyWiki.LINE_BREAK);
                    }
                    // TODO Criar constante para 🔗
                    sbTexto.append("!".repeat(Integer.parseInt(titulo.getNivel())) +
                            " [[🔗|" + title + "]]{{" + title + "}}" +
                            TiddlyWiki.LINE_BREAK);
                }
            }

            String title = versiculoToTiddler(database, booksAll, versesChapter, verses, wiki);
            sbTexto.append("^^[[" + verses.getVerse().toString() + "|" + title + "]]^^{{" + title + "}}" + TiddlyWiki.LINE_BREAK);
        }

        TiddlerCapitulo tiddlerCapitulo = new TiddlerCapitulo(
            padronizaSigla(booksAll.getShort_name()),
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
            padronizaSigla(booksAll.getShort_name()),
            versesChapter.getChapter().toString(),
            numVersiculo,
            database.getUrl(),
            database.getTimestamp(),
            verses.getText()
        );
        return wiki.addVersiculo(tiddlerVersiculo);
    }

    private String tituloToTiddler(Database database, BooksAll booksAll, Stories stories, WikiBiblia wiki) {
        TiddlerTitulo tiddlerTitulo = new TiddlerTitulo(
            padronizaSigla(booksAll.getShort_name()),
            stories.getChapter().toString(),
            stories.getVerse().toString(),
            stories.getOrder_if_several().toString(),
            database.getUrl(),
            database.getTimestamp(),
            stories.getTitle()
        );
        return wiki.addTitulo(tiddlerTitulo);
    }
}
