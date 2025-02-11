package zcla71.mybible.model;

import java.util.Collection;

import lombok.Data;
import zcla71.mybible.model.commentaries.Info;
import zcla71.mybible.model.common.ContentFragments;

@Data
public class Commentaries {
    private Collection<Info> info;
    private Collection<ContentFragments> contentFragments;
    private Collection<zcla71.mybible.model.commentaries.Commentaries> commentaries;
    // TODO CREATE TABLE books (book_number NUMERIC, short_name TEXT)
}
