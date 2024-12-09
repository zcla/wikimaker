package zcla71.wikimaker.wiki.biblia;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class TiddlerLivro {
    private String sigla;
    private String nome;
    private String url;
    private LocalDateTime timestamp;
    private String texto;

    public TiddlerLivro(String sigla, String nome, String url, LocalDateTime timestamp, String texto) {
        this.sigla = sigla;
        this.nome = nome;
        this.url = url;
        this.timestamp = timestamp;
        this.texto = texto;
    }

    public String getTitle() {
        return this.sigla;
    }
}
