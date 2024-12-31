package zcla71.wikimaker.bibliapauluscombr.biblia;

import java.time.LocalDateTime;
import java.util.Collection;

import lombok.Data;

@Data
public class Testaments {
    // Propriedades da API
    private Collection<Testament> data;
    // Propriedades "minhas"
    private String nome;
    private String urlSite;
    private String url;
    private LocalDateTime timestamp;
}
