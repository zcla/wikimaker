package zcla71.wikimaker.bibliajerusalem;

import java.io.File;
import java.io.IOException;
import java.net.SocketTimeoutException;
import java.time.format.DateTimeFormatter;
import java.util.Map;

import org.jsoup.HttpStatusException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.fasterxml.jackson.core.util.DefaultIndenter;
import com.fasterxml.jackson.core.util.DefaultPrettyPrinter;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import lombok.extern.slf4j.Slf4j;
import zcla71.tiddlywiki.Tiddler;
import zcla71.tiddlywiki.TiddlyWiki;

@Slf4j
public class BibliaJerusalem {
    // TODO Só pra acelerar no desenvolvimento; remover ao final.
    public static void main(String[] args) throws IOException {
        new BibliaJerusalem();
    }

    private static final String NOME = "Bíblia de Jerusalém";
    private static final String BASE_URL = "https://liturgiadashoras.online/biblia/biblia-jerusalem/";
    private static final String REGEX_LIVRO = "^https:\\/\\/liturgiadashoras\\.online\\/biblia\\/biblia-jerusalem\\/([^\\/]+)\\/$";
    private static final String REGEX_LIVRO_GROUP_SIGLA = "$1";
    private static final String REGEX_CAPITULO = "^https:\\/\\/liturgiadashoras\\.online\\/biblia\\/biblia-jerusalem\\/([^\\/]+)\\/(.+)\\/$";
    private static final String JSON_DOWNLOAD_FILE_NAME = "./data/BibliaJerusalem.json";
    private static final Map<String, String> MAP_LIVRO = Map.ofEntries(
            Map.entry("genesis", "Gn"),
            Map.entry("exodus", "Ex"),
            Map.entry("leviticus", "Lv"),
            Map.entry("numeri", "Nm"),
            Map.entry("deuteronomii", "Dt"),
            Map.entry("iosue", "Js"),
            Map.entry("iudicum", "Jz"),
            Map.entry("ruth", "Rt"),
            Map.entry("i-samuelis", "1Sm"),
            Map.entry("ii-samuelis", "2Sm"),
            Map.entry("i-regum", "1Rs"),
            Map.entry("ii-regum", "2Rs"),
            Map.entry("i-paralipomenon", "1Cr"),
            Map.entry("ii-paralipomenon", "2Cr"),
            Map.entry("esdrae", "Esd"),
            Map.entry("nehemiae", "Ne"),
            Map.entry("thobis", "Tb"),
            Map.entry("iudith", "Jt"),
            Map.entry("esther", "Est"),
            Map.entry("i-maccabaeorum", "1Mc"),
            Map.entry("ii-maccabaeorum", "2Mc"),
            Map.entry("iob", "Jó"),
            Map.entry("psalmorum-2", "Sl"),
            Map.entry("proverbiorum", "Pr"),
            Map.entry("ecclesiastes", "Ecl"),
            Map.entry("canticum-canticorum", "Ct"),
            Map.entry("sapientiae", "Sb"),
            Map.entry("ecclesiasticus", "Eclo"),
            Map.entry("isaiae", "Is"),
            Map.entry("ieremiae", "Jr"),
            Map.entry("lamentationes", "Lm"),
            Map.entry("baruch", "Ba"),
            Map.entry("ezechielis", "Ez"),
            Map.entry("danielis", "Dn"),
            Map.entry("osee", "Os"),
            Map.entry("ioel", "Jl"),
            Map.entry("amos", "Am"),
            Map.entry("abdiae", "Ab"),
            Map.entry("ionae", "Jn"),
            Map.entry("michaeae", "Mq"),
            Map.entry("nahum", "Na"),
            Map.entry("habacuc", "Hab"),
            Map.entry("sophoniae", "Sf"),
            Map.entry("aggaei", "Ag"),
            Map.entry("zachariae", "Zc"),
            Map.entry("malachiae", "Ml"),
            Map.entry("matthaeum", "Mt"),
            Map.entry("marcum", "Mc"),
            Map.entry("lucam", "Lc"),
            Map.entry("ioannem", "Jo"),
            Map.entry("actus-apostolorum", "At"),
            Map.entry("romanos", "Rm"),
            Map.entry("i-corinthios", "1Cor"),
            Map.entry("ii-corinthios", "2Cor"),
            Map.entry("galatas", "Gl"),
            Map.entry("ephesios", "Ef"),
            Map.entry("philippenses", "Fl"),
            Map.entry("colossenses", "Cl"),
            Map.entry("i-thessalonicenses", "1Ts"),
            Map.entry("ii-thessalonicenses", "2Ts"),
            Map.entry("i-timotheum", "1Tm"),
            Map.entry("ii-timotheum", "2Tm"),
            Map.entry("titum", "Tt"),
            Map.entry("philemonem", "Fm"),
            Map.entry("hebraeos", "Hb"),
            Map.entry("iacobi", "Tg"),
            Map.entry("i-petri", "1Pd"),
            Map.entry("ii-petri", "2Pd"),
            Map.entry("i-ioannis", "1Jo"),
            Map.entry("ii-ioannis", "2Jo"),
            Map.entry("iii-ioannis", "3Jo"),
            Map.entry("iudae", "Jd"),
            Map.entry("apocalypsis", "Ap"));
    private static final String WIKI_EMPTY_FILE = "./data/empty.html";
    private static final String WIKI_OUTPUT_FILE = "./data/BibliaJerusalem.html";

