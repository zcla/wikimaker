package zcla71.mybible.model;

import java.util.List;

import lombok.Data;
import zcla71.mybible.model.bible.Books;
import zcla71.mybible.model.bible.BooksAll;
import zcla71.mybible.model.bible.Info;
import zcla71.mybible.model.bible.Introductions;
import zcla71.mybible.model.bible.Stories;
import zcla71.mybible.model.bible.Verses;

@Data
public class Bible {
    private List<Info> info;
    private List<Books> books;
    private List<BooksAll> booksAll;
    private List<Verses> verses;
    private List<Introductions> introductions;
    private List<Stories> stories;
    // TODO CREATE TABLE morphology_indications (indication TEXT, applicable_to TEXT, language as TEXT, meaning TEXT)
    // TODO CREATE TABLE morphology_topics (indication TEXT, topic TEXT)
}
