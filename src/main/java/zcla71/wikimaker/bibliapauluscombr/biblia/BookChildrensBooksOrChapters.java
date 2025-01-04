package zcla71.wikimaker.bibliapauluscombr.biblia;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class BookChildrensBooksOrChapters {
    // Propriedades da API
    private BookChildrensBooksOrChaptersData data;
    // Propriedades "minhas"
    private String urlSite;
    private String url;
    private LocalDateTime timestamp;
}
