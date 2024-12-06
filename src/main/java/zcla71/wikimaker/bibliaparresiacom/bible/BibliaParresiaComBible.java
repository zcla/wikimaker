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
import com.fasterxml.jackson.core.util.DefaultIndenter;
import com.fasterxml.jackson.core.util.DefaultPrettyPrinter;
import com.fasterxml.jackson.databind.DatabindException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import lombok.extern.slf4j.Slf4j;
import zcla71.tiddlywiki.Tiddler;
import zcla71.tiddlywiki.TiddlyWiki;

@Slf4j
public class BibliaParresiaComBible {
public static void main(String[] args) throws StreamReadException, DatabindException, IOException, URISyntaxException {
new BibliaParresiaComBible();
}
    private static final String ID = "biblia_parresia_com_bible";
    private static final String NOME = "Bíblia Ave-Maria";
    private static final String SITE_URL = "https://claretianos.com.br/biblia-ave-maria-online/";
    private static final String BASE_API_URL = "https://biblia.parresia.com/wp-json/bible/v2/";
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
    private static final String WIKI_EMPTY_FILE = "./data/tiddlywiki_empty.html";
    private static final String WIKI_OUTPUT_FILE = "./data/" + ID + ".html";

    public BibliaParresiaComBible() throws StreamReadException, DatabindException, IOException, URISyntaxException {
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
        if (wikiOutputFile.exists()) {
            log.info("\tWiki já gerado.");
            return;
        }

        File wikiEmptyFile = new File(WIKI_EMPTY_FILE);
        if (!wikiEmptyFile.exists()) {
            log.error("\tWiki vazio não encontrado.");
            return;
        }
        makeWiki(biblia, wikiEmptyFile, wikiOutputFile);
    }

    private Biblia downloadBiblia(ObjectMapper objectMapper, File jsonDownloadFile) throws URISyntaxException, StreamReadException, DatabindException, IOException {
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

    private void makeWiki(Biblia biblia, File wikiEmptyFile, File wikiOutputFile) throws IOException {
        log.info("\tMaking wiki");
        DateTimeFormatter dtfHuman = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

        TiddlyWiki tiddlyWiki = new TiddlyWiki(wikiEmptyFile);
        tiddlyWiki.setSiteTitle(biblia.getNome());
        tiddlyWiki.setSiteSubtitle("Importada [[daqui|" + biblia.getUrlSite() + "]] em " + biblia.getTimestamp().format(dtfHuman) + ".");

        // Bíblia
        bibliaToTiddler(biblia, tiddlyWiki);

        tiddlyWiki.save(wikiOutputFile);
    }

    private void bibliaToTiddler(Biblia biblia, TiddlyWiki tiddlyWiki) {
        Tiddler tiddlerBiblia = new Tiddler("Bíblia");
        tiddlerBiblia.getCustomProperties().put("nome", biblia.getNome());
        tiddlerBiblia.getCustomProperties().put("url", biblia.getUrl());
        tiddlerBiblia.getCustomProperties().put("timestamp", biblia.getTimestamp().format(TiddlyWiki.DATE_TIME_FORMATTER_TIDDLYWIKI));
        tiddlerBiblia.setText("! Livros");
        for (Livro livro : biblia.getLivros()) {
            tiddlerBiblia.setText(tiddlerBiblia.getText() + "\n* [[" + livro.getChapter() + "|" + livro.getSigla() + "]]");
            livroToTiddler(biblia, livro, tiddlyWiki);
        }
        tiddlyWiki.insert(tiddlerBiblia);
        tiddlyWiki.setDefaultTiddlers(tiddlerBiblia.getTitle());
    }

    private void livroToTiddler(Biblia biblia, Livro livro, TiddlyWiki tiddlyWiki) {
        log.info("\t\t" + livro.getSigla());
        Tiddler tiddlerLivro = new Tiddler(livro.getSigla());
        tiddlerLivro.setTags("Livro");
        tiddlerLivro.getCustomProperties().put("sigla", livro.getSigla());
        tiddlerLivro.getCustomProperties().put("nome", livro.getChapter());
        tiddlerLivro.getCustomProperties().put("url", biblia.getUrl());
        tiddlerLivro.getCustomProperties().put("timestamp", biblia.getTimestamp().format(TiddlyWiki.DATE_TIME_FORMATTER_TIDDLYWIKI));
        tiddlerLivro.setText("! Capítulos");
        for (Capitulo capitulo : livro.getCapitulos()) {
            tiddlerLivro.setText(tiddlerLivro.getText() + "\n* [[" + capitulo.getNumero() + "|" + livro.getSigla() + " " + capitulo.getNumero() + "]]");
            capituloToTiddler(livro, capitulo, tiddlyWiki);
        }
        tiddlyWiki.insert(tiddlerLivro);
    }

    private void capituloToTiddler(Livro livro, Capitulo capitulo, TiddlyWiki tiddlyWiki) {
        Tiddler tiddlerCapitulo = new Tiddler(livro.getSigla() + " " + capitulo.getNumero());
        tiddlerCapitulo.setTags("Capítulo");
        tiddlerCapitulo.getCustomProperties().put("livro", "[[" + livro.getSigla() + "]]");
        tiddlerCapitulo.getCustomProperties().put("numero", capitulo.getNumero().toString());
        tiddlerCapitulo.getCustomProperties().put("url", capitulo.getUrl());
        tiddlerCapitulo.getCustomProperties().put("timestamp", capitulo.getTimestamp().format(TiddlyWiki.DATE_TIME_FORMATTER_TIDDLYWIKI));
        tiddlerCapitulo.setText("");
        for (Versiculo versiculo : capitulo.getVersiculos()) {
            Tiddler tiddlerVersiculo = versiculoToTiddler(livro, capitulo, versiculo, tiddlyWiki);
            String numVersiculo = tiddlerVersiculo.getCustomProperties().get("numero");
            tiddlerCapitulo.setText(tiddlerCapitulo.getText() + "^^" + numVersiculo + "^^{{" + tiddlerVersiculo.getTitle() + "}}" + TiddlyWiki.LINE_BREAK);
        }
        tiddlyWiki.insert(tiddlerCapitulo);
    }

    private Tiddler versiculoToTiddler(Livro livro, Capitulo capitulo, Versiculo versiculo, TiddlyWiki tiddlyWiki) {
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
        Tiddler tiddlerVersiculo = new Tiddler(livroCapitulo + "," + numVersiculo);
        tiddlerVersiculo.setTags("Versículo");
        tiddlerVersiculo.getCustomProperties().put("livro", "[[" + livro.getSigla() + "]]");
        tiddlerVersiculo.getCustomProperties().put("capitulo", capitulo.getNumero().toString());
        tiddlerVersiculo.getCustomProperties().put("numero", numVersiculo);
        tiddlerVersiculo.getCustomProperties().put("url", capitulo.getUrl());
        tiddlerVersiculo.getCustomProperties().put("timestamp", capitulo.getTimestamp().format(TiddlyWiki.DATE_TIME_FORMATTER_TIDDLYWIKI));
        tiddlerVersiculo.setText(versiculo.getText());
        tiddlyWiki.insert(tiddlerVersiculo);
        return tiddlerVersiculo;
    }
}
