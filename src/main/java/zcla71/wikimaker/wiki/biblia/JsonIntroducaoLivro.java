package zcla71.wikimaker.wiki.biblia;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class JsonIntroducaoLivro {
    private String livro;
    private String texto;
    private String url;
    private LocalDateTime timestamp;
    
    public JsonIntroducaoLivro(TiddlerIntroducaoLivro introducaoLivro) {
        this.livro = introducaoLivro.getLivro();
        this.texto = introducaoLivro.getTexto();
        this.url = introducaoLivro.getUrl();
        this.timestamp = introducaoLivro.getTimestamp();
    }
}
