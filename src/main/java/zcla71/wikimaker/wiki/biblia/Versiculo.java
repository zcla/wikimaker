package zcla71.wikimaker.wiki.biblia;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class Versiculo {
    private String livro;
    private String capitulo;
    private String numero;
    private String url;
    private LocalDateTime timestamp;
    private String texto;
}
