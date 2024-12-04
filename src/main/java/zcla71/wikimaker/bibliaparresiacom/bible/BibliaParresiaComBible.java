package zcla71.wikimaker.bibliaparresiacom.bible;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
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

        // File wikiEmptyFile = new File(WIKI_EMPTY_FILE);
        // if (!wikiEmptyFile.exists()) {
        //     log.error("\tWiki vazio não encontrado.");
        //     return;
        // }
        // makeWiki(biblia, wikiEmptyFile, wikiOutputFile);
    }

    private Biblia downloadBiblia(ObjectMapper objectMapper, File jsonDownloadFile) throws URISyntaxException, StreamReadException, DatabindException, IOException {
        log.info("\tDownload");

        Biblia result = new Biblia(NOME, SITE_URL, BASE_API_URL);

        URL urlBooks = new URI(BASE_API_URL + "books").toURL();
        Collection<Livro> livros = objectMapper.readValue(urlBooks, new TypeReference<Collection<Livro>>(){});
        result.setLivros(livros);

        for (Livro livro : livros) {
            String sigla = MAP_LIVRO.get(livro.getSlug());
            log.info("\t\t" + sigla);
            livro.setSigla(sigla);
            livro.setCapitulos(new ArrayList<>());
            for (int i = 1; i <= livro.getChapterCount(); i++) {
                log.info("\t\t\t" + i);
                Capitulo capitulo = new Capitulo(i);
                livro.getCapitulos().add(capitulo);

                URL urlChapter = new URI(BASE_API_URL + "chapter/" + livro.getSlug() + "_" + i + "/").toURL();
                Collection<Versiculo> versiculos = objectMapper.readValue(urlChapter, new TypeReference<Collection<Versiculo>>(){});
                capitulo.setVersiculos(versiculos);
            }
        }

        return result;
    }

    // private void makeWiki(Biblia biblia, File wikiEmptyFile, File wikiOutputFile) throws IOException {
    //     log.info("\tMaking wiki");
    //     DateTimeFormatter dtfHuman = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

    //     TiddlyWiki tiddlyWiki = new TiddlyWiki(wikiEmptyFile);
    //     tiddlyWiki.setSiteTitle(biblia.getNome());
    //     tiddlyWiki.setSiteSubtitle("Importada [[daqui|" + biblia.getUrl() + "]] em " + biblia.getTimestamp().format(dtfHuman) + ".");

    //     // Bíblia
    //     bibliaToTiddler(biblia, tiddlyWiki);

    //     tiddlyWiki.save(wikiOutputFile);
    // }

    // private void bibliaToTiddler(Biblia biblia, TiddlyWiki tiddlyWiki) {
    //     Tiddler tiddlerBiblia = new Tiddler("Bíblia");
    //     tiddlerBiblia.getCustomProperties().put("nome", biblia.getNome());
    //     tiddlerBiblia.getCustomProperties().put("url", biblia.getUrl());
    //     tiddlerBiblia.getCustomProperties().put("timestamp", biblia.getTimestamp().format(TiddlyWiki.DATE_TIME_FORMATTER_TIDDLYWIKI));
    //     tiddlerBiblia.setText("! Livros");
    //     for (Livro livro : biblia.getLivros()) {
    //         tiddlerBiblia.setText(tiddlerBiblia.getText() + "\n* [[" + livro.getNome() + "|" + livro.getSigla() + "]]");
    //         livroToTiddler(livro, tiddlyWiki);
    //     }
    //     tiddlyWiki.insert(tiddlerBiblia);
    //     tiddlyWiki.setDefaultTiddlers(tiddlerBiblia.getTitle());
    // }

    // private void livroToTiddler(Livro livro, TiddlyWiki tiddlyWiki) {
    //     Tiddler tiddlerLivro = new Tiddler(livro.getSigla());
    //     tiddlerLivro.setTags("Livro");
    //     tiddlerLivro.getCustomProperties().put("sigla", livro.getSigla());
    //     tiddlerLivro.getCustomProperties().put("nome", livro.getNome());
    //     tiddlerLivro.getCustomProperties().put("url", livro.getUrl());
    //     tiddlerLivro.getCustomProperties().put("timestamp", livro.getTimestamp().format(TiddlyWiki.DATE_TIME_FORMATTER_TIDDLYWIKI));
    //     tiddlerLivro.setText("! Capítulos");
    //     for (Capitulo capitulo : livro.getCapitulos()) {
    //         tiddlerLivro.setText(tiddlerLivro.getText() + "\n* [[" + capitulo.getNumero() + "|" + livro.getSigla() + " " + capitulo.getNumero() + "]]");
    //         capituloToTiddler(livro, capitulo, tiddlyWiki);
    //     }
    //     tiddlyWiki.insert(tiddlerLivro);
    // }

    // private void capituloToTiddler(Livro livro, Capitulo capitulo, TiddlyWiki tiddlyWiki) {
    //     Tiddler tiddlerCapitulo = new Tiddler(livro.getSigla() + " " + capitulo.getNumero());
    //     tiddlerCapitulo.setTags("Capítulo");
    //     tiddlerCapitulo.getCustomProperties().put("livro", "[[" + livro.getSigla() + "]]");
    //     tiddlerCapitulo.getCustomProperties().put("numero", capitulo.getNumero().toString());
    //     tiddlerCapitulo.getCustomProperties().put("url", capitulo.getUrl());
    //     tiddlerCapitulo.getCustomProperties().put("timestamp", capitulo.getTimestamp().format(TiddlyWiki.DATE_TIME_FORMATTER_TIDDLYWIKI));
    //     tiddlerCapitulo.setText("");
    //     for (String html : capitulo.getHtml()) {
    //         tiddlerCapitulo.setText(tiddlerCapitulo.getText() + html);
    //     }
    //     tiddlyWiki.insert(tiddlerCapitulo);
    //     versiculosToTiddler(tiddlerCapitulo, tiddlyWiki);
    // }

    // private void versiculosToTiddler(Tiddler tiddlerCapitulo, TiddlyWiki tiddlyWiki) {
    //     log.info("\t\t" + tiddlerCapitulo.getTitle());
    //     final String LINE_BREAK = "\n";
    //     String html = tiddlerCapitulo.getText();

    //     // Listas (ul/li) "perdidas" (Ex 24, Lv 4, ...?)
    //     int pFaltando = 0;
    //     String find = "</p><ul class=\"wp-block-list\">" + LINE_BREAK + " <li>";
    //     String repl = " ";
    //     int lengthBefore = html.length();
    //     html = html.replace(find, repl);
    //     int lengthAfter = html.length();
    //     pFaltando += (lengthBefore - lengthAfter) / (find.length() - repl.length());
    //     if (lengthBefore != html.length()) { // Só se alterou na linha acima
    //         find = "</li>" + LINE_BREAK + "</ul><p>";
    //         repl = " ";
    //         lengthBefore = html.length();
    //         html = html.replace(find, repl);
    //         lengthAfter = html.length();
    //         pFaltando -= (lengthBefore - lengthAfter) / (find.length() - repl.length());
    //     }
    //     find = "<ul class=\"wp-block-list\">" + LINE_BREAK + " <li>";
    //     repl = "";
    //     html = html.replace(find, repl);
    //     find = "</li>" + LINE_BREAK + "</ul>";
    //     repl = "";
    //     html = html.replace("</li>" + LINE_BREAK + "</ul>", "");
    //     if ((pFaltando != 0) && (pFaltando != 1)) {
    //         throw new RuntimeException("Não sei o que fazer...");
    //     }
    //     for (int i = 0; i < pFaltando; i++) {
    //         html += "</p>";
    //     }

    //     int pos = 0;
    //     String htmlFechamento = "";
    //     String numVersiculo = "";
    //     int posVersiculoIni = 0;
    //     while (pos < html.length()) {
    //         int posElementoIniIni = html.indexOf("<", pos);
    //         if (posElementoIniIni >= 0) {
    //             int posElementoIniFim = html.indexOf(">", posElementoIniIni);
    //             String elementoTag = html.substring(posElementoIniIni + 1, posElementoIniFim).split(" ")[0];
    //             int posElementoFimIni = html.indexOf("</" + elementoTag + ">", posElementoIniIni);
    //             int posElementoFimFim = posElementoFimIni + elementoTag.length() + 3;
    //             if (posElementoFimIni == -1) { // Não achou o fim da tag
    //                 String fim = html.substring(posElementoIniIni);
    //                 if ((htmlFechamento.length() > 0) && fim.startsWith(htmlFechamento)) { // Se for o fechamento final
    //                     posElementoFimIni = posElementoIniIni;
    //                     posElementoFimFim = posElementoFimIni + htmlFechamento.length();
    //                     htmlFechamento = "";
    //                 } else {
    //                     if (elementoTag.startsWith("/") || "br".equals(elementoTag)) {
    //                         // É uma tag sem fechamento ou um fechamento em si; ignora, pois vai ser tratado mais à frente.
    //                     } else {
    //                         throw new RuntimeException("Não sei o que fazer...");
    //                     }
    //                 }
    //             }
    //             if (posElementoFimFim == -1) {
    //                 throw new RuntimeException("Elemento \"" + elementoTag + "\" sem fechamento.");
    //             }
    //             if (numVersiculo.length() == 0) { // Está no texto do capítulo, fora de qualquer versículo
    //                 switch (elementoTag) {
    //                     case "a": // Links "perdidos" no texto: remove
    //                         // fall through
    //                     case "/a": // Links "perdidos" no texto: remove
    //                         html = html.substring(0, posElementoIniIni) + html.substring(posElementoIniFim + 1);
    //                         break;

    //                     case "em": // É uma ênfase; deixa no capítulo e continua
    //                         // fall through
    //                     case "ol": // É um título; deixa no capítulo e continua
    //                         // fall through
    //                     case "strong": // É um subtítulo; deixa no capítulo e continua
    //                         pos = posElementoFimFim;
    //                         break;

    //                     case "figure": // ???; remove do início ao fim
    //                         html = html.substring(0, posElementoIniIni) + html.substring(posElementoFimFim);
    //                         break;

    //                     case "p": // Esse elemento vai até o final do html; tem que tratar dentro dele
    //                         htmlFechamento = "</" + elementoTag + ">" + htmlFechamento;
    //                         pos = posElementoIniFim;
    //                         break;

    //                     case "/p": // Já foi feito o fechamento anteriormente; só ignora
    //                         pos = posElementoIniFim;
    //                         break;

    //                     case "sup": // É o início de um versículo; marca início de versículo e continua, mantendo a tag
    //                         // Marca início de versículo
    //                         numVersiculo = html.substring(posElementoIniFim + 1, posElementoFimIni);
    //                         posVersiculoIni = posElementoFimFim;
    //                         pos = posVersiculoIni;
    //                         break;

    //                     default:
    //                         throw new RuntimeException("Elemento \"" + elementoTag + "\" desconhecido.");
    //                 }
    //             } else { // Está no meio de um versículo
    //                 switch (elementoTag) {
    //                     case "a": // Links "perdidos" no texto: remove
    //                         // fall through
    //                     case "/a": // Links "perdidos" no texto: remove
    //                         // fall through
    //                     case "br": // Quebras de linha: remove
    //                         html = html.substring(0, posElementoIniIni) + html.substring(posElementoIniFim + 1);
    //                         break;

                            
    //                     case "em": // É uma ênfase; continua
    //                         pos = posElementoFimFim;
    //                         break;

    //                     case "/p": // Fim do capítulo
    //                         // fall through
    //                     case "sup": // É o início de um novo versículo
    //                         // Adiciona o que estava desde o último <sup> até chegar aqui
    //                         String tiddlerText = html.substring(posVersiculoIni, posElementoIniIni);
    //                         Tiddler tiddlerVersiculo = new Tiddler(tiddlerCapitulo.getTitle() + "," + numVersiculo);
    //                         tiddlerVersiculo.setTags("Versículo");
    //                         tiddlerVersiculo.getCustomProperties().put("livro", tiddlerCapitulo.getCustomProperties().get("livro"));
    //                         tiddlerVersiculo.getCustomProperties().put("capitulo", tiddlerCapitulo.getCustomProperties().get("numero"));
    //                         tiddlerVersiculo.getCustomProperties().put("numero", numVersiculo);
    //                         tiddlerVersiculo.getCustomProperties().put("url", tiddlerCapitulo.getCustomProperties().get("url"));
    //                         tiddlerVersiculo.getCustomProperties().put("timestamp", tiddlerCapitulo.getCustomProperties().get("timestamp"));
    //                         tiddlerVersiculo.setText(tiddlerText);
    //                         try {
    //                             tiddlyWiki.insert(tiddlerVersiculo);
    //                         } catch (TiddlyWikiException e) {
    //                             if (e.getMessage().startsWith("Tentativa de incluir tiddler duplicada: ")) {
    //                                 tiddlerVersiculo.getCustomProperties().put("versiculo", tiddlerVersiculo.getTitle());
    //                                 tiddlerVersiculo.setTitle(tiddlerVersiculo.getTitle() + " " + UUID.randomUUID());
    //                                 tiddlerVersiculo.setTags(tiddlerVersiculo.getTags() + " TiddlyWikiException");
    //                                 tiddlyWiki.insert(tiddlerVersiculo);
    //                             } else {
    //                                 throw e;
    //                             }
    //                         }
    //                         // Substitui o texto do versículo por um transclusion
    //                         String transclusion = "{{" + tiddlerVersiculo.getTitle() + "}}";
    //                         html = html.substring(0, posVersiculoIni) + transclusion + html.substring(posElementoIniIni);
    //                         // Marca início de versículo
    //                         int posDif = transclusion.length() - tiddlerText.length();
    //                         if ("/p".equals(elementoTag)) {
    //                             numVersiculo = "";
    //                         } else {
    //                             numVersiculo = html.substring(posElementoIniFim + posDif + 1, posElementoFimIni + posDif);
    //                         }
    //                         posVersiculoIni = posElementoFimFim + posDif;
    //                         pos = posVersiculoIni;
    //                         break;

    //                     case "strong": // É um negrito; continua
    //                         pos = posElementoFimFim;
    //                         break;

    //                     default:
    //                         throw new RuntimeException("Elemento \"" + elementoTag + "\" desconhecido.");
    //                 }
    //             }
    //         } else {
    //             pos = html.length();
    //         }
    //     }
    //     // Adiciona links para os versículos
    //     html = html.replaceAll("<sup>(.+?)<\\/sup>\\{\\{(.+?)\\}\\}", "<sup>[[$1|$2]]<\\/sup>{{$2}}");
    //     tiddlerCapitulo.setText(html);
    // }
}
