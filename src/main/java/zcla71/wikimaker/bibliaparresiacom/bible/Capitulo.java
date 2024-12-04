package zcla71.wikimaker.bibliaparresiacom.bible;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;

import lombok.Data;

@Data
public class Capitulo {
    private Integer numero;
    private LocalDateTime timestamp;
    private Collection<Versiculo> versiculos;

    public Capitulo(Integer numero) {
        this.numero = numero;
        this.timestamp = LocalDateTime.now();
        this.versiculos = new ArrayList<>();
    }
}
