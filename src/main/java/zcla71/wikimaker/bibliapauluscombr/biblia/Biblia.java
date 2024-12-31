package zcla71.wikimaker.bibliapauluscombr.biblia;

import java.util.Collection;

import lombok.Data;

@Data
public class Biblia {
    private Testaments testaments;
    private Collection<BookChildrensBooksOrChapters> bookChildrensBooksOrChapters;
    private Collection<Chapter> chapters;
}
