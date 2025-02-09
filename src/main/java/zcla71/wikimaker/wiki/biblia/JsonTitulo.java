package zcla71.wikimaker.wiki.biblia;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class JsonTitulo {
    private String nivel;
    private String texto;
    private String url;
    private LocalDateTime timestamp;

    public JsonTitulo(TiddlerTitulo tTitulo) {
        this.nivel = tTitulo.getNivel();
        this.texto = tTitulo.getTexto();
        this.url = tTitulo.getUrl();
        this.timestamp = tTitulo.getTimestamp();
    }
}
