package zcla71.mybible.model.bible;

import lombok.Data;

@Data
// CREATE TABLE introductions (
public class Introductions {
    // book_number NUMERIC,
    private Object book_number; // Can not set java.lang.Integer field zcla71.mybible.model.bible.Introductions.book_number to java.lang.String
                                // Can not set java.lang.String field zcla71.mybible.model.bible.Introductions.book_number to java.lang.Integer
    // introduction TEXT
    private String introduction;
    // )
}
