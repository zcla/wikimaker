package zcla71.wikimaker.wiki.biblia;

import java.time.LocalDateTime;

import lombok.Data;
import zcla71.tiddlywiki.Tiddler;
import zcla71.tiddlywiki.TiddlyWiki;

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

    public TiddlerLivro(Tiddler livro) {
        this.sigla = livro.getCustomProperties().get("sigla");
        this.nome = livro.getCustomProperties().get("nome");
        this.url = livro.getCustomProperties().get("url");
        this.timestamp = LocalDateTime.parse(livro.getCustomProperties().get("timestamp"), TiddlyWiki.DATE_TIME_FORMATTER_TIDDLYWIKI);
        this.texto = livro.getText();
    }

    public String getTitle() {
        return this.sigla;
    }
}
