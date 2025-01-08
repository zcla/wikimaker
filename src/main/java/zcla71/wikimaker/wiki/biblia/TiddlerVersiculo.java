package zcla71.wikimaker.wiki.biblia;

import java.time.LocalDateTime;

import lombok.Data;
import zcla71.tiddlywiki.Tiddler;
import zcla71.tiddlywiki.TiddlyWiki;

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

    public TiddlerVersiculo(Tiddler versiculo) {
        this.livro = versiculo.getCustomProperties().get("livro");
        this.capitulo = versiculo.getCustomProperties().get("capitulo");;
        this.numero = versiculo.getCustomProperties().get("numero");
        this.url = versiculo.getCustomProperties().get("url");
        this.timestamp = LocalDateTime.parse(versiculo.getCustomProperties().get("timestamp"), TiddlyWiki.DATE_TIME_FORMATTER_TIDDLYWIKI);
        this.texto = versiculo.getText();
    }

    public String getTitle() {
        return this.livro + " " + this.capitulo + "," + this.numero;
    }
}
