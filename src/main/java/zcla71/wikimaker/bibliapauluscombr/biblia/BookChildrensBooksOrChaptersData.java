package zcla71.wikimaker.bibliapauluscombr.biblia;

import java.util.Collection;
import java.util.Map;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import lombok.Data;

@Data
public class BookChildrensBooksOrChaptersData {
    private Integer id;
    private String name;
    private String bible;
    private String testament;
    private Integer has_introduction;
    private String parent;
    private String title;
    private String introduction;
    private String introduction_footnote_name;
    private String introduction_footnote;
    private Collection<BookChildrensBooksOrChaptersDataChild> childrens;
    @JsonDeserialize(using = BookChildrensBooksOrChaptersDataChaptersDeserializer.class)
    private Map<String, String> chapters;
}
