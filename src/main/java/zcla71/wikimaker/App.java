package zcla71.wikimaker;

import java.io.IOException;
import java.net.URISyntaxException;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import zcla71.wikimaker.a12com.biblia.A12ComBiblia;
import zcla71.wikimaker.bibliaparresiacom.bible.BibliaParresiaComBible;
import zcla71.wikimaker.liturgiadashorasonline.biblia.LiturgiaDasHorasOnlineBiblia;

@SpringBootApplication
public class App {
	public static void main(String[] args) throws IOException, ClassNotFoundException, URISyntaxException {
		// Bíblias
		new A12ComBiblia();
		new BibliaParresiaComBible();
		new LiturgiaDasHorasOnlineBiblia();

		SpringApplication.run(App.class, args);
	}
}
