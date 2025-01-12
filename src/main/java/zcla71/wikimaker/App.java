package zcla71.wikimaker;

import java.net.URI;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import zcla71.mybible.MyBibleSQLite3;
import zcla71.mybible.MyBibleZip;
import zcla71.wikimaker.a12com.biblia.A12ComBiblia;
import zcla71.wikimaker.bibliaparresiacom.bible.BibliaParresiaComBible;
import zcla71.wikimaker.bibliapauluscombr.biblia.BibliaPaulusComBrBiblia;
import zcla71.wikimaker.liturgiadashorasonline.biblia.LiturgiaDasHorasOnlineBiblia;
import zcla71.wikimaker.vaticanva.bible.VaticanVaBibleNovaVulgataLt;

@SpringBootApplication
public class App {
	public static void main(String[] args) throws Exception {
		// Bíblias
		new A12ComBiblia();
		new BibliaParresiaComBible();
		new BibliaPaulusComBrBiblia();
		new LiturgiaDasHorasOnlineBiblia();
		new MyBibleSQLite3(new URI("https://github.com/Bible-Projects/believers-sword-next/raw/refs/heads/main/Modules/Bible/B%C3%ADblia%20Ave-Maria%201959.SQLite3"), "github_com_Bible-Projects_believers-sword-next_AveMaria1959");
        new MyBibleSQLite3(new URI("https://github.com/Bible-Projects/believers-sword-next/raw/refs/heads/main/Modules/Bible/B%C3%ADblia%20Padre%20Matos%20Soares%201950.SQLite3"), "github_com_Bible-Projects_believers-sword-next_MatosSoares1950");
		new MyBibleZip(new URI("https://www.ph4.org/_dl.php?back=bbl&a=BAM&b=mybible&c"), "ph4_org_BAM");
		new MyBibleZip(new URI("https://www.ph4.org/_dl.php?back=bbl&a=BEP&b=mybible&c"), "ph4_org_BEP");
		new MyBibleZip(new URI("https://www.ph4.org/_dl.php?back=bbl&a=BJRD&b=mybible&c"), "ph4_org_BJRD");
		new MyBibleZip(new URI("https://www.ph4.org/_dl.php?back=bbl&a=BPT%2709D&b=mybible&c"), "ph4_org_BPT_09D");
		new MyBibleZip(new URI("https://www.ph4.org/_dl.php?back=bbl&a=CNBB&b=mybible&c"), "ph4_org_CNBB");
        new MyBibleZip(new URI("https://www.ph4.org/_dl.php?back=bbl&a=DBFC&b=mybible&c"), "ph4_org_DBFC");
        new MyBibleZip(new URI("https://www.ph4.org/_dl.php?back=bbl&a=DIF&b=mybible&c"), "ph4_org_DIF");
		new VaticanVaBibleNovaVulgataLt();

		SpringApplication.run(App.class, args);
	}
}
