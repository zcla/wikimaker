package zcla71.wikimaker.a12com.biblia;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
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
import zcla71.wikimaker.wiki.biblia.TiddlerBiblia;
import zcla71.wikimaker.wiki.biblia.TiddlerCapitulo;
import zcla71.wikimaker.wiki.biblia.TiddlerLivro;
import zcla71.wikimaker.wiki.biblia.TiddlerVersiculo;
import zcla71.wikimaker.wiki.biblia.WikiBiblia;

@Slf4j
public class A12ComBiblia {
    private static final String ID = "a12_com_biblia";
    private static final String NOME = "Bíblia de Aparecida";
    private static final String SITE_URL = "https://www.a12.com/biblia";
    private static final String BASE_API_URL = "https://www.a12.com/bible-api/";
    private static final String JSON_DOWNLOAD_FILE_NAME = "./data/" + ID + ".json";
    private static final Map<String, String> MAP_LIVRO = Map.ofEntries(
            Map.entry("genesis", "Gn"),
            Map.entry("exodo", "Ex"),
            Map.entry("levitico", "Lv"),
            Map.entry("numeros", "Nm"),
            Map.entry("deuteronomio", "Dt"),
            Map.entry("josue", "Js"),
            Map.entry("juizes", "Jz"),
            Map.entry("rute", "Rt"),
            Map.entry("i-samuel", "1Sm"),
            Map.entry("ii-samuel", "2Sm"),
            Map.entry("i-reis", "1Rs"),
            Map.entry("ii-reis", "2Rs"),
            Map.entry("i-cronicas", "1Cr"),
            Map.entry("ii-cronicas", "2Cr"),
            Map.entry("esdras", "Esd"),
            Map.entry("neemias", "Ne"),
            Map.entry("tobias", "Tb"),
            Map.entry("judite", "Jt"),
            Map.entry("ester", "Est"),
            Map.entry("i-macabeus", "1Mc"),
            Map.entry("ii-macabeus", "2Mc"),
            Map.entry("jo", "Jó"),
            Map.entry("salmos", "Sl"),
            Map.entry("proverbios", "Pr"),
            Map.entry("eclesiastes", "Ecl"),
            Map.entry("cantico-dos-canticos", "Ct"),
            Map.entry("sabedoria", "Sb"),
            Map.entry("eclesiastico", "Eclo"),
            Map.entry("isaias", "Is"),
            Map.entry("jeremias", "Jr"),
            Map.entry("lamentacoes", "Lm"),
            Map.entry("baruc", "Br"),
            Map.entry("ezequiel", "Ez"),
            Map.entry("daniel", "Dn"),
            Map.entry("oseias", "Os"),
            Map.entry("joel", "Jl"),
            Map.entry("amos", "Am"),
            Map.entry("abdias", "Ab"),
            Map.entry("jonas", "Jn"),
            Map.entry("miqueias", "Mq"),
            Map.entry("naum", "Na"),
            Map.entry("habacuc", "Hab"),
            Map.entry("sofonias", "Sf"),
            Map.entry("ageu", "Ag"),
            Map.entry("zacarias", "Zc"),
            Map.entry("malaquias", "Ml"),
            Map.entry("sao-mateus", "Mt"),
            Map.entry("sao-marcos", "Mc"),
            Map.entry("sao-lucas", "Lc"),
            Map.entry("sao-joao", "Jo"),
            Map.entry("atos-dos-apostolos", "At"),
            Map.entry("romanos", "Rm"),
            Map.entry("i-corintios", "1Cor"),
            Map.entry("ii-corintios", "2Cor"),
            Map.entry("galatas", "Gl"),
            Map.entry("efesios", "Ef"),
            Map.entry("filipenses", "Fl"),
            Map.entry("colossenses", "Cl"),
            Map.entry("i-tessalonicenses", "1Ts"),
            Map.entry("ii-tessalonicenses", "2Ts"),
            Map.entry("i-timoteo", "1Tm"),
            Map.entry("ii-timoteo", "2Tm"),
            Map.entry("tito", "Tt"),
            Map.entry("filemon", "Fm"),
            Map.entry("hebreus", "Hb"),
            Map.entry("sao-tiago", "Tg"),
            Map.entry("i-sao-pedro", "1Pd"),
            Map.entry("ii-sao-pedro", "2Pd"),
            Map.entry("i-sao-joao", "1Jo"),
            Map.entry("ii-sao-joao", "2Jo"),
            Map.entry("iii-sao-joao", "3Jo"),
            Map.entry("sao-judas", "Jd"),
            Map.entry("apocalipse", "Ap"));
    private static final String WIKI_OUTPUT_FILE = "./data/" + ID + ".html";

    public A12ComBiblia() throws StreamReadException, DatabindException, IOException, URISyntaxException {
        log.info(ID);

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
        objectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
        DefaultPrettyPrinter prettyPrinter = new DefaultPrettyPrinter();
        prettyPrinter.indentArraysWith(DefaultIndenter.SYSTEM_LINEFEED_INSTANCE);

        File jsonDownloadFile = new File(JSON_DOWNLOAD_FILE_NAME);
        Biblia biblia = null;
        if (jsonDownloadFile.exists()) {
            log.info("\tJson já gerado.");
            biblia = objectMapper.readValue(jsonDownloadFile, Biblia.class);
        } else {
            biblia = downloadBiblia(objectMapper, jsonDownloadFile);
            objectMapper.writer(prettyPrinter).writeValue(jsonDownloadFile, biblia);
        }

        File wikiOutputFile = new File(WIKI_OUTPUT_FILE);
        // if (wikiOutputFile.exists()) {
        //     log.info("\tWiki já gerado.");
        //     return;
        // }

        WikiBiblia wiki = makeWiki(biblia);
        log.info("\tSalvando wiki");
        wiki.save(wikiOutputFile);

    }

