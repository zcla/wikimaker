package zcla71.wikimaker.wiki.biblia;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class TiddlerCapitulo {
    private String livro;
    private String numero;
    private String url;
    private LocalDateTime timestamp;
    private String texto;

    public TiddlerCapitulo(String livro, String numero, String url, LocalDateTime timestamp, String texto) {
        this.livro = livro;
        this.numero = numero;
        this.url = url;
        this.timestamp = timestamp;
        this.texto = texto;
    }

    public String getTitle() {
        return this.livro + " " + this.numero;
    }
}
