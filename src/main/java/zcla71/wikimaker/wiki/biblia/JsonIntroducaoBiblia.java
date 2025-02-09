package zcla71.wikimaker.wiki.biblia;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class JsonIntroducaoBiblia {
    private String texto;
    private String url;
    private LocalDateTime timestamp;

    public JsonIntroducaoBiblia(TiddlerIntroducaoBiblia tIntroducaoBiblia) {
        this.texto = tIntroducaoBiblia.getTexto();
        this.url = tIntroducaoBiblia.getUrl();
        this.timestamp = tIntroducaoBiblia.getTimestamp();
    }
}
