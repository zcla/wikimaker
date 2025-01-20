package zcla71.wikimaker.wiki.biblia;

import java.time.LocalDateTime;

import lombok.Data;

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

    public String getTitle() {
        return "Bíblia\\Introdução";
    }
}
