package zcla71.wikimaker.bibliapauluscombr.biblia;

import java.time.LocalDateTime;
import java.util.Collection;

import lombok.Data;

@Data
public class Chapter {
    // Propriedades da API
    private String chapter;
    private String bookChildren;
    private String bookChildrenAbbreviation;
    private String book;
    private String testament;
    private String testamentAbbreviation;
    private String bible;
    private String title;
    private ChapterView view;
    private Collection<ChapterVersicle> versicles;
    private Collection<ChapterFootNote> footnotes;
    private Integer prev_chapter;
    private String prev_chapter_url;
    private String next_chapter; // Devia ser Integer, mas às vezes vem um texto. Ex.: Gn 50: "next_chapter": "Próximo Livro".
    private Object next_chapter_url; // Devia ser String, mas às vezes vem um objeto (?!). Ex.: Dt 34: "next_chapter_url": { "chapter": null, "url": null }
    // Propriedades "minhas"
    private String nome;
    private String urlSite;
    private String url;
    private LocalDateTime timestamp;
}
