package zcla71.wikimaker.biblia;

import java.util.Collection;

import lombok.Data;

@Data
public class Livro {
    private String sigla;
    private String nome;
    private String url;
    private Collection<Capitulo> capitulos;
}
