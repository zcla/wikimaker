package zcla71.wikimaker.wiki.biblia;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class Biblia {
    private String nome;
    private String url;
    private LocalDateTime timestamp;
    private String texto;
}
