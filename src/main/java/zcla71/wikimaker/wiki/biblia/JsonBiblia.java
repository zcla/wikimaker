package zcla71.wikimaker.wiki.biblia;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;

import lombok.Data;

@Data
public class JsonBiblia {
    private String nome;
    private String url;
    private LocalDateTime timestamp;
    private Collection<JsonLivro> livros;
    
    public JsonBiblia(TiddlerBiblia biblia) {
        this.nome = biblia.getNome();
        this.url = biblia.getUrl();
        this.timestamp = biblia.getTimestamp();
        this.livros = new ArrayList<>();
    }
}
