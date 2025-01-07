package zcla71.wikimaker.bibliapauluscombr.biblia;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collection;

import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;
import zcla71.tiddlywiki.TiddlyWiki;
import zcla71.utils.JacksonUtils;
import zcla71.utils.RestCall;
import zcla71.utils.StringUtils;
import zcla71.wikimaker.wiki.biblia.TiddlerBiblia;
import zcla71.wikimaker.wiki.biblia.TiddlerCapitulo;
import zcla71.wikimaker.wiki.biblia.TiddlerLivro;
import zcla71.wikimaker.wiki.biblia.TiddlerVersiculo;
import zcla71.wikimaker.wiki.biblia.WikiBiblia;

@Slf4j
public class BibliaPaulusComBrBiblia {
    private static final String ID = "biblia_paulus_com_br_biblia";
    private static final String NOME = "Bíblia Sagrada Edição Pastoral";
    private static final String SITE_URL = "https://biblia.paulus.com.br/";
    private static final String BASE_API_URL = "https://biblia.paulus.com.br/api/v1/";
    private static final String JSON_DOWNLOAD_FILE_NAME = "./data/" + ID + ".json";
    private static final String WIKI_OUTPUT_FILE = "./data/" + ID + ".html";

    public BibliaPaulusComBrBiblia() throws MalformedURLException, IOException, URISyntaxException {
        log.info(ID);

        ObjectMapper objectMapper = JacksonUtils.getObjectMapperInstance();
        JacksonUtils.enableJavaTime(objectMapper);

        File jsonDownloadFile = new File(JSON_DOWNLOAD_FILE_NAME);
        Biblia biblia = null;
        if (jsonDownloadFile.exists()) {
            log.info("\tJson já gerado.");
            biblia = objectMapper.readValue(jsonDownloadFile, Biblia.class);
        } else {
            biblia = downloadBiblia();
            objectMapper.writer(JacksonUtils.getPrettyPrinter()).writeValue(jsonDownloadFile, biblia);
        }

        File wikiOutputFile = new File(WIKI_OUTPUT_FILE);
        if (wikiOutputFile.exists()) {
            log.info("\tWiki já gerado.");
            return;
        }

        WikiBiblia wiki = makeWiki(biblia);
        log.info("\tSalvando wiki");
        wiki.saveAsWiki(wikiOutputFile);
    }

