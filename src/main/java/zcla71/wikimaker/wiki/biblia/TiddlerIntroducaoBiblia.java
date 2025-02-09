package zcla71.wikimaker.wiki.biblia;

import java.time.LocalDateTime;

import lombok.Data;
import zcla71.tiddlywiki.Tiddler;
import zcla71.tiddlywiki.TiddlyWiki;

@Data
public class TiddlerIntroducaoBiblia {
    private String url;
    private LocalDateTime timestamp;
    private String texto;

    public TiddlerIntroducaoBiblia(String url, LocalDateTime timestamp, String texto) {
        this.url = url;
        this.timestamp = timestamp;
        this.texto = texto;
    }

    public TiddlerIntroducaoBiblia(Tiddler introducaoBiblia) {
        this.url = introducaoBiblia.getCustomProperties().get("url");
        this.timestamp = LocalDateTime.parse(introducaoBiblia.getCustomProperties().get("timestamp"), TiddlyWiki.DATE_TIME_FORMATTER_TIDDLYWIKI);
        this.texto = introducaoBiblia.getText();    }

    public String getTitle() {
        return "Bíblia\\Introdução";
    }
}
