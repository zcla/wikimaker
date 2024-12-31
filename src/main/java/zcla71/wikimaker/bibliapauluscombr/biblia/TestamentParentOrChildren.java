package zcla71.wikimaker.bibliapauluscombr.biblia;

import lombok.Data;

@Data
public class TestamentParentOrChildren {
    private Integer id;
    private Integer parent_id;
    private String name;
    private String abbreviation;
    private Integer order;
    private Integer testament_id;
    private Integer bible_id;
    private Integer language_id;
    private Integer has_introduction;
}
