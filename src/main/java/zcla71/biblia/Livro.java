package zcla71.biblia;

import java.util.Collection;

import lombok.Data;

@Data
public class Livro {
    private String sigla;
    private String nome;
    private Collection<Capitulo> capitulos;
}
