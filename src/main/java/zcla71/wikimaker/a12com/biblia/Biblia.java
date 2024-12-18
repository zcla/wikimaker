package zcla71.wikimaker.a12com.biblia;

import java.time.LocalDateTime;
import java.util.Collection;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class Biblia {
    // Propriedades da API
    private Integer error;
    private Collection<Livro> data;
    // Propriedades "minhas"
    private String nome;
    private String urlSite;
    private String url;
    private LocalDateTime timestamp;

    public Biblia(String nome, String urlSite, String url) {
        this.nome = nome;
        this.urlSite = urlSite;
        this.url = url;
        this.timestamp = LocalDateTime.now();
    }
}
