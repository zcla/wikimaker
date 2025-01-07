package zcla71.wikimaker.liturgiadashorasonline.biblia;

import java.io.File;
import java.io.IOException;
import java.net.SocketTimeoutException;
import java.time.format.DateTimeFormatter;
import java.util.LinkedHashMap;
import java.util.Map;

import org.jsoup.HttpStatusException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;
import zcla71.utils.JacksonUtils;
import zcla71.wikimaker.wiki.biblia.TiddlerBiblia;
import zcla71.wikimaker.wiki.biblia.TiddlerCapitulo;
import zcla71.wikimaker.wiki.biblia.TiddlerLivro;
import zcla71.wikimaker.wiki.biblia.TiddlerVersiculo;
import zcla71.wikimaker.wiki.biblia.WikiBiblia;

@Slf4j
public class LiturgiaDasHorasOnlineBiblia {
    private static final String ID = "liturgiadashoras_online_biblia";
    private static final String NOME = "Bíblia de Jerusalém";
    private static final String BASE_URL = "https://liturgiadashoras.online/biblia/biblia-jerusalem/";
    private static final String REGEX_LIVRO = "^https:\\/\\/liturgiadashoras\\.online\\/biblia\\/biblia-jerusalem\\/([^\\/]+)\\/$";
    private static final String REGEX_LIVRO_GROUP_SIGLA = "$1";
    private static final String REGEX_CAPITULO = "^https:\\/\\/liturgiadashoras\\.online\\/biblia\\/biblia-jerusalem\\/([^\\/]+)\\/(.+)\\/$";
    private static final String JSON_DOWNLOAD_FILE_NAME = "./data/download/" + ID + ".json";
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
    private static final String WIKI_OUTPUT_FILE = "./data/wiki/" + ID + ".html";

