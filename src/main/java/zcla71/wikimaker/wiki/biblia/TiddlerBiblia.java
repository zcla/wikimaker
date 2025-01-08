package zcla71.wikimaker.wiki.biblia;

import java.time.LocalDateTime;

import lombok.Data;
import zcla71.tiddlywiki.Tiddler;
import zcla71.tiddlywiki.TiddlyWiki;

@Data
public class TiddlerBiblia {
    private String nome;
    private String url;
    private LocalDateTime timestamp;
    private String texto;

    public TiddlerBiblia(String nome, String url, LocalDateTime timestamp, String texto) {
        this.nome = nome;
        this.url = url;
        this.timestamp = timestamp;
        this.texto = texto;
    }

    public TiddlerBiblia(Tiddler biblia) {
        this.nome = biblia.getCustomProperties().get("nome");
        this.url = biblia.getCustomProperties().get("url");
        this.timestamp = LocalDateTime.parse(biblia.getCustomProperties().get("timestamp"), TiddlyWiki.DATE_TIME_FORMATTER_TIDDLYWIKI);
        this.texto = biblia.getText();
    }

    public String getTitle() {
        return "Bíblia";
    }
}
