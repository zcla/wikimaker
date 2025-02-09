package zcla71.wikimaker.wiki.biblia;

import java.time.LocalDateTime;

import lombok.Data;
import zcla71.tiddlywiki.Tiddler;
import zcla71.tiddlywiki.TiddlyWiki;

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

    public TiddlerTitulo(Tiddler titulo) {
        this.livro = titulo.getCustomProperties().get("livro");
        this.capitulo = titulo.getCustomProperties().get("capitulo");
        this.versiculo = titulo.getCustomProperties().get("versiculo");
        this.nivel = titulo.getCustomProperties().get("nivel");
        this.url = titulo.getCustomProperties().get("url");
        this.timestamp = LocalDateTime.parse(titulo.getCustomProperties().get("timestamp"), TiddlyWiki.DATE_TIME_FORMATTER_TIDDLYWIKI);
        this.texto = titulo.getText();
    }

    public String getTitle() {
        return this.livro + " " + this.capitulo + "," + this.versiculo + "\\Título\\" + this.nivel;
    }
}
