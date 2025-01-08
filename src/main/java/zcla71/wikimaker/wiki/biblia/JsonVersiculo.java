package zcla71.wikimaker.wiki.biblia;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class JsonVersiculo {
    private String numero;
    private String texto;
    private String url;
    private LocalDateTime timestamp;

    public JsonVersiculo(TiddlerVersiculo tVersiculo) {
        this.numero = tVersiculo.getNumero();
        this.texto = tVersiculo.getTexto();
        this.url = tVersiculo.getUrl();
        this.timestamp = tVersiculo.getTimestamp();
    }
}
