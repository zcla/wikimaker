package zcla71.biblia;

import java.util.Collection;

import lombok.Data;

@Data
public class Capitulo {
    private String numero;
    private Collection<Versiculo> versiculos;
}
