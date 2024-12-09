package zcla71.wikimaker.wiki.biblia;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class Capitulo {
    private String livro;
    private String numero;
    private String url;
    private LocalDateTime timestamp;
    private String texto;
}
