package zcla71.mybible.model;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class Database {
    // Meus dados
    private String url;
    private LocalDateTime timestamp;
    private String downloadedFileName;
    
    // Módulos (https://docs.google.com/document/d/12rf4Pqy13qhnAW31uKkaWNTBDTtRbNW0s7cM0vcimlA/)
    private Bible bible = null;
    // TODO Dictionary Module
    // TODO Subheadings Module
    // TODO Cross References Module
    // Commentaries Module
    private Commentaries commentaries = null;
    // TODO Reading Plan Module
    // TODO Devotions Module
}
