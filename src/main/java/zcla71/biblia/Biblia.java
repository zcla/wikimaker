package zcla71.biblia;

import java.util.Collection;

import lombok.Data;

@Data
public class Biblia {
    private String id;
    private String nome;
    private String url;
    private Collection<Livro> livros;
}
