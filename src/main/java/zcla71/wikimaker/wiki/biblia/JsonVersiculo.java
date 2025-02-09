package zcla71.wikimaker.wiki.biblia;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;

import lombok.Data;

@Data
public class JsonVersiculo {
    private String numero;
    private String texto;
    private String url;
    private LocalDateTime timestamp;
    private Collection<JsonTitulo> titulos;

    public JsonVersiculo(TiddlerVersiculo tVersiculo) {
        this.numero = tVersiculo.getNumero();
        this.texto = tVersiculo.getTexto();
        this.url = tVersiculo.getUrl();
        this.timestamp = tVersiculo.getTimestamp();
        this.titulos = new ArrayList<>();
    }
}
