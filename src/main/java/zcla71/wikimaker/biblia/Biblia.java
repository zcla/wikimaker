package zcla71.wikimaker.biblia;

import java.util.Collection;

import lombok.Data;

@Data
public class Biblia {
    private String nome;
    private String url;
    private Collection<Livro> livros;
}
