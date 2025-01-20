package zcla71.wikimaker.wiki.biblia;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class TiddlerTitulo {
    private String livro;
    private String capitulo;
    private String versiculo;
    private String nivel;
    private String url;
    private LocalDateTime timestamp;
    private String texto;

    public TiddlerTitulo(String livro, String capitulo, String versiculo, String nivel, String url, LocalDateTime timestamp, String texto) {
        this.livro = livro;
        this.capitulo = capitulo;
        this.versiculo = versiculo;
        this.nivel = nivel;
        this.url = url;
        this.timestamp = timestamp;
        this.texto = texto;
    }

    public String getTitle() {
        return this.livro + " " + this.capitulo + "," + this.versiculo + "\\Título\\" + this.nivel;
    }
}
