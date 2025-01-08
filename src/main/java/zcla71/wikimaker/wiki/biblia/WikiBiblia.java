package zcla71.wikimaker.wiki.biblia;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;

import com.fasterxml.jackson.core.exc.StreamWriteException;
import com.fasterxml.jackson.databind.DatabindException;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.AccessLevel;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import zcla71.tiddlywiki.Tiddler;
import zcla71.tiddlywiki.TiddlyWiki;
import zcla71.utils.JacksonUtils;

@Data
@Slf4j
public class WikiBiblia {
    private static final String WIKI_EMPTY_FILE = "./data/tiddlywiki_empty.html";

    private String titulo;
    private String subtitulo;
    @Getter(AccessLevel.PRIVATE)
    @Setter(AccessLevel.NONE)
    private TiddlerBiblia biblia;
    @Getter(AccessLevel.PRIVATE)
    @Setter(AccessLevel.PRIVATE)
    private Collection<TiddlerLivro> livros;
    @Getter(AccessLevel.PRIVATE)
    @Setter(AccessLevel.PRIVATE)
    private Collection<TiddlerCapitulo> capitulos;
    @Getter(AccessLevel.PRIVATE)
    @Setter(AccessLevel.PRIVATE)
    private Collection<TiddlerVersiculo> versiculos;
    private Map<String, Object> tiddlerMap;

    private String fixTitle(String title) {
        // "Warning: avoid using any of the characters | [ ] { } in tiddler titles"
        String result = title;
        result = result.replaceAll("\\|", "&vert;"); // | &vert; &#x7c;
        result = result.replaceAll("\\[", "&lbrack;"); // [ &lbrack; &#x5b;
        result = result.replaceAll("\\]", "&rbrack;"); // ] &rbrack; &#x5d;
        result = result.replaceAll("\\{", "&lbrace;"); // { &lbrace; &#x7b;
        result = result.replaceAll("\\}", "&rbrace;"); // } &rbrace; &#x7d;
        return result;
    }

    public WikiBiblia(String titulo, String subtitulo) {
        this.titulo = titulo;
        this.subtitulo = subtitulo;
        this.livros = new ArrayList<>();
        this.capitulos = new ArrayList<>();
        this.versiculos = new ArrayList<>();
        this.tiddlerMap = new LinkedHashMap<>();
    }

    public WikiBiblia(File wikiInputFile) throws IOException {
        this.tiddlerMap = new LinkedHashMap<>();

        TiddlyWiki tiddlyWiki = new TiddlyWiki(wikiInputFile);
        this.titulo = tiddlyWiki.getSiteTitle();
        this.subtitulo = tiddlyWiki.getSiteSubtitle();

        List<Tiddler> biblias = tiddlyWiki.listByTag("Bíblia");
        if (biblias.size() != 1) {
            throw new RuntimeException("Erro ao buscar bíblia");
        }
        this.setBiblia(new TiddlerBiblia(biblias.get(0)));

        this.livros = new ArrayList<>();
        List<Tiddler> livros = tiddlyWiki.listByTag("Livro");
        for (Tiddler livro : livros) {
            this.addLivro(new TiddlerLivro(livro));
        }

        this.capitulos = new ArrayList<>();
        List<Tiddler> capitulos = tiddlyWiki.listByTag("Capítulo");
        for (Tiddler capitulo : capitulos) {
            this.addCapitulo(new TiddlerCapitulo(capitulo));
        }

        this.versiculos = new ArrayList<>();
        List<Tiddler> versiculos = tiddlyWiki.listByTag("Versículo");
        for (Tiddler versiculo : versiculos) {
            this.addVersiculo(new TiddlerVersiculo(versiculo));
        }
    }

    // TODO public WikiBiblia(JsonBiblia jsonBiblia) {}

    public String setBiblia(TiddlerBiblia biblia) {
        String title = fixTitle(biblia.getTitle());
        this.biblia = biblia;
        this.tiddlerMap.put(title, biblia);
        return title;
    }

    public String addLivro(TiddlerLivro livro) {
        String title = fixTitle(livro.getTitle());
        if (this.tiddlerMap.get(title) != null) {
            title += "/" + UUID.randomUUID().toString();
        }
        this.livros.add(livro);
        this.tiddlerMap.put(title, livro);
        return title;
    }

    public String addCapitulo(TiddlerCapitulo capitulo) {
        String title = fixTitle(capitulo.getTitle());
        if (this.tiddlerMap.get(title) != null) {
            title += "/" + UUID.randomUUID().toString();
        }
        this.capitulos.add(capitulo);
        this.tiddlerMap.put(title, capitulo);
        return title;
    }

    public String addVersiculo(TiddlerVersiculo versiculo) {
        String title = fixTitle(versiculo.getTitle());
        if (this.tiddlerMap.get(title) != null) {
            title += "/" + UUID.randomUUID().toString();
        }
        this.versiculos.add(versiculo);
        this.tiddlerMap.put(title, versiculo);
        return title;
    }

