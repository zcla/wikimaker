package zcla71.mybible;

import java.net.URI;
import java.util.Map;

public class MyBibleSQLite3GithubComBibleProjectsBelieversSwordNextAveMaria1959 extends MyBibleSQLite3 {
    private Map<String, String> SIGLA_REPLACE_MAP = null; // inicializado em padronizaSigla()

    public MyBibleSQLite3GithubComBibleProjectsBelieversSwordNextAveMaria1959() throws Exception {
        super(new URI("https://github.com/Bible-Projects/believers-sword-next/raw/refs/heads/main/Modules/Bible/B%C3%ADblia%20Ave-Maria%201959.SQLite3"), "github_com_Bible-Projects_believers-sword-next_AveMaria1959");
    }

    @Override
    protected String padronizaSigla(String sigla) {
        if (this.SIGLA_REPLACE_MAP == null) {
            this.SIGLA_REPLACE_MAP = Map.ofEntries(
                Map.entry("Jud", "Jt")
            );
        }
        String result = this.SIGLA_REPLACE_MAP.get(sigla);
        if (result == null) {
            result = sigla;
        }
        return result;
    }
}
