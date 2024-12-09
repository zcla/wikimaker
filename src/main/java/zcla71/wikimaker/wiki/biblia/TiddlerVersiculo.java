package zcla71.wikimaker.wiki.biblia;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class TiddlerVersiculo {
    private String livro;
    private String capitulo;
    private String numero;
    private String url;
    private LocalDateTime timestamp;
    private String texto;

    public TiddlerVersiculo(String livro, String capitulo, String numero, String url, LocalDateTime timestamp, String texto) {
        this.livro = livro;
        this.capitulo = capitulo;
        this.numero = numero;
        this.url = url;
        this.timestamp = timestamp;
        this.texto = texto;
    }

    public String getTitle() {
        return this.livro + " " + this.capitulo + "," + this.numero;
    }
}
