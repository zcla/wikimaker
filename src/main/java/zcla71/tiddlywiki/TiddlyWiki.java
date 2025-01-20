package zcla71.tiddlywiki;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.ObjectMapper;

public class TiddlyWiki {
    private Document html;
    private List<Tiddler> tiddlers;
    private File arquivo;

    public static final DateTimeFormatter DATE_TIME_FORMATTER_TIDDLYWIKI = DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS");
    public static final String LINE_BREAK = "\r\n";

    public TiddlyWiki(File arquivo) throws IOException {
        super();
        this.arquivo = arquivo;
        load();
    }

    private ObjectMapper getObjectMapper() {
        ObjectMapper result = new ObjectMapper();
        result.findAndRegisterModules();
        result.setSerializationInclusion(Include.NON_NULL);
        return result;
    }

    private Element getStore() {
        Elements stores = html.select("script.tiddlywiki-tiddler-store");
        if (stores.size() != 1) {
            throw new TiddlyWikiException("Deve haver um e apenas um script com classe \"tiddlywiki-tiddler-store\"");
        }
        return stores.first();
    }

    private void load() throws IOException {
        html = Jsoup.parse(arquivo);
        Element store = getStore();
        String json = store.html();
        ObjectMapper objectMapper = getObjectMapper();
        tiddlers = objectMapper.readValue(json, objectMapper.getTypeFactory().constructCollectionType(List.class, Tiddler.class));
        fixU003COnLoad();
    }

    private void fixU003COnLoad() {
        // 2024-11-25 Até hoje funcionava assim; depois disso, não precisa mais...

        // // Conserta problema que faz com que os "\u003C" da tag "$:/core" sejam transformados em "<".
        // Tiddler core = getByTitle("$:/core");
        // // Não sei por que não basta trocar por "\u003C"; precisei trocar por "\u003C", o que é desfeito em fixU003COnSave().
        // core.setText(core.getText().replaceAll("<", "\u003C"));
    }

    private String fixU003COnSave(String json) {
        // 2024-11-25 Até hoje funcionava assim; depois disso, mudou pro que tá embaixo...
        // // Desfaz o que o fixU003COnLoad() fez.
        // json = json.replaceAll("\\\\u003C", "\\u003C");
        // // Comparando com o original, fica restando um único trecho que fica diferente (falta uma barra invertida); não tenho ideia do motivo.
        // json = json.replace("\"\\\\\\\\\\\\\\u003C\\\\\\\"", "\"\\\\\\\\\\\\\\\\u003C\\\\\\\"");
        json = json.replaceAll("\\u003C", "\\\\u003C");

        return json;
    }

    public void delete(Tiddler tiddler) {
        if (tiddlers.contains(tiddler)) {
            tiddlers.remove(tiddler);
        } else {
            throw new TiddlyWikiException("Tiddler não encontrada");
        }
    }

    public Tiddler getByTagAndTitle(String tag, String title) {
        return tiddlers.stream().filter(t -> (t.getTags() != null) && t.getTags().contains(tag) && title.equals(t.getTitle())).findFirst().orElse(null);
    }

    public Tiddler getByTitle(String title) {
        return tiddlers.stream().filter(t -> title.equals(t.getTitle())).findFirst().orElse(null);
    }

    public String getSiteSubtitle() {
        return getByTitle("$:/SiteSubtitle").getText();
    }

    public String getSiteTitle() {
        return getByTitle("$:/SiteTitle").getText();
    }

    public void insert(Tiddler tiddler) {
        Tiddler existing = getByTitle(tiddler.getTitle());
        if (existing != null) {
            throw new TiddlerDuplicadaException(tiddler.getTitle());
        }
        tiddler.setCreated(LocalDateTime.now());
        tiddler.setModified(LocalDateTime.now());
        tiddlers.add(tiddler);
    }

    public List<Tiddler> listByTag(String tag) {
        return tiddlers.stream().filter(t -> (t.getTags() != null) && t.hasTag(tag)).toList();
    }

    public void save() throws IOException {
        save(arquivo);
    }

    public void save(File arquivo) throws IOException {
        ObjectMapper objectMapper = getObjectMapper();
        String json = objectMapper.writeValueAsString(tiddlers);
        Element store = getStore();
        store.html(fixU003COnSave(json));
        BufferedWriter writer = new BufferedWriter(new FileWriter(arquivo));
        writer.write(html.outerHtml());
        writer.close();
    }

    public void setDefaultTiddlers(String defaultTiddlers) {
        final String tiddlerTitle = "$:/DefaultTiddlers";
        Tiddler tiddler = getByTitle(tiddlerTitle);
        if (tiddler == null) {
            tiddler = new Tiddler(tiddlerTitle);
            this.insert(tiddler);
        }
        tiddler.setText(defaultTiddlers);
    }

    public void setSiteSubtitle(String siteSubtitle) {
        final String tiddlerTitle = "$:/SiteSubtitle";
        Tiddler tiddler = getByTitle(tiddlerTitle);
        if (tiddler == null) {
            tiddler = new Tiddler(tiddlerTitle);
            this.insert(tiddler);
        }
        tiddler.setText(siteSubtitle);
    }

    public void setSiteTitle(String siteTitle) {
        final String tiddlerTitle = "$:/SiteTitle";
        Tiddler tiddler = getByTitle(tiddlerTitle);
        if (tiddler == null) {
            tiddler = new Tiddler(tiddlerTitle);
            this.insert(tiddler);
        }
        tiddler.setText(siteTitle);
    }
}
