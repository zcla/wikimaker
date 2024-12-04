package zcla71.wikimaker.bibliaparresiacom.bible;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class Versiculo {
    private Integer chapter;
    @JsonProperty("chapter_slug")
    private String chapterSlug;
    private Integer number;
    private String text;
}
