package zcla71.wikimaker.liturgiadashorasonline.biblia;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class Livro {
    private String sigla;
    private String nome;
    private String url;
    private LocalDateTime timestamp;
    private Collection<Capitulo> capitulos;

    public Livro(String sigla, String nome, String url) {
        this.sigla = sigla;
        this.nome = nome;
        this.url = url;
        this.timestamp = LocalDateTime.now();
        this.capitulos = new ArrayList<>();
    }
}
