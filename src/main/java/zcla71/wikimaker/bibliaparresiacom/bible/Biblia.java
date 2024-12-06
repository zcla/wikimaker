package zcla71.wikimaker.bibliaparresiacom.bible;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class Biblia {
    private String nome;
    private String urlSite;
    private String url;
    private LocalDateTime timestamp;
    private Collection<Livro> livros;

    public Biblia(String nome, String urlSite, String url) {
        this.nome = nome;
        this.urlSite = urlSite;
        this.url = url;
        this.timestamp = LocalDateTime.now();
        this.livros = new ArrayList<>();
    }
}
