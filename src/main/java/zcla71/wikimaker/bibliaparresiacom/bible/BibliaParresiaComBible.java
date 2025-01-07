package zcla71.wikimaker.bibliaparresiacom.bible;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

import com.fasterxml.jackson.core.exc.StreamReadException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DatabindException;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;
import zcla71.tiddlywiki.TiddlyWiki;
import zcla71.utils.JacksonUtils;
import zcla71.wikimaker.wiki.biblia.TiddlerBiblia;
import zcla71.wikimaker.wiki.biblia.TiddlerCapitulo;
import zcla71.wikimaker.wiki.biblia.TiddlerLivro;
import zcla71.wikimaker.wiki.biblia.TiddlerVersiculo;
import zcla71.wikimaker.wiki.biblia.WikiBiblia;

@Slf4j
public class BibliaParresiaComBible {
    private static final String ID = "biblia_parresia_com_bible";
    private static final String NOME = "Bíblia Ave-Maria";
    private static final String SITE_URL = "https://claretianos.com.br/biblia-ave-maria-online/";
    private static final String BASE_API_URL = "https://biblia.parresia.com/wp-json/bible/v2/";
    private static final String JSON_DOWNLOAD_FILE_NAME = "./data/download/" + ID + ".json";
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
            Map.entry("jo", "Jó"),
            Map.entry("salmos", "Sl"),
            Map.entry("i-macabeus", "1Mc"),
            Map.entry("ii-macabeus", "2Mc"),
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

    public BibliaParresiaComBible() throws StreamReadException, DatabindException, IOException, URISyntaxException {
        log.info(ID);

        ObjectMapper objectMapper = JacksonUtils.getObjectMapperInstance();
        JacksonUtils.enableJavaTime(objectMapper);

        File jsonDownloadFile = new File(JSON_DOWNLOAD_FILE_NAME);
        Biblia biblia = null;
        if (jsonDownloadFile.exists()) {
            log.info("\tJson já gerado.");
            biblia = objectMapper.readValue(jsonDownloadFile, Biblia.class);
        } else {
            biblia = downloadBiblia(objectMapper);
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

    private Biblia downloadBiblia(ObjectMapper objectMapper) throws URISyntaxException, StreamReadException, DatabindException, IOException {
        log.info("\tDownload");

        String strUrlBooks = BASE_API_URL + "books";
        Biblia result = new Biblia(NOME, SITE_URL, strUrlBooks);

        URL urlBooks = new URI(strUrlBooks).toURL();
        Collection<Livro> livros = objectMapper.readValue(urlBooks, new TypeReference<Collection<Livro>>(){});
        result.setLivros(livros);

        for (Livro livro : livros) {
            String sigla = MAP_LIVRO.get(livro.getSlug());
            log.info("\t\t" + sigla);
            livro.setSigla(sigla);
            livro.setCapitulos(new ArrayList<>());
            for (int i = 1; i <= livro.getChapterCount(); i++) {
                log.info("\t\t\t" + i);
                String strUrlChapter = BASE_API_URL + "chapter/" + livro.getSlug() + "_" + i + "/";
                Capitulo capitulo = new Capitulo(i, strUrlChapter);
                livro.getCapitulos().add(capitulo);

                URL urlChapter = new URI(strUrlChapter).toURL();
                Collection<Versiculo> versiculos = objectMapper.readValue(urlChapter, new TypeReference<Collection<Versiculo>>(){});
                capitulo.setVersiculos(versiculos);
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
        for (Livro livro : biblia.getLivros()) {
            String title = livroToTiddler(biblia, livro, wiki);
            sbTexto.append("\n* [[" + livro.getChapter() + "|" + title + "]]");
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
            livro.getChapter(),
            biblia.getUrl(),
            biblia.getTimestamp(),
            sbTexto.toString()
        );
        return wiki.addLivro(tiddlerLivro);
    }

    private String capituloToTiddler(Livro livro, Capitulo capitulo, WikiBiblia wiki) {
        StringBuilder sbTexto = new StringBuilder();

        for (Versiculo versiculo : capitulo.getVersiculos()) {
            String title = versiculoToTiddler(livro, capitulo, versiculo, wiki);
            sbTexto.append("^^[[" + versiculo.getNumber() + "|" + title + "]]^^{{" + title + "}}" + TiddlyWiki.LINE_BREAK);
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
        String livroCapitulo = livro.getSigla() + " " + capitulo.getNumero();
        String numVersiculo = versiculo.getNumber();
        // Fix: 2Rs 3,9 vem numerado como 2Rs 3,10
        if ("2Rs 3".equals(livroCapitulo) && "10".equals(numVersiculo) && versiculo.getText().matches(".+marcha.+")) {
            numVersiculo = "9";
        }
        // Fix: At 5,1 vem numerado como At 5,2
        if ("At 5".equals(livroCapitulo) && "2".equals(numVersiculo) && versiculo.getText().matches(".+Ananias.+")) {
            numVersiculo = "1";
        }

        TiddlerVersiculo tiddlerVersiculo = new TiddlerVersiculo(
            livro.getSigla(),
            capitulo.getNumero().toString(),
            numVersiculo,
            capitulo.getUrl(),
            capitulo.getTimestamp(),
            versiculo.getText()
        );
        return wiki.addVersiculo(tiddlerVersiculo);
    }
}
