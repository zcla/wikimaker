package zcla71.wikimaker.bibliaparresiacom.bible;

import java.util.Collection;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class Livro {
    private String sigla;
    private String chapter;
    @JsonProperty("chapter_count")
    private Integer chapterCount;
    private LivroMeta meta;
    private String slug;
    private Collection<Capitulo> capitulos;
}
