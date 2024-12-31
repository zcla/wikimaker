package zcla71.wikimaker.bibliapauluscombr.biblia;

import java.util.Collection;

import lombok.Data;

@Data
public class Testament {
    private Integer id;
    private String name;
    private String introduction;
    private String bible;
    private String language;
    private Integer status;
    private String link;
    private Collection<TestamentParentOrChildren> parent;
    private Collection<TestamentParentOrChildren> children;
}