    public LiturgiaDasHorasOnlineBiblia() throws IOException {
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

    private Biblia downloadBiblia() throws IOException {
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
        Map<String, String> capitulos = new LinkedHashMap<>();
        for (Element link : links) {
            String href = link.attr("href") // Erros meio bizarros ¯\_(ツ)_/¯
                    .replace("liturgiahorarum", "liturgiadashoras") // Vários lugares
                    .replace("comunidade /", ""); // At 1
            if (href.matches(REGEX_CAPITULO)) {
                String capitulo = ((Integer)Integer.parseInt(link.text())).toString();
                capitulos.put(capitulo, href);
            }
        }
        switch (result.getSigla()) {
            case "Rt":
                // Rt traz a lista errada, mas os 4 capítulos estão no site.
                capitulos.clear();
                for (Integer i = 1; i <= 4; i++) {
                    capitulos.put(i.toString(), url + i.toString() + "-2/");
                }
                break;

            case "Mq":
                // Mq não lista o capítulo 7, cujo texto está no site.
                if (result.getCapitulos().size() == 0) {
                    capitulos.put("7", url + "7-2/");
                }
                break;

            case "Jd":
                // Jd não lista o único capítulo, cujo texto está no site.
                if (result.getCapitulos().size() == 0) {
                    capitulos.put("1", url + "1-2/");
                }
                break;
        
            default:
                break;
        }
        for (String capitulo : capitulos.keySet()) {
            String href = capitulos.get(capitulo);
            if (!href.startsWith(url)) { // Acontece com todos os salmos, mas só com eles
                log.warn("Possível url errada: " + href);
            }
            while (!result.getCapitulos().stream().filter(c -> c.getNumero().toString().equals(capitulo)).findAny().isPresent()) {
                try {
                    result.getCapitulos().add(downloadCapitulo(capitulo, href));
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

    private WikiBiblia makeWiki(Biblia biblia) {
        log.info("\tGerando wiki");
        DateTimeFormatter dtfHuman = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

        WikiBiblia wiki = new WikiBiblia(
                biblia.getNome(),
                "Importada [[daqui|" + biblia.getUrl() + "]] em " + biblia.getTimestamp().format(dtfHuman) + "."
        );

        // Bíblia
        bibliaToTiddler(biblia, wiki);

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
        for (Capitulo capitulo : livro.getCapitulos()) {
            String title = capituloToTiddler(livro, capitulo, wiki);
            sbTexto.append("\n* [[" + capitulo.getNumero() + "|" + title + "]]");
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

    private String capituloToTiddler(Livro livro, Capitulo capitulo, WikiBiblia wiki) {
        log.info("\t\t\t" + capitulo.getNumero());

        StringBuilder sbTexto = new StringBuilder();
        for (String html : capitulo.getHtml()) {
            sbTexto.append(html);
        }

        TiddlerCapitulo tiddlerCapitulo = new TiddlerCapitulo(
            livro.getSigla(),
            capitulo.getNumero().toString(),
            capitulo.getUrl(),
            capitulo.getTimestamp(),
            sbTexto.toString()
        );
        versiculosToTiddler(tiddlerCapitulo, wiki);
        return wiki.addCapitulo(tiddlerCapitulo);
    }

    private void versiculosToTiddler(TiddlerCapitulo tiddlerCapitulo, WikiBiblia wiki) {
        final String LINE_BREAK = "\n";
        String html = tiddlerCapitulo.getTexto();

        // Listas (ul/li) "perdidas" (Ex 24, Lv 4, ...?)
        int pFaltando = 0;
        String find = "</p><ul class=\"wp-block-list\">" + LINE_BREAK + " <li>";
        String repl = " ";
        int lengthBefore = html.length();
        html = html.replace(find, repl);
        int lengthAfter = html.length();
        pFaltando += (lengthBefore - lengthAfter) / (find.length() - repl.length());
        if (lengthBefore != html.length()) { // Só se alterou na linha acima
            find = "</li>" + LINE_BREAK + "</ul><p>";
            repl = " ";
            lengthBefore = html.length();
            html = html.replace(find, repl);
            lengthAfter = html.length();
            pFaltando -= (lengthBefore - lengthAfter) / (find.length() - repl.length());
        }
        find = "<ul class=\"wp-block-list\">" + LINE_BREAK + " <li>";
        repl = "";
        html = html.replace(find, repl);
        find = "</li>" + LINE_BREAK + "</ul>";
        repl = "";
        html = html.replace("</li>" + LINE_BREAK + "</ul>", "");
        if ((pFaltando != 0) && (pFaltando != 1)) {
            throw new RuntimeException("Não sei o que fazer...");
        }
        for (int i = 0; i < pFaltando; i++) {
            html += "</p>";
        }

        int pos = 0;
        String htmlFechamento = "";
        String numVersiculo = "";
        int posVersiculoIni = 0;
        while (pos < html.length()) {
            int posElementoIniIni = html.indexOf("<", pos);
            if (posElementoIniIni >= 0) {
                int posElementoIniFim = html.indexOf(">", posElementoIniIni);
                String elementoTag = html.substring(posElementoIniIni + 1, posElementoIniFim).split(" ")[0];
                int posElementoFimIni = html.indexOf("</" + elementoTag + ">", posElementoIniIni);
                int posElementoFimFim = posElementoFimIni + elementoTag.length() + 3;
                if (posElementoFimIni == -1) { // Não achou o fim da tag
                    String fim = html.substring(posElementoIniIni);
                    if ((htmlFechamento.length() > 0) && fim.startsWith(htmlFechamento)) { // Se for o fechamento final
                        posElementoFimIni = posElementoIniIni;
                        posElementoFimFim = posElementoFimIni + htmlFechamento.length();
                        htmlFechamento = "";
                    } else {
                        if (elementoTag.startsWith("/") || "br".equals(elementoTag)) {
                            // É uma tag sem fechamento ou um fechamento em si; ignora, pois vai ser tratado mais à frente.
                        } else {
                            throw new RuntimeException("Não sei o que fazer...");
                        }
                    }
                }
                if (posElementoFimFim == -1) {
                    throw new RuntimeException("Elemento \"" + elementoTag + "\" sem fechamento.");
                }
                if (numVersiculo.length() == 0) { // Está no texto do capítulo, fora de qualquer versículo
                    switch (elementoTag) {
                        case "a": // Links "perdidos" no texto: remove
                            // fall through
                        case "/a": // Links "perdidos" no texto: remove
                            html = html.substring(0, posElementoIniIni) + html.substring(posElementoIniFim + 1);
                            break;

                        case "em": // É uma ênfase; deixa no capítulo e continua
                            // fall through
                        case "ol": // É um título; deixa no capítulo e continua
                            // fall through
                        case "strong": // É um subtítulo; deixa no capítulo e continua
                            pos = posElementoFimFim;
                            break;

                        case "figure": // ???; remove do início ao fim
                            html = html.substring(0, posElementoIniIni) + html.substring(posElementoFimFim);
                            break;

                        case "p": // Esse elemento vai até o final do html; tem que tratar dentro dele
                            htmlFechamento = "</" + elementoTag + ">" + htmlFechamento;
                            pos = posElementoIniFim;
                            break;

                        case "/p": // Já foi feito o fechamento anteriormente; só ignora
                            pos = posElementoIniFim;
                            break;

                        case "sup": // É o início de um versículo; marca início de versículo e continua, mantendo a tag
                            // Marca início de versículo
                            numVersiculo = html.substring(posElementoIniFim + 1, posElementoFimIni);
                            posVersiculoIni = posElementoFimFim;
                            pos = posVersiculoIni;
                            break;

                        default:
                            throw new RuntimeException("Elemento \"" + elementoTag + "\" desconhecido.");
                    }
                } else { // Está no meio de um versículo
                    switch (elementoTag) {
                        case "a": // Links "perdidos" no texto: remove
                            // fall through
                        case "/a": // Links "perdidos" no texto: remove
                            // fall through
                        case "br": // Quebras de linha: remove
                            html = html.substring(0, posElementoIniIni) + html.substring(posElementoIniFim + 1);
                            break;

                        case "em": // É uma ênfase; continua
                            pos = posElementoFimFim;
                            break;

                        case "/p": // Fim do capítulo
                            // fall through
                        case "sup": // É o início de um novo versículo
                            // Adiciona o que estava desde o último <sup> até chegar aqui
                            String tiddlerText = html.substring(posVersiculoIni, posElementoIniIni);
                            TiddlerVersiculo tiddlerVersiculo = new TiddlerVersiculo(
                                tiddlerCapitulo.getLivro(),
                                tiddlerCapitulo.getNumero(),
                                numVersiculo,
                                tiddlerCapitulo.getUrl(),
                                tiddlerCapitulo.getTimestamp(),
                                tiddlerText
                            );
                            String title = wiki.addVersiculo(tiddlerVersiculo);
                            // Substitui o texto do versículo por um transclusion
                            String transclusion = "{{" + title + "}}";
                            html = html.substring(0, posVersiculoIni) + transclusion + html.substring(posElementoIniIni);
                            // Marca início de versículo
                            int posDif = transclusion.length() - tiddlerText.length();
                            if ("/p".equals(elementoTag)) {
                                numVersiculo = "";
                            } else {
                                numVersiculo = html.substring(posElementoIniFim + posDif + 1, posElementoFimIni + posDif);
                            }
                            posVersiculoIni = posElementoFimFim + posDif;
                            pos = posVersiculoIni;
                            break;

                        case "strong": // É um negrito; continua
                            pos = posElementoFimFim;
                            break;

                        default:
                            throw new RuntimeException("Elemento \"" + elementoTag + "\" desconhecido.");
                    }
                }
            } else {
                pos = html.length();
            }
        }
        // Adiciona links para os versículos
        html = html.replaceAll("<sup>(.+?)<\\/sup>\\{\\{(.+?)\\}\\}", "<sup>[[$1|$2]]<\\/sup>{{$2}}");
        tiddlerCapitulo.setTexto(html);
    }
}