    private Biblia downloadBiblia() throws MalformedURLException, IOException, URISyntaxException {
        Biblia result = new Biblia();
        result.setNome(NOME);
        result.setTestaments(downloadTestaments());

        result.setBookChildrensBooksOrChapters(new ArrayList<>());
        result.setChapters(new ArrayList<>());
        for (Testament testament : result.getTestaments().getData()) {
            Collection<TestamentParentOrChildren> testamentParentOrChildren = new ArrayList<>();
            testamentParentOrChildren.addAll(testament.getParent());
            testamentParentOrChildren.addAll(testament.getChildren());
            testamentParentOrChildren = testamentParentOrChildren.stream().filter(c -> c.getTestament_id().equals(testament.getId())).toList(); // Remove os 2 livros do NT que aparecem no AT: At e Ap
            if (testament.getId().equals(2)) { // Remove os 2 "grupos" do NT que são idênticos aos respectivos livros: At e Ap
                TestamentParentOrChildren at = testamentParentOrChildren.stream().filter(c -> c.getId().equals(61)).findFirst().get();
                TestamentParentOrChildren ap = testamentParentOrChildren.stream().filter(c -> c.getId().equals(85)).findFirst().get();
                testamentParentOrChildren = testamentParentOrChildren.stream().filter(c -> (c != at) && (c != ap)).toList();
            }
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
        result.setUrlSite(SITE_URL);
        result.setUrl(strUrlChapter);
        result.setTimestamp(LocalDateTime.now());

        return result;
    }

    private String toUrlString(String str) {
        return StringUtils.removeAcentos(str.toLowerCase()).replaceAll(" ", "-");
    }

    private WikiBiblia makeWiki(Biblia biblia) {
        log.info("\tGerando wiki");
        DateTimeFormatter dtfHuman = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

        WikiBiblia wiki = new WikiBiblia(
                biblia.getNome(),
                "Importada [[daqui|" + biblia.getTestaments().getUrlSite() + "]] em " + biblia.getTestaments().getTimestamp().format(dtfHuman) + "."
        );

        // Bíblia
        bibliaToTiddler(biblia, wiki);

        return wiki;
    }

    private String bibliaToTiddler(Biblia biblia, WikiBiblia wiki) {
        StringBuilder sbTexto = new StringBuilder("! Livros");
        Collection<BookChildrensBooksOrChapters> livros = biblia.getBookChildrensBooksOrChapters().stream().filter(bcboc -> bcboc.getData().getChapters() != null).toList();
        for (BookChildrensBooksOrChapters livro : livros) {
            String title = livroToTiddler(biblia, livro, wiki);
            sbTexto.append("\n* [[" + livro.getData().getName() + "|" + title + "]]");
        }

        TiddlerBiblia tiddlerBiblia = new TiddlerBiblia(
            biblia.getNome(),
            biblia.getTestaments().getUrl(),
            biblia.getTestaments().getTimestamp(),
            sbTexto.toString()
        );
        return wiki.setBiblia(tiddlerBiblia);
    }

    private String livroToTiddler(Biblia biblia, BookChildrensBooksOrChapters livro, WikiBiblia wiki) {
        Testament testament = biblia.getTestaments().getData().stream().filter(t -> t.getName().equals(livro.getData().getTestament())).findFirst().get();
        TestamentParentOrChildren tpoc = testament.getChildren().stream().filter(c -> c.getName().equals(livro.getData().getName())).findFirst().get();
        String sigla = tpoc.getAbbreviation();
        log.info("\t\t" + sigla);

        StringBuilder sbTexto = new StringBuilder("! Capítulos");
        Collection<Chapter> chapters = biblia.getChapters().stream().filter(c -> c.getBookChildren().equals(livro.getData().getName())).toList();
        for (Chapter capitulo : chapters) {
            String title = capituloToTiddler(sigla, capitulo, wiki);
            sbTexto.append("\n* [[" + capitulo.getChapter() + "|" + title + "]]");
        }

        TiddlerLivro tiddlerLivro = new TiddlerLivro(
            sigla,
            livro.getData().getName(),
            livro.getUrl(),
            livro.getTimestamp(),
            sbTexto.toString()
        );
        return wiki.addLivro(tiddlerLivro);
    }

    private String capituloToTiddler(String sigla, Chapter capitulo, WikiBiblia wiki) {
        StringBuilder sbTexto = new StringBuilder();

        for (ChapterVersicle versiculo : capitulo.getVersicles()) {
            String title = versiculoToTiddler(sigla, capitulo, versiculo, wiki);
            sbTexto.append("^^[[" + versiculo.getValue() + "|" + title + "]]^^{{" + title + "}}" + TiddlyWiki.LINE_BREAK);
        }

        TiddlerCapitulo tiddlerCapitulo = new TiddlerCapitulo(
            sigla,
            capitulo.getChapter(),
            capitulo.getUrl(),
            capitulo.getTimestamp(),
            sbTexto.toString()
        );
        return wiki.addCapitulo(tiddlerCapitulo);
    }

    private String versiculoToTiddler(String sigla, Chapter capitulo, ChapterVersicle versiculo, WikiBiblia wiki) {
        TiddlerVersiculo tiddlerVersiculo = new TiddlerVersiculo(
            sigla,
            capitulo.getChapter(),
            versiculo.getValue(),
            capitulo.getUrl(),
            capitulo.getTimestamp(),
            versiculo.getText()
        );
        return wiki.addVersiculo(tiddlerVersiculo);
    }
}
