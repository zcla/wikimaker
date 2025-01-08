package zcla71.wikimaker.wiki.biblia;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;

import lombok.Data;

@Data
public class JsonCapitulo {
    private String numero;
    private String url;
    private LocalDateTime timestamp;
    private Collection<JsonVersiculo> versiculos;

    public JsonCapitulo(TiddlerCapitulo tCapitulo) {
        this.numero = tCapitulo.getNumero();
        this.url = tCapitulo.getUrl();
        this.timestamp = tCapitulo.getTimestamp();
        this.versiculos = new ArrayList<>();
    }
}
