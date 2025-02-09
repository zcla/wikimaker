package zcla71.wikimaker.wiki.biblia;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;

import lombok.Data;

@Data
public class JsonLivro {
    private String sigla;
    private String nome;
    private String url;
    private LocalDateTime timestamp;
    private Collection<JsonCapitulo> capitulos;
    private JsonIntroducaoLivro introducao;
    
    public JsonLivro(TiddlerLivro livro) {
        this.sigla = livro.getSigla();
        this.nome = livro.getNome();
        this.url = livro.getUrl();
        this.timestamp = livro.getTimestamp();
        this.capitulos = new ArrayList<>();
    }
}
