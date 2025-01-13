package zcla71.mybible;

import java.net.URI;
import java.util.Map;

public class MyBibleZipPh4OrgBep extends MyBibleZip {
    private Map<String, String> SIGLA_REPLACE_MAP = null; // inicializado em padronizaSigla()

    public MyBibleZipPh4OrgBep() throws Exception {
        super(new URI("https://www.ph4.org/_dl.php?back=bbl&a=BEP&b=mybible&c"), "ph4_org_BEP");
    }

    @Override
    protected String padronizaSigla(String sigla) {
        if (this.SIGLA_REPLACE_MAP == null) {
            this.SIGLA_REPLACE_MAP = Map.ofEntries(
                Map.entry("Êx", "Ex"),
                Map.entry("Ed", "Esd"),
                Map.entry("Et", "Est"),
                Map.entry("Pv", "Pr"),
                Map.entry("Ec", "Ecl"),
                Map.entry("Ob", "Ab"),
                Map.entry("Hc", "Hab"),
                Map.entry("1Co", "1Cor"),
                Map.entry("2Co", "2Cor"),
                Map.entry("Fp", "Fl"),
                Map.entry("1Pe", "1Pd"),
                Map.entry("2Pe", "2Pd")
            );
        }
        String result = this.SIGLA_REPLACE_MAP.get(sigla);
        if (result == null) {
            result = sigla;
        }
        return result;
    }
}