    public BibliaJerusalem() throws IOException {
        log.info("BibliaJerusalem");

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
        objectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
        DefaultPrettyPrinter prettyPrinter = new DefaultPrettyPrinter();
        prettyPrinter.indentArraysWith(DefaultIndenter.SYSTEM_LINEFEED_INSTANCE);

        File jsonDownloadFile = new File(JSON_DOWNLOAD_FILE_NAME);
        Biblia biblia = null;
        if (!jsonDownloadFile.exists()) {
            biblia = downloadBiblia(jsonDownloadFile);
            objectMapper.writer(prettyPrinter).writeValue(jsonDownloadFile, biblia);
        } else {
            biblia = objectMapper.readValue(jsonDownloadFile, Biblia.class);
        }

        File wikiEmptyFile = new File(WIKI_EMPTY_FILE);
        if (!wikiEmptyFile.exists()) {
            log.error("Wiki vazio não encontrado.");
            return;
        }

        File wikiOutputFile = new File(WIKI_OUTPUT_FILE);
        // if (wikiOutputFile.exists()) {
        //     log.info("Wiki já gerado.");
        //     return;
        // }

        makeWiki(biblia, wikiEmptyFile, wikiOutputFile);
    }

    private Biblia downloadBiblia(File jsonDownloadFile) throws IOException {
        log.info("\tDownload");

        Biblia result = new Biblia(NOME, BASE_URL);

        Document html = Jsoup.connect(BASE_URL).get();
        Elements links = html.select("a");
        for (Element link : links) {
            String href = link.attr("href").replace("\n", "");
            if (href.matches(REGEX_LIVRO)) {
                String sigla = href.replaceAll(REGEX_LIVRO, REGEX_LIVRO_GROUP_SIGLA);
                String nome = link.text();
                sigla = MAP_LIVRO.get(sigla);
                result.getLivros().add(downloadLivro(sigla, nome, href));
            }
        }

        return result;
    }

    private Livro downloadLivro(String sigla, String nome, String url) throws IOException {
        log.info("\t\t" + sigla);

        Livro result = new Livro(sigla, nome, url);

        Document html = Jsoup.connect(url).get();
        Elements links = html.select("a");
        for (Element link : links) {
            String href = link.attr("href").replace("liturgiahorarum", "liturgiadashoras"); // ¯\_(ツ)_/¯
            if (href.matches(REGEX_CAPITULO)) {
                String capitulo = link.text();
                while (!result.getCapitulos().stream().filter(c -> c.getNumero().equals(capitulo)).findAny()
                        .isPresent()) {
                    try {
                        result.getCapitulos().add(downloadCapitulo(capitulo, href));
                    } catch (HttpStatusException | SocketTimeoutException e) {
                        log.warn("\t\t\t\t" + e.getMessage());
                        // Vai continuar tentando, até conseguir.
                    }
                }
            }
        }
        if (result.getCapitulos().size() == 0) {
            // Jd não lista os capítulos, mas ele está lá no site; basta montar a url como nos outros livros
            while (!result.getCapitulos().stream().filter(c -> c.getNumero().equals("1")).findAny().isPresent()) {
                try {
                    result.getCapitulos().add(downloadCapitulo("1", url + "1-2/"));
                } catch (HttpStatusException | SocketTimeoutException e) {
                    log.warn("\t\t\t\t" + e.getMessage());
                    // Vai continuar tentando, até conseguir.
                }
            }
        }

        return result;
    }

    private Capitulo downloadCapitulo(String numero, String url) throws IOException {
        log.info("\t\t\t" + numero);

        Capitulo result = new Capitulo(numero, url);

        Document html = Jsoup.connect(url).get();
        Elements entryContents = html.select(".entry-content");
        for (Element entryContent : entryContents) {
            for (Element bloco : entryContent.children()) {
                if (bloco.text().trim().length() > 0) {
                    result.getHtml().add(bloco.outerHtml());
                }
            }
        }

        return result;
    }

    private void makeWiki(Biblia biblia, File wikiEmptyFile, File wikiOutputFile) throws IOException {
        log.info("\tMaking wiki");
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

        TiddlyWiki tiddlyWiki = new TiddlyWiki(wikiEmptyFile);
        tiddlyWiki.setSiteTitle(biblia.getNome());
        tiddlyWiki.setSiteSubtitle("Importada [[daqui|" + biblia.getUrl() + "]] em " + biblia.getTimestamp().format(dtf) + ".");

        // Bíblia
        Tiddler tiddlerBiblia = new Tiddler("Bíblia");
        tiddlerBiblia.setText("! Livros");
        for (Livro livro : biblia.getLivros()) {
            tiddlerBiblia.setText(tiddlerBiblia.getText() + "\n* [[" + livro.getNome() + "|" + livro.getSigla() + "]]");
        }
        tiddlyWiki.insert(tiddlerBiblia);

        // Livros
        for (Livro livro : biblia.getLivros()) {
            Tiddler tiddlerLivro = new Tiddler(livro.getSigla());
            tiddlerLivro.setText("! Capítulos");
            for (Capitulo capitulo : livro.getCapitulos()) {
                tiddlerLivro.setText(tiddlerLivro.getText() + "\n* [[" + capitulo.getNumero() + "|" + livro.getSigla() + " " + capitulo.getNumero() + "]]");
            }
            tiddlyWiki.insert(tiddlerLivro);
        }

        // Capítulos
        for (Livro livro : biblia.getLivros()) {
            for (Capitulo capitulo : livro.getCapitulos()) {
                Tiddler tiddlerCapitulo = new Tiddler(livro.getSigla() + " " + capitulo.getNumero());
                tiddlerCapitulo.setText("! Texto");
                for (String html : capitulo.getHtml()) {
                    tiddlerCapitulo.setText(tiddlerCapitulo.getText() + "\n\n" + html);
                }
                tiddlyWiki.insert(tiddlerCapitulo);
            }
        }

        tiddlyWiki.setDefaultTiddlers(tiddlerBiblia.getTitle());

        tiddlyWiki.save(wikiOutputFile);
    }
}
