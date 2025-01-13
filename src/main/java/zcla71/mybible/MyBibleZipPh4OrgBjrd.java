package zcla71.mybible;

import java.net.URI;
import java.util.Map;

public class MyBibleZipPh4OrgBjrd extends MyBibleZip {
    private Map<String, String> SIGLA_REPLACE_MAP = null; // inicializado em padronizaSigla()

    public MyBibleZipPh4OrgBjrd() throws Exception {
        super(new URI("https://www.ph4.org/_dl.php?back=bbl&a=BJRD&b=mybible&c"), "ph4_org_BJRD");
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
