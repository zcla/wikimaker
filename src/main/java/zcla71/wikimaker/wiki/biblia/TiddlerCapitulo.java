package zcla71.wikimaker.wiki.biblia;

import java.time.LocalDateTime;

import lombok.Data;
import zcla71.tiddlywiki.Tiddler;
import zcla71.tiddlywiki.TiddlyWiki;

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

    public TiddlerCapitulo(Tiddler capitulo) {
        this.livro = capitulo.getCustomProperties().get("livro");
        this.numero = capitulo.getCustomProperties().get("numero");
        this.url = capitulo.getCustomProperties().get("url");
        this.timestamp = LocalDateTime.parse(capitulo.getCustomProperties().get("timestamp"), TiddlyWiki.DATE_TIME_FORMATTER_TIDDLYWIKI);
        this.texto = capitulo.getText();
    }

    public String getTitle() {
        return this.livro + " " + this.numero;
    }
}
