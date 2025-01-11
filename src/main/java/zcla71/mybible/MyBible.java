package zcla71.mybible;

import zcla71.mybible.model.Database;
import zcla71.wikimaker.WikiMaker;

// Documentação: https://docs.google.com/document/d/12rf4Pqy13qhnAW31uKkaWNTBDTtRbNW0s7cM0vcimlA/
public abstract class MyBible extends WikiMaker<Database> {
    protected MyBible(Object... arguments) throws Exception {
        super(arguments);
    }

    @Override
    protected Class<Database> getDownloadClass() {
        return Database.class;
    }
}
