package zcla71.wikimaker.liturgiadashorasonline.biblia;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class Biblia {
    private String nome;
    private String url;
    private LocalDateTime timestamp;
    private Collection<Livro> livros;

    public Biblia(String nome, String url) {
        this.nome = nome;
        this.url = url;
        this.timestamp = LocalDateTime.now();
        this.livros = new ArrayList<>();
    }
}
