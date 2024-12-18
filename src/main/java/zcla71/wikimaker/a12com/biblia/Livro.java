package zcla71.wikimaker.a12com.biblia;

import java.util.Collection;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class Livro {
    // Propriedades da API
    private Integer id;
    private String title;
    private String slug;
    @JsonProperty("chapters_total")
    private Integer chaptersTotal;
    @JsonProperty("new_testament")
    private Integer newTestament;
    // Propriedades "minhas"
    private String sigla;
    private Collection<Capitulo> capitulos;
}
