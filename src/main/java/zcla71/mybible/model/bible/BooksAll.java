package zcla71.mybible.model.bible;

import lombok.Data;

@Data
// CREATE TABLE books_all (
public class BooksAll {
    // book_number NUMERIC,
    private Integer book_number;
    // book_color TEXT,
    private String book_color;
    // short_name TEXT,
    private String short_name;
    // title TEXT,
    private String title;
    // long_name TEXT,
    private String long_name;
    // is_present BOOLEAN,
    private Integer is_present; // Can not set java.lang.Boolean field zcla71.mybible.model.bible.BooksAll.is_present to java.lang.Integer
    // sorting_order NUMERIC
    private Integer sorting_order;
    // )
}
