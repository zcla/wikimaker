package zcla71.mybible.model;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Data;

@Data
// Documentação: https://docs.google.com/document/d/12rf4Pqy13qhnAW31uKkaWNTBDTtRbNW0s7cM0vcimlA/
public class Database {
    // Meus dados
    private String url;
    private LocalDateTime timestamp;
    private String downloadedFileName;
    
    // Módulos
    private Bible bible = null;
    // TODO Dictionary Module
    // TODO Subheadings Module
    // TODO Cross References Module
    // Commentaries Module
    private Commentaries commentaries = null;
    // TODO Reading Plan Module
    // TODO Devotions Module

    @JsonIgnore
    public String getName() {
        return bible.getInfo().stream().filter(i -> i.getName().equals("description")).findFirst().get().getValue();
    }
}
