package zcla71.tiddlywiki;

public class TiddlerDuplicadaException extends TiddlyWikiException {
    public TiddlerDuplicadaException(String title) {
        super("Tentativa de incluir tiddler duplicada: " + title);
    }
}
