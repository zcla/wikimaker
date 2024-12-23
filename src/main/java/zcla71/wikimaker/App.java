package zcla71.wikimaker;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.URI;
import java.net.URISyntaxException;
import java.sql.SQLException;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.fasterxml.jackson.core.exc.StreamReadException;
import com.fasterxml.jackson.databind.DatabindException;

import zcla71.mybible.MyBible;
import zcla71.wikimaker.a12com.biblia.A12ComBiblia;
import zcla71.wikimaker.bibliaparresiacom.bible.BibliaParresiaComBible;
import zcla71.wikimaker.liturgiadashorasonline.biblia.LiturgiaDasHorasOnlineBiblia;

@SpringBootApplication
public class App {
	public static void main(String[] args) throws StreamReadException, DatabindException, IOException, URISyntaxException, InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException, NoSuchFieldException, SQLException {
		// Bíblias
		new A12ComBiblia();
		new BibliaParresiaComBible();
		new LiturgiaDasHorasOnlineBiblia();
		new MyBible(new URI("https://www.ph4.org/_dl.php?back=bbl&a=BAM&b=mybible&c"), "ph4_org_BAM");
		new MyBible(new URI("https://www.ph4.org/_dl.php?back=bbl&a=BEP&b=mybible&c"), "ph4_org_BEP");
		new MyBible(new URI("https://www.ph4.org/_dl.php?back=bbl&a=BJRD&b=mybible&c"), "ph4_org_BJRD");
		new MyBible(new URI("https://www.ph4.org/_dl.php?back=bbl&a=BPT%2709D&b=mybible&c"), "ph4_org_BPT_09D");
		new MyBible(new URI("https://www.ph4.org/_dl.php?back=bbl&a=CNBB&b=mybible&c"), "ph4_org_CNBB");

		SpringApplication.run(App.class, args);
	}
}
