package zcla71.wikimaker.wiki.biblia;

import java.time.LocalDateTime;

import lombok.Data;

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

    public String getTitle() {
        return "Bíblia";
    }
}
