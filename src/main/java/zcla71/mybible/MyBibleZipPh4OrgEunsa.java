package zcla71.mybible;

import java.net.URI;
import java.util.Map;

public class MyBibleZipPh4OrgEunsa extends MyBibleZip {
    private Map<String, String> SIGLA_REPLACE_MAP = null; // inicializado em padronizaSigla()

    public MyBibleZipPh4OrgEunsa() throws Exception {
        super(new URI("https://www.ph4.org/_dl.php?back=bbl&a=EUNSA&b=mybible&c"), "ph4_org_EUNSA");
    }

    @Override
    protected String padronizaSigla(String sigla) {
        if (this.SIGLA_REPLACE_MAP == null) {
            this.SIGLA_REPLACE_MAP = Map.ofEntries(
                Map.entry("Gén", "Gn"),
                Map.entry("Éx", "Ex"),
                Map.entry("Lev", "Lv"),
                Map.entry("Núm", "Nm"),
                Map.entry("Jos", "Js"),
                Map.entry("Jue", "Jz"),
                Map.entry("Rut", "Rt"),
                Map.entry("1 Sam", "1Sm"),
                Map.entry("2 Sam", "2Sm"),
                Map.entry("1 Re", "1Rs"),
                Map.entry("2 Re", "2Rs"),
                Map.entry("1 Crón", "1Cr"),
                Map.entry("2 Crón", "2Cr"),
                Map.entry("Neh", "Ne"),
                Map.entry("Tob", "Tb"),
                Map.entry("Jdt", "Jt"),
                Map.entry("Job", "Jó"),
                Map.entry("Sal", "Sl"),
                Map.entry("Prov", "Pr"),
                Map.entry("Cant", "Ct"),
                Map.entry("Sab", "Sb"),
                Map.entry("Jer", "Jr"),
                Map.entry("Lam", "Lm"),
                Map.entry("Bar", "Br"),
                Map.entry("Dan", "Dn"),
                Map.entry("Abd", "Ab"),
                Map.entry("Jon", "Jn"),
                Map.entry("Miq", "Mq"),
                Map.entry("Nah", "Na"),
                Map.entry("Sof", "Sf"),
                Map.entry("Zac", "Zc"),
                Map.entry("Mal", "Ml"),
                Map.entry("1 Mac", "1Mc"),
                Map.entry("2 Mac", "2Mc"),
                Map.entry("Jn", "Jo"),
                Map.entry("Hch", "At"),
                Map.entry("Rom", "Rm"),
                Map.entry("1 Cor", "1Cor"),
                Map.entry("2 Cor", "2Cor"),
                Map.entry("Gal", "Gl"),
                Map.entry("Flp", "Fl"),
                Map.entry("Col", "Cl"),
                Map.entry("1 Tes", "1Ts"),
                Map.entry("2 Tes", "2Ts"),
                Map.entry("1 Tim", "1Tm"),
                Map.entry("2 Tim", "2Tm"),
                Map.entry("Tit", "Tt"),
                Map.entry("Flm", "Fm"),
                Map.entry("Heb", "Hb"),
                Map.entry("Sant", "Tg"),
                Map.entry("1 Pe", "1Pd"),
                Map.entry("2 Pe", "2Pd"),
                Map.entry("1 Jn", "1Jo"),
                Map.entry("2 Jn", "2Jo"),
                Map.entry("3 Jn", "3Jo"),
                Map.entry("Jt", "Jd")
            );
        }
        String result = this.SIGLA_REPLACE_MAP.get(sigla);
        if (result == null) {
            result = sigla;
        }
        return result;
    }
}