    public void saveAsWiki(File wikiOutputFile) throws IOException {
        File wikiEmptyFile = new File(WIKI_EMPTY_FILE);
        if (!wikiEmptyFile.exists()) {
            log.error("\t\tWiki vazio não encontrado.");
            return;
        }

        TiddlyWiki tiddlyWiki = new TiddlyWiki(wikiEmptyFile);
        tiddlyWiki.setSiteTitle(this.titulo);
        tiddlyWiki.setSiteSubtitle(this.subtitulo);

        for (Entry<String, Object> entry : this.tiddlerMap.entrySet()) {
            boolean tratado = false;
            String title = entry.getKey();

            if (entry.getValue() instanceof TiddlerBiblia biblia) {
                tratado = true;
                Tiddler tiddlerBiblia = new Tiddler(title);
                tiddlerBiblia.setTags("Bíblia");
                tiddlerBiblia.getCustomProperties().put("nome", biblia.getNome());
                tiddlerBiblia.getCustomProperties().put("url", biblia.getUrl());
                tiddlerBiblia.getCustomProperties().put("timestamp", biblia.getTimestamp().format(TiddlyWiki.DATE_TIME_FORMATTER_TIDDLYWIKI));
                tiddlerBiblia.setText(biblia.getTexto());
                tiddlyWiki.insert(tiddlerBiblia);
                tiddlyWiki.setDefaultTiddlers(tiddlerBiblia.getTitle());
            }

            if (entry.getValue() instanceof TiddlerLivro livro) {
                tratado = true;
                Tiddler tiddlerLivro = new Tiddler(title);
                tiddlerLivro.setTags("Livro");
                if (!title.equals(livro.getTitle())) {
                    tiddlerLivro.setTags(tiddlerLivro.getTags() + " Duplicado");
                }
                tiddlerLivro.getCustomProperties().put("sigla", livro.getSigla());
                tiddlerLivro.getCustomProperties().put("nome", livro.getNome());
                tiddlerLivro.getCustomProperties().put("url", livro.getUrl());
                tiddlerLivro.getCustomProperties().put("timestamp", livro.getTimestamp().format(TiddlyWiki.DATE_TIME_FORMATTER_TIDDLYWIKI));
                tiddlerLivro.setText(livro.getTexto());
                tiddlyWiki.insert(tiddlerLivro);
            }

            if (entry.getValue() instanceof TiddlerCapitulo capitulo) {
                log.info(title);
                tratado = true;
                Tiddler tiddlerCapitulo = new Tiddler(title);
                tiddlerCapitulo.setTags("Capítulo");
                if (!title.equals(capitulo.getTitle())) {
                    tiddlerCapitulo.setTags(tiddlerCapitulo.getTags() + " Duplicado");
                }
                tiddlerCapitulo.getCustomProperties().put("livro", capitulo.getLivro());
                tiddlerCapitulo.getCustomProperties().put("numero", capitulo.getNumero());
                tiddlerCapitulo.getCustomProperties().put("url", capitulo.getUrl());
                tiddlerCapitulo.getCustomProperties().put("timestamp", capitulo.getTimestamp().format(TiddlyWiki.DATE_TIME_FORMATTER_TIDDLYWIKI));
                tiddlerCapitulo.setText(capitulo.getTexto());
                tiddlyWiki.insert(tiddlerCapitulo);
            }

            if (entry.getValue() instanceof TiddlerVersiculo versiculo) {
                tratado = true;
                Tiddler tiddlerVersiculo = new Tiddler(title);
                tiddlerVersiculo.setTags("Versículo");
                if (!title.equals(versiculo.getTitle())) {
                    tiddlerVersiculo.setTags(tiddlerVersiculo.getTags() + " Duplicado");
                }
                tiddlerVersiculo.getCustomProperties().put("livro", versiculo.getLivro());
                tiddlerVersiculo.getCustomProperties().put("capitulo", versiculo.getCapitulo());
                tiddlerVersiculo.getCustomProperties().put("numero", versiculo.getNumero());
                tiddlerVersiculo.getCustomProperties().put("url", versiculo.getUrl());
                tiddlerVersiculo.getCustomProperties().put("timestamp", versiculo.getTimestamp().format(TiddlyWiki.DATE_TIME_FORMATTER_TIDDLYWIKI));
                tiddlerVersiculo.setText(versiculo.getTexto());
                tiddlyWiki.insert(tiddlerVersiculo);
            }

            if (!tratado) {
                throw new RuntimeException("Não sei tratar esse objeto");
            }
        }
        tiddlyWiki.save(wikiOutputFile);
    }

    public void saveAsJson(File jsonOutputFile) throws StreamWriteException, DatabindException, IOException {
        JsonBiblia jsonBiblia = asJson();
        ObjectMapper objectMapper = JacksonUtils.getObjectMapperInstance();
        JacksonUtils.enableJavaTime(objectMapper);
        objectMapper.writer(JacksonUtils.getPrettyPrinter()).writeValue(jsonOutputFile, jsonBiblia);
    }

    private JsonBiblia asJson() {
        JsonBiblia result = new JsonBiblia(this.getBiblia());

        for (TiddlerLivro tLivro : this.getLivros()) {
            JsonLivro jLivro = new JsonLivro(tLivro);
            result.getLivros().add(jLivro);
        }

        for (TiddlerCapitulo tCapitulo : this.getCapitulos()) {
            JsonCapitulo jCapitulo = new JsonCapitulo(tCapitulo);
            JsonLivro jLivro = result.getLivros().stream().filter(l -> l.getSigla().equals(tCapitulo.getLivro())).findFirst().get();
            jLivro.getCapitulos().add(jCapitulo);
        }

        for (TiddlerVersiculo tVersiculo : this.getVersiculos()) {
            JsonVersiculo jVersiculo = new JsonVersiculo(tVersiculo);
            JsonLivro jLivro = result.getLivros().stream().filter(l -> l.getSigla().equals(tVersiculo.getLivro())).findFirst().get();
            JsonCapitulo jCapitulo = jLivro.getCapitulos().stream().filter(c -> c.getNumero().equals(tVersiculo.getCapitulo())).findFirst().get();
            jCapitulo.getVersiculos().add(jVersiculo);
        }

        return result;
    }
}
