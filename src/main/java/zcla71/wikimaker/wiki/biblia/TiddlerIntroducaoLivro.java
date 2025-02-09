package zcla71.wikimaker.wiki.biblia;

import java.time.LocalDateTime;

import lombok.Data;
import zcla71.tiddlywiki.Tiddler;
import zcla71.tiddlywiki.TiddlyWiki;

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

    public TiddlerIntroducaoLivro(Tiddler introducaoLivro) {
        this.url = introducaoLivro.getCustomProperties().get("url");
        this.timestamp = LocalDateTime.parse(introducaoLivro.getCustomProperties().get("timestamp"), TiddlyWiki.DATE_TIME_FORMATTER_TIDDLYWIKI);
        this.livro = introducaoLivro.getCustomProperties().get("livro");
        this.texto = introducaoLivro.getText();
    }

    public String getTitle() {
        return this.livro + "\\Introdução";
    }
}
