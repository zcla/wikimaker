package zcla71.wikimaker.bibliapauluscombr.biblia;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import com.fasterxml.jackson.core.exc.StreamReadException;
import com.fasterxml.jackson.core.util.DefaultIndenter;
import com.fasterxml.jackson.core.util.DefaultPrettyPrinter;
import com.fasterxml.jackson.databind.DatabindException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import lombok.extern.slf4j.Slf4j;
import zcla71.tiddlywiki.TiddlyWiki;
import zcla71.utils.RestCall;
import zcla71.utils.StringUtils;
import zcla71.wikimaker.wiki.biblia.TiddlerBiblia;
import zcla71.wikimaker.wiki.biblia.TiddlerCapitulo;
import zcla71.wikimaker.wiki.biblia.TiddlerLivro;
import zcla71.wikimaker.wiki.biblia.TiddlerVersiculo;
import zcla71.wikimaker.wiki.biblia.WikiBiblia;

@Slf4j
public class BibliaPaulusComBrBiblia {
public static void main(String[] args) throws MalformedURLException, IOException, URISyntaxException {
new BibliaPaulusComBrBiblia();
}
    private static final String ID = "biblia_paulus_com_br_biblia";
    private static final String NOME = "Bíblia Sagrada Edição Pastoral";
    private static final String SITE_URL = "https://biblia.paulus.com.br/";
    private static final String BASE_API_URL = "https://biblia.paulus.com.br/api/v1/";
    private static final String JSON_DOWNLOAD_FILE_NAME = "./data/" + ID + ".json";
    private static final String WIKI_OUTPUT_FILE = "./data/" + ID + ".html";

    public BibliaPaulusComBrBiblia() throws MalformedURLException, IOException, URISyntaxException {
        log.info(ID);

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
        objectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
        DefaultPrettyPrinter prettyPrinter = new DefaultPrettyPrinter();
        prettyPrinter.indentArraysWith(DefaultIndenter.SYSTEM_LINEFEED_INSTANCE);

        File jsonDownloadFile = new File(JSON_DOWNLOAD_FILE_NAME);
        Biblia biblia = null;
        // if (jsonDownloadFile.exists()) {
        //     log.info("\tJson já gerado.");
        //     testaments = objectMapper.readValue(jsonDownloadFile, Testaments.class);
        // } else {
            biblia = downloadBiblia();
            objectMapper.writer(prettyPrinter).writeValue(jsonDownloadFile, biblia);
        // }

        // File wikiOutputFile = new File(WIKI_OUTPUT_FILE);
        // if (wikiOutputFile.exists()) {
        //     log.info("\tWiki já gerado.");
        //     return;
        // }

        // WikiBiblia wiki = makeWiki(biblia);
        // log.info("\tSalvando wiki");
        // wiki.save(wikiOutputFile);
    }

    private Biblia downloadBiblia() throws MalformedURLException, IOException, URISyntaxException {
        Biblia result = new Biblia();
        result.setTestaments(downloadTestaments());

        result.setBookChildrensBooksOrChapters(new ArrayList<>());
        result.setChapters(new ArrayList<>());
        for (Testament testament : result.getTestaments().getData()) {
            Collection<TestamentParentOrChildren> testamentParentOrChildren = new ArrayList<>();
            testamentParentOrChildren.addAll(testament.getParent());
            testamentParentOrChildren.addAll(testament.getChildren());
            for (TestamentParentOrChildren tpoc : testamentParentOrChildren) {
                BookChildrensBooksOrChapters bcboc = downloadTestamentParentOrChildren(tpoc);
                result.getBookChildrensBooksOrChapters().add(bcboc);
                if (bcboc.getData().getChapters() != null) {
                    for (String chapter : bcboc.getData().getChapters().values()) {
                        result.getChapters().add(downloadChapter(bcboc, chapter));
                    }
                }
            }
        }

        return result;
    }

    private Testaments downloadTestaments() throws MalformedURLException, IOException, URISyntaxException {
        log.info("\tDownload");
        log.info("\t\ttestaments");

        String strUrlTestaments = BASE_API_URL + "testaments";
        RestCall restTestaments = new RestCall(strUrlTestaments);
        Testaments result = restTestaments.getJson(Testaments.class);
        result.setNome(NOME);
        result.setUrlSite(SITE_URL);
        result.setUrl(strUrlTestaments);
        result.setTimestamp(LocalDateTime.now());

        return result;
    }

    private BookChildrensBooksOrChapters downloadTestamentParentOrChildren(TestamentParentOrChildren tpoc) throws MalformedURLException, IOException, URISyntaxException {
        String urlBookId = toUrlString(tpoc.getName());
        log.info("\t\tchildrens-books-or-chapters: " + tpoc.getName() + " (" + urlBookId + ")");
        String strUrlChildrensBooksOrChapters = BASE_API_URL + "books/" + urlBookId + "/childrens-books-or-chapters";
        RestCall restChildrensBooksOrChapters = new RestCall(strUrlChildrensBooksOrChapters);
        BookChildrensBooksOrChapters result = restChildrensBooksOrChapters.getJson(BookChildrensBooksOrChapters.class);
        result.setNome(NOME);
        result.setUrlSite(SITE_URL);
        result.setUrl(strUrlChildrensBooksOrChapters);
        result.setTimestamp(LocalDateTime.now());

        return result;
    }

