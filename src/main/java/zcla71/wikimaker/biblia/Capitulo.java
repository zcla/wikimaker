package zcla71.wikimaker.biblia;

import java.util.Collection;

import lombok.Data;

@Data
public class Capitulo {
    private String numero;
    private String url;
    private Collection<String> html;
}
