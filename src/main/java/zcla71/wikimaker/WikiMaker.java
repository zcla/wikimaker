package zcla71.wikimaker;

import java.io.File;

import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;
import zcla71.utils.JacksonUtils;
import zcla71.wikimaker.wiki.biblia.WikiBiblia;

@Slf4j
public abstract class WikiMaker<T> {
    // Retorna o ID da bíblia
    protected abstract String getId();
    // Retorna a classe do objeto com os dados de download
    protected abstract Class<T> getDownloadClass();
    // Retorna o objeto contendo o download dos dados
    protected abstract T doDownload() throws Exception;
    // Cria o wiki a partir do download
    protected abstract WikiBiblia makeWiki(T download);

    protected WikiMaker() throws Exception {
        log.info(getId());

        ObjectMapper objectMapper = JacksonUtils.getObjectMapperInstance();
        JacksonUtils.enableJavaTime(objectMapper);

        File downloadFile = new File(this.getDownloadFileName());
        T download = null;
        if (downloadFile.exists()) {
            log.info("\tDownload já gerado. Carregando.");
            download = objectMapper.readValue(downloadFile, this.getDownloadClass());
        } else {
            log.info("\tFazendo download.");
            download = doDownload();
            objectMapper.writer(JacksonUtils.getPrettyPrinter()).writeValue(downloadFile, download);
        }

        File wikiOutputFile = new File(this.getWikiFileName());
        WikiBiblia wiki = null;
        if (wikiOutputFile.exists()) {
            log.info("\tWiki já gerado. Carregando.");
            wiki = new WikiBiblia(wikiOutputFile);
        } else {
            wiki = makeWiki(download);
            log.info("\tSalvando wiki");
            wiki.saveAsWiki(wikiOutputFile);
        }

        log.info("\tSalvando json");
        wiki.saveAsJson(new File(this.getJsonFileName()));
    }

    private String getDownloadFileName() {
        return "./data/download/" + this.getId() + ".json";
    }

    private String getWikiFileName() {
        return "./data/wiki/" + this.getId() + ".html";
    }

    private String getJsonFileName() {
        return "./data/json/" + this.getId() + ".json";
    }
}
