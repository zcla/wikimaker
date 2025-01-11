package zcla71.wikimaker;

import java.net.URI;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

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
		new MyBibleZip(new URI("https://www.ph4.org/_dl.php?back=bbl&a=BAM&b=mybible&c"), "ph4_org_BAM");
		new MyBibleZip(new URI("https://www.ph4.org/_dl.php?back=bbl&a=BEP&b=mybible&c"), "ph4_org_BEP");
		new MyBibleZip(new URI("https://www.ph4.org/_dl.php?back=bbl&a=BJRD&b=mybible&c"), "ph4_org_BJRD");
		new MyBibleZip(new URI("https://www.ph4.org/_dl.php?back=bbl&a=BPT%2709D&b=mybible&c"), "ph4_org_BPT_09D");
		new MyBibleZip(new URI("https://www.ph4.org/_dl.php?back=bbl&a=CNBB&b=mybible&c"), "ph4_org_CNBB");
		new VaticanVaBibleNovaVulgataLt();

		SpringApplication.run(App.class, args);
	}
}