    private Chapter downloadChapter(BookChildrensBooksOrChapters bcboc, String chapter) throws MalformedURLException, IOException, URISyntaxException {
        String book = bcboc.getData().getParent();
        if ((book == null) && (("ATOS DOS APÓSTOLOS".equals(bcboc.getData().getName())) || ("APOCALIPSE DE SÃO JOÃO".equals(bcboc.getData().getName())))) { // bug nos dados que vêm da api
            book = bcboc.getData().getName();
        }
        String urlBookId = "bibles/" + toUrlString(bcboc.getData().getBible()) + "/testaments/" + toUrlString(bcboc.getData().getTestament()) + "/books/" + toUrlString(book) + "/children/" + toUrlString(bcboc.getData().getName()) + "/chapters/" + chapter;
        log.info("\t\tchapter: " + urlBookId);
        String strUrlChapter = BASE_API_URL + urlBookId;
        RestCall restChapter = new RestCall(strUrlChapter);
        Chapter result = restChapter.getJson(Chapter.class);
        result.setNome(NOME);
        result.setUrlSite(SITE_URL);
        result.setUrl(strUrlChapter);
        result.setTimestamp(LocalDateTime.now());

        return result;
    }

    private String toUrlString(String str) {
        return StringUtils.removeAcentos(str.toLowerCase()).replaceAll(" ", "-");
    }

    // private WikiBiblia makeWiki(Biblia biblia) throws IOException {
    //     log.info("\tGerando wiki");
    //     DateTimeFormatter dtfHuman = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

    //     WikiBiblia wiki = new WikiBiblia(
    //             biblia.getNome(),
    //             "Importada [[daqui|" + biblia.getUrlSite() + "]] em " + biblia.getTimestamp().format(dtfHuman) + "."
    //     );

    //     // Bíblia
    //     bibliaToTiddler(biblia, wiki);

    //     return wiki;
    // }

    // private String bibliaToTiddler(Biblia biblia, WikiBiblia wiki) {
    //     StringBuilder sbTexto = new StringBuilder("! Livros");
    //     for (Livro livro : biblia.getData()) {
    //         String title = livroToTiddler(biblia, livro, wiki);
    //         sbTexto.append("\n* [[" + livro.getTitle() + "|" + title + "]]");
    //     }

    //     TiddlerBiblia tiddlerBiblia = new TiddlerBiblia(
    //         biblia.getNome(),
    //         biblia.getUrl(),
    //         biblia.getTimestamp(),
    //         sbTexto.toString()
    //     );
    //     return wiki.setBiblia(tiddlerBiblia);
    // }

    // private String livroToTiddler(Biblia biblia, Livro livro, WikiBiblia wiki) {
    //     log.info("\t\t" + livro.getSigla());

    //     StringBuilder sbTexto = new StringBuilder("! Capítulos");
    //     for (Capitulo capitulo : livro.getCapitulos()) {
    //         String title = capituloToTiddler(livro, capitulo, wiki);
    //         sbTexto.append("\n* [[" + capitulo.getNumero() + "|" + title + "]]");
    //     }

    //     TiddlerLivro tiddlerLivro = new TiddlerLivro(
    //         livro.getSigla(),
    //         livro.getTitle(),
    //         biblia.getUrl(),
    //         biblia.getTimestamp(),
    //         sbTexto.toString()
    //     );
    //     return wiki.addLivro(tiddlerLivro);
    // }

    // private String capituloToTiddler(Livro livro, Capitulo capitulo, WikiBiblia wiki) {
    //     StringBuilder sbTexto = new StringBuilder();

    //     for (Versiculo versiculo : capitulo.getData()) {
    //         String title = versiculoToTiddler(livro, capitulo, versiculo, wiki);
    //         if ((versiculo.getVerseNumber() == 1) && (versiculo.getChapterTitle() != null) && versiculo.getChapterTitle().length() > 0) {
    //             sbTexto.append("! " + versiculo.getChapterTitle() + TiddlyWiki.LINE_BREAK);
    //         }
    //         if ((versiculo.getVerseTitle() != null) && versiculo.getVerseTitle().length() > 0) {
    //             sbTexto.append(TiddlyWiki.LINE_BREAK + "!! " + versiculo.getVerseTitle() + TiddlyWiki.LINE_BREAK);
    //         }
    //         sbTexto.append("^^[[" + versiculo.getVerseNumber() + "|" + title + "]]^^{{" + title + "}}" + TiddlyWiki.LINE_BREAK);
    //     }

    //     TiddlerCapitulo tiddlerCapitulo = new TiddlerCapitulo(
    //         livro.getSigla(),
    //         capitulo.getNumero().toString(),
    //         capitulo.getUrl(),
    //         capitulo.getTimestamp(),
    //         sbTexto.toString()
    //     );
    //     return wiki.addCapitulo(tiddlerCapitulo);
    // }

    // private String versiculoToTiddler(Livro livro, Capitulo capitulo, Versiculo versiculo, WikiBiblia wiki) {
    //     String numVersiculo = versiculo.getVerseNumber().toString();

    //     TiddlerVersiculo tiddlerVersiculo = new TiddlerVersiculo(
    //         livro.getSigla(),
    //         capitulo.getNumero().toString(),
    //         numVersiculo,
    //         capitulo.getUrl(),
    //         capitulo.getTimestamp(),
    //         versiculo.getVerseContent()
    //     );
    //     return wiki.addVersiculo(tiddlerVersiculo);
    // }
}