    private Biblia downloadBiblia(ObjectMapper objectMapper, File jsonDownloadFile) throws URISyntaxException, StreamReadException, DatabindException, IOException {
        log.info("\tDownload");

        String strUrlBooks = BASE_API_URL + "get_books";
        RestCall restBooks = new RestCall(strUrlBooks);
        Biblia result = restBooks.postJson(Biblia.class);
        result.setData(result.getData().stream().filter(l -> l.getTitle() != null).toList());
        result.setNome(NOME);
        result.setUrlSite(SITE_URL);
        result.setUrl(strUrlBooks);
        result.setTimestamp(LocalDateTime.now());

        for (Livro livro : result.getData()) {
            String sigla = MAP_LIVRO.get(livro.getSlug());
            log.info("\t\t" + sigla);
            livro.setSigla(sigla);
            livro.setCapitulos(new ArrayList<>());
            for (Integer numero = 1; numero <= livro.getChaptersTotal(); numero++) {
                log.info("\t\t\t" + numero);
                String strUrlVersicles = BASE_API_URL + "get_versicles";
                RestCall restVersicles = new RestCall(strUrlVersicles);
                Map<String, String> data = new HashMap<>();
                data.put("book_slug", livro.getSlug());
                data.put("chapter_order", numero.toString());
                restVersicles.setData(data);
                Capitulo capitulo = restVersicles.postJson(Capitulo.class);
                capitulo.setNumero(numero);
                capitulo.setUrl(strUrlVersicles);
                capitulo.setTimestamp(LocalDateTime.now());
                livro.getCapitulos().add(capitulo);
            }
        }

        return result;
    }

    private WikiBiblia makeWiki(Biblia biblia) throws IOException {
        log.info("\tGerando wiki");
        DateTimeFormatter dtfHuman = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

        WikiBiblia wiki = new WikiBiblia(
                biblia.getNome(),
                "Importada [[daqui|" + biblia.getUrlSite() + "]] em " + biblia.getTimestamp().format(dtfHuman) + "."
        );

        // Bíblia
        bibliaToTiddler(biblia, wiki);

        return wiki;
    }

    private String bibliaToTiddler(Biblia biblia, WikiBiblia wiki) {
        StringBuilder sbTexto = new StringBuilder("! Livros");
        for (Livro livro : biblia.getData()) {
            String title = livroToTiddler(biblia, livro, wiki);
            sbTexto.append("\n* [[" + livro.getTitle() + "|" + title + "]]");
        }

        TiddlerBiblia tiddlerBiblia = new TiddlerBiblia(
            biblia.getNome(),
            biblia.getUrl(),
            biblia.getTimestamp(),
            sbTexto.toString()
        );
        return wiki.setBiblia(tiddlerBiblia);
    }

    private String livroToTiddler(Biblia biblia, Livro livro, WikiBiblia wiki) {
        log.info("\t\t" + livro.getSigla());

        StringBuilder sbTexto = new StringBuilder("! Capítulos");
        for (Capitulo capitulo : livro.getCapitulos()) {
            String title = capituloToTiddler(livro, capitulo, wiki);
            sbTexto.append("\n* [[" + capitulo.getNumero() + "|" + title + "]]");
        }

        TiddlerLivro tiddlerLivro = new TiddlerLivro(
            livro.getSigla(),
            livro.getTitle(),
            biblia.getUrl(),
            biblia.getTimestamp(),
            sbTexto.toString()
        );
        return wiki.addLivro(tiddlerLivro);
    }

    private String capituloToTiddler(Livro livro, Capitulo capitulo, WikiBiblia wiki) {
        StringBuilder sbTexto = new StringBuilder();

        for (Versiculo versiculo : capitulo.getData()) {
            String title = versiculoToTiddler(livro, capitulo, versiculo, wiki);
            if ((versiculo.getVerseNumber() == 1) && (versiculo.getChapterTitle() != null) && versiculo.getChapterTitle().length() > 0) {
                sbTexto.append("! " + versiculo.getChapterTitle() + TiddlyWiki.LINE_BREAK);
            }
            if ((versiculo.getVerseTitle() != null) && versiculo.getVerseTitle().length() > 0) {
                sbTexto.append(TiddlyWiki.LINE_BREAK + "!! " + versiculo.getVerseTitle() + TiddlyWiki.LINE_BREAK);
            }
            sbTexto.append("^^[[" + versiculo.getVerseNumber() + "|" + title + "]]^^{{" + title + "}}" + TiddlyWiki.LINE_BREAK);
        }

        TiddlerCapitulo tiddlerCapitulo = new TiddlerCapitulo(
            livro.getSigla(),
            capitulo.getNumero().toString(),
            capitulo.getUrl(),
            capitulo.getTimestamp(),
            sbTexto.toString()
        );
        return wiki.addCapitulo(tiddlerCapitulo);
    }

    private String versiculoToTiddler(Livro livro, Capitulo capitulo, Versiculo versiculo, WikiBiblia wiki) {
        String numVersiculo = versiculo.getVerseNumber().toString();

        TiddlerVersiculo tiddlerVersiculo = new TiddlerVersiculo(
            livro.getSigla(),
            capitulo.getNumero().toString(),
            numVersiculo,
            capitulo.getUrl(),
            capitulo.getTimestamp(),
            versiculo.getVerseContent()
        );
        return wiki.addVersiculo(tiddlerVersiculo);
    }
}