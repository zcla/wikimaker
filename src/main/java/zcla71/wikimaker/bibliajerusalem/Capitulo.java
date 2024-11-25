package zcla71.wikimaker.bibliajerusalem;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class Capitulo {
    private String numero;
    private String url;
    private LocalDateTime timestamp;
    private Collection<String> html;

    public Capitulo(String numero, String url) {
        this.numero = numero;
        this.url = url;
        this.timestamp = LocalDateTime.now();
        this.html = new ArrayList<>();
    }
}
