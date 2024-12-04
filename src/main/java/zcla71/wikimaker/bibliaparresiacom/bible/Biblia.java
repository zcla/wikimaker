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
    private String urlApi;
    private LocalDateTime timestamp;
    private Collection<Livro> livros;

    public Biblia(String nome, String urlSite, String urlApi) {
        this.nome = nome;
        this.urlSite = urlSite;
        this.urlApi = urlApi;
        this.timestamp = LocalDateTime.now();
        this.livros = new ArrayList<>();
    }
}
