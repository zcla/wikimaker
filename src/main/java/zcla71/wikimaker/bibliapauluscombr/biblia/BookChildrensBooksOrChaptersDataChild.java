package zcla71.wikimaker.bibliapauluscombr.biblia;

import lombok.Data;

@Data
public class BookChildrensBooksOrChaptersDataChild {
    private Integer id;
    private String name;
    private String bible;
    private String language;
    private String testament;
    private Integer has_introduction;
    private String parent;
    private String links;
    private String introduction;
}
