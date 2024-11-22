package zcla71.wikimaker.bibliajerusalem;

import java.io.File;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class BibliaJerusalem {
    // TODO Só pra acelerar no desenvolvimento; remover ao final.
    public static void main(String[] args) {
        new BibliaJerusalem();
    }

    @SuppressWarnings("unused") // Só pra fins de documentação
    private static final String SCRIPT_FILE = "./tampermonkey/BibliaJerusalem.json";
    private static final String JSON_INPUT_FILE = "./data/BibliaJerusalem.json";
    private static final String WIKI_EMPTY_FILE = "./data/empty.html";
    private static final String WIKI_OUTPUT_FILE = "./data/BibliaJerusalem.html";

    public BibliaJerusalem() {
        File wikiOutputFile = new File(WIKI_OUTPUT_FILE);
        if (wikiOutputFile.exists()) {
            log.info("Wiki já gerado.");
            return;
        }

        File jsonInputFile = new File(JSON_INPUT_FILE);
        if (!jsonInputFile.exists()) {
            log.error("JSON de origem não encontrado.");
            return;
        }

        File wikiEmptyFile = new File(WIKI_EMPTY_FILE);
        if (!wikiEmptyFile.exists()) {
            log.error("Wiki vazio não encontrado.");
            return;
        }
    }
}
