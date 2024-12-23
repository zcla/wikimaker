package zcla71.mybible.model;

import java.util.Collection;

import lombok.Data;
import zcla71.mybible.model.bible.Books;
import zcla71.mybible.model.bible.BooksAll;
import zcla71.mybible.model.bible.Info;
import zcla71.mybible.model.bible.Introductions;
import zcla71.mybible.model.bible.Stories;
import zcla71.mybible.model.bible.Verses;

@Data
public class Bible {
    private Collection<Info> info;
    private Collection<Books> books;
    private Collection<BooksAll> booksAll;
    private Collection<Verses> verses;
    private Collection<Introductions> introductions;
    private Collection<Stories> stories;
    // TODO CREATE TABLE morphology_indications (indication TEXT, applicable_to TEXT, language as TEXT, meaning TEXT)
    // TODO CREATE TABLE morphology_topics (indication TEXT, topic TEXT)
}
