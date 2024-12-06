package zcla71.wikimaker.bibliaparresiacom.bible;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class Capitulo {
    private Integer numero;
    private String url;
    private LocalDateTime timestamp;
    private Collection<Versiculo> versiculos;

    public Capitulo(Integer numero, String url) {
        this.numero = numero;
        this.url = url;
        this.timestamp = LocalDateTime.now();
        this.versiculos = new ArrayList<>();
    }
}
