package zcla71.wikimaker.vaticanva.bible;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import lombok.extern.slf4j.Slf4j;
import zcla71.tiddlywiki.TiddlyWiki;
import zcla71.wikimaker.WikiMaker;
import zcla71.wikimaker.wiki.biblia.TiddlerBiblia;
import zcla71.wikimaker.wiki.biblia.TiddlerCapitulo;
import zcla71.wikimaker.wiki.biblia.TiddlerLivro;
import zcla71.wikimaker.wiki.biblia.TiddlerVersiculo;
import zcla71.wikimaker.wiki.biblia.WikiBiblia;

@Slf4j
public class VaticanVaBibleNovaVulgataLt extends WikiMaker<Biblia> {
    private static final String NOME = "Nova Vulgata";
    private static final String BASE_URL = "https://www.vatican.va";
    private static final String SITE_URL = BASE_URL + "/archive/bible/nova_vulgata/documents/nova-vulgata_index_lt.html";
    private static final Collection<String> DOWNLOAD_URLS = Arrays.asList(
        BASE_URL + "/archive/bible/nova_vulgata/documents/nova-vulgata_vetus-testamentum_lt.html",
        BASE_URL + "/archive/bible/nova_vulgata/documents/nova-vulgata_novum-testamentum_lt.html"
    );
    private static final String REGEX_LIVRO = "^https:\\/\\/www\\.vatican\\.va\\/archive\\/bible\\/nova_vulgata\\/documents\\/nova-vulgata_[v|n]t_(.+?)_lt\\.html$";
    private static final String REGEX_LIVRO_GROUP_SIGLA = "$1";
    // private static final String REGEX_CAPITULO = "^https:\/\/liturgiadashoras\.online\/biblia\/biblia-jerusalem\/([^\/]+)\/(.+)\/$";
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
            Map.entry("iob", "Jó"),
            Map.entry("psalmorum", "Sl"),
            Map.entry("proverbiorum", "Pr"),
            Map.entry("ecclesiastes", "Ecl"),
            Map.entry("canticum-canticorum", "Ct"),
            Map.entry("sapientiae", "Sb"),
            Map.entry("ecclesiasticus", "Eclo"),
            Map.entry("isaiae", "Is"),
            Map.entry("ieremiae", "Jr"),
            Map.entry("lamentationes", "Lm"),
            Map.entry("baruch", "Br"),
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
            Map.entry("i-maccabaeorum", "1Mc"),
            Map.entry("ii-maccabaeorum", "2Mc"),
            Map.entry("evang-matthaeum", "Mt"),
            Map.entry("evang-marcum", "Mc"),
            Map.entry("evang-lucam", "Lc"),
            Map.entry("evang-ioannem", "Jo"),
            Map.entry("actus-apostolorum", "At"),
            Map.entry("epist-romanos", "Rm"),
            Map.entry("epist-i-corinthios", "1Cor"),
            Map.entry("epist-ii-corinthios", "2Cor"),
            Map.entry("epist-galatas", "Gl"),
            Map.entry("epist-ephesios", "Ef"),
            Map.entry("epist-philippenses", "Fl"),
            Map.entry("epist-colossenses", "Cl"),
            Map.entry("epist-i-thessalonicenses", "1Ts"),
            Map.entry("epist-ii-thessalonicenses", "2Ts"),
            Map.entry("epist-i-timotheum", "1Tm"),
            Map.entry("epist-ii-timotheum", "2Tm"),
            Map.entry("epist-titum", "Tt"),
            Map.entry("epist-philemonem", "Fm"),
            Map.entry("epist-hebraeos", "Hb"),
            Map.entry("epist-iacobi", "Tg"),
            Map.entry("epist-i-petri", "1Pd"),
            Map.entry("epist-ii-petri", "2Pd"),
            Map.entry("epist-i-ioannis", "1Jo"),
            Map.entry("epist-ii-ioannis", "2Jo"),
            Map.entry("epist-iii-ioannis", "3Jo"),
            Map.entry("epist-iudae", "Jd"),
            Map.entry("apocalypsis-ioannis", "Ap"));

    public VaticanVaBibleNovaVulgataLt() throws Exception {
        super();
    }

    @Override
    protected String getId() {
        return "vatican_va_bible_nova_vulgata_lt";
    }

    @Override
    protected Class<Biblia> getDownloadClass() {
        return Biblia.class;
    }

    @Override
    protected Biblia doDownload() throws Exception {
        Biblia result = new Biblia(NOME, SITE_URL);

        for (String url : DOWNLOAD_URLS) {
            String baseUrl = url.substring(0, url.lastIndexOf("/") + 1);
            Document html = Jsoup.connect(url).get();
            Elements links = html.select("a");
            for (Element link : links) {
                String href = link.attr("href").replace("\n", "");
                if (href.indexOf("/") == -1) { // Link relativo
                    href = baseUrl + href;
                }
                if (href.matches(REGEX_LIVRO)) {
                    String sigla = href.replaceAll(REGEX_LIVRO, REGEX_LIVRO_GROUP_SIGLA);
                    String nome = link.text();
                    sigla = MAP_LIVRO.get(sigla);
                    result.getLivros().add(downloadLivro(sigla, nome, href));
                }
            }
        }

        return result;
    }

    private Livro downloadLivro(String sigla, String nome, String url) throws IOException {
        log.info("\t\t" + sigla);

        Livro result = new Livro(sigla, nome, url);

        Document html = Jsoup.connect(url).get();
        result.setHtml(html.outerHtml());

        return result;
    }

    @Override
    protected WikiBiblia makeWiki(Biblia download) {
        DateTimeFormatter dtfHuman = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

        LocalDateTime now = LocalDateTime.now();
        System.out.println(now.format(dtfHuman));

        WikiBiblia wiki = new WikiBiblia(
            download.getNome(),
                "Importada [[daqui|" + download.getUrl() + "]] em " + download.getTimestamp().format(dtfHuman) + "."
        );

        // Bíblia
        bibliaToTiddler(download, wiki);

        return wiki;
    }

    private String bibliaToTiddler(Biblia biblia, WikiBiblia wiki) {
        StringBuilder sbTexto = new StringBuilder("! Livros");
        for (Livro livro : biblia.getLivros()) {
            String title = livroToTiddler(livro, wiki);
            sbTexto.append("\n* [[" + livro.getNome() + "|" + title + "]]");
        }

        TiddlerBiblia tiddlerBiblia = new TiddlerBiblia(
            biblia.getNome(),
            biblia.getUrl(),
            biblia.getTimestamp(),
            sbTexto.toString()
        );
        return wiki.setBiblia(tiddlerBiblia);
    }

    private String livroToTiddler(Livro livro, WikiBiblia wiki) {
        log.info("\t\t" + livro.getSigla());

        StringBuilder sbTexto = new StringBuilder("! Capítulos");
        Document html = Jsoup.parse(livro.getHtml());
        Elements links = html.select("a[name]");
        if (links.size() < 2) { // Livros com um só capítulo; sempre têm um <a name="top">
            String numCapitulo = "1";
            Elements ps = html.select("p");
            for (Element p : ps) {
                if (p.text().startsWith("1")) {
                    Element pCapitulo = p;
                    String title = capituloToTiddler(livro, numCapitulo, pCapitulo, wiki);
                    sbTexto.append("\n* [[" + numCapitulo + "|" + title + "]]");
                }
            }
        } else { // nem precisava, mas...
            for (Element link : links) {
                String linkName = link.attr("name");
                String regex = "^(PSALMUS )?(\\d+)$";
                String regexReplace = "$2";
                if (linkName.matches(regex)) {
                    String numCapitulo = linkName.replaceAll(regex, regexReplace);
                    Element pCapitulo = link;
                    while (!pCapitulo.tagName().equals("p")) {
                        pCapitulo = pCapitulo.parent();
                    }
        
                    String title = capituloToTiddler(livro, numCapitulo, pCapitulo, wiki);
                    sbTexto.append("\n* [[" + numCapitulo + "|" + title + "]]");
                }
            }
        }

        TiddlerLivro tiddlerLivro = new TiddlerLivro(
            livro.getSigla(),
            livro.getNome(),
            livro.getUrl(),
            livro.getTimestamp(),
            sbTexto.toString()
        );
        return wiki.addLivro(tiddlerLivro);
    }

    private String capituloToTiddler(Livro livro, String numCapitulo, Element pCapitulo, WikiBiblia wiki) {
        log.info("\t\t\t" + numCapitulo);

        StringBuilder sbTexto = new StringBuilder();
        List<String> versiculos = Arrays.asList(pCapitulo.outerHtml().split("<br>"));
        int numVersiculos = versiculos.size();
        versiculos = versiculos.stream().map(v -> v.trim()).toList(); // trim() em tudo
        versiculos = versiculos.stream().filter(v -> !v.matches("^<.*?>$")).toList(); // retira linhas que são elementos html
        Map<String, String> mapVersiculos = new LinkedHashMap<>();
        String lastNumVersiculo = null;
        String regexVersiculo = "^(\\d+)\\s+(.*)$";
        String regexVersiculoReplaceNumVersiculo = "$1";
        String regexVersiculoReplaceTexto = "$2";
        for (String versiculo : versiculos) {
            if (versiculo.matches(regexVersiculo)) {
                String numVersiculo = versiculo.replaceAll(regexVersiculo, regexVersiculoReplaceNumVersiculo);
                String texto = versiculo.replaceAll(regexVersiculo, regexVersiculoReplaceTexto);
                if (mapVersiculos.get(numVersiculo) != null) {
                    throw new RuntimeException("Versículo duplicado!");
                }
                mapVersiculos.put(numVersiculo, texto + "<br />" + TiddlyWiki.LINE_BREAK);
                lastNumVersiculo = numVersiculo;
            } else {
                mapVersiculos.put(lastNumVersiculo, mapVersiculos.get(lastNumVersiculo) + versiculo + "<br />" + TiddlyWiki.LINE_BREAK);
            }
        }
        log.info("\t\t\t\t" + numVersiculos + " => " + mapVersiculos.size());
        for (String numVersiculo : mapVersiculos.keySet()) {
            String versiculo = mapVersiculos.get(numVersiculo);
            String title = versiculoToTiddler(livro, numCapitulo, numVersiculo, versiculo, wiki);
            sbTexto.append("^^[[" + numVersiculo + "|" + title + "]]^^{{" + title + "}}" + TiddlyWiki.LINE_BREAK);
        }

        TiddlerCapitulo tiddlerCapitulo = new TiddlerCapitulo(
            livro.getSigla(),
            numCapitulo,
            livro.getUrl() + "#" + numCapitulo,
            livro.getTimestamp(),
            sbTexto.toString().trim()
        );
        return wiki.addCapitulo(tiddlerCapitulo);
    }

    private String versiculoToTiddler(Livro livro, String numCapitulo, String numVersiculo, String versiculo, WikiBiblia wiki) {
        TiddlerVersiculo tiddlerVersiculo = new TiddlerVersiculo(
            livro.getSigla(),
            numCapitulo,
            numVersiculo,
            livro.getUrl(),
            livro.getTimestamp(),
            versiculo
        );
        return wiki.addVersiculo(tiddlerVersiculo);
    }
}
