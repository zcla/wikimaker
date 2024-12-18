package zcla71.wikimaker.a12com.biblia;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class Versiculo {
    @JsonProperty("book_id")
    private Integer bookId;
    @JsonProperty("book_title")
    private String bookTitle;
    @JsonProperty("book_slug")
    private String bookSlug;
    @JsonProperty("book_order")
    private Integer bookOrder;
    @JsonProperty("book_new_testament")
    private Integer bookNewTestament;
    @JsonProperty("chapter_id")
    private Integer chapterId;
    @JsonProperty("chapter_title")
    private String chapterTitle;
    @JsonProperty("chapter_order")
    private Integer chapterOrder;
    @JsonProperty("verse_id")
    private Integer verseId;
    @JsonProperty("verse_number")
    private Integer verseNumber;
    @JsonProperty("verse_title")
    private String verseTitle;
    @JsonProperty("verse_content")
    private String verseContent;
}
