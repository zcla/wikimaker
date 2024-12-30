package zcla71.wikimaker.vaticanva.bible;

import java.time.LocalDateTime;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class Livro {
    private String sigla;
    private String nome;
    private String url;
    private LocalDateTime timestamp;
    private String html;

    public Livro(String sigla, String nome, String url) {
        this.sigla = sigla;
        this.nome = nome;
        this.url = url;
        this.timestamp = LocalDateTime.now();
    }
}
