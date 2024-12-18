package zcla71.wikimaker.a12com.biblia;

import java.time.LocalDateTime;
import java.util.Collection;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class Capitulo {
    // Propriedades da API
    private Integer error;
    private Collection<Versiculo> data;
    // Propriedades "minhas"
    private Integer numero;
    private String url;
    private LocalDateTime timestamp;
}
