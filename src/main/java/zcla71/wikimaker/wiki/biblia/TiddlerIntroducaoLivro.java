package zcla71.wikimaker.wiki.biblia;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class TiddlerIntroducaoLivro {
    private String url;
    private LocalDateTime timestamp;
    private String livro;
    private String texto;

    public TiddlerIntroducaoLivro(String url, LocalDateTime timestamp, String livro, String texto) {
        this.url = url;
        this.timestamp = timestamp;
        this.livro = livro;
        this.texto = texto;
    }

    public String getTitle() {
        return this.livro + "\\Introdução";
    }
}
