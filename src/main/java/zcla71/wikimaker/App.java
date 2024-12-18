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
		// vatican.va https://www.vatican.va/archive/bible/index_po.htm
		//   [cn] https://www.vatican.va/chinese/bibbia.htm
		//   [en] https://www.vatican.va/archive/ENG0839/_INDEX.HTM
		//   [es] https://www.vatican.va/archive/ESL0506/_INDEX.HTM
		//   [it] https://www.vatican.va/archive/ITA0001/_INDEX.HTM
		//   [lt] https://www.vatican.va/archive/bible/nova_vulgata/documents/nova-vulgata_index_lt.html
		// bibliacatolica.com.br
		//   [vários]
		//   [pt] https://www.bibliacatolica.com.br/biblia-ave-maria/genesis/1/ Ave Maria
		//   [pt] https://www.bibliacatolica.com.br/biblia-matos-soares-1956/genesis/1/ Matos Soares 1956
		// clerus.org https://www.clerus.org/bibliaclerusonline/pt/index.htm
		//   [de] https://www.clerus.org/bibliaclerusonline/de/66c.htm
		//   [en] https://www.clerus.org/bibliaclerusonline/en/66c.htm
		//   [es] https://www.clerus.org/bibliaclerusonline/es/66c.htm
		//   [fr] https://www.clerus.org/bibliaclerusonline/fr/66c.htm
		//   [it] https://www.clerus.org/bibliaclerusonline/it/66c.htm
		//   [pt] https://www.clerus.org/bibliaclerusonline/pt/66c.htm
		// die-bibel.de
		//   [el] https://www.die-bibel.de/en/bible/NA28/
		// intratext.com
		//   [várias] https://www.intratext.com/BIBLE/default.htm
		//   [pt] https://www.intratext.com/IXT/POR0013/__P1.HTM "A Bíblia" - parece muito fácil de baixar
		// newadvent.org
		//   [el/en/lt] https://www.newadvent.org/bible/
		// paulus.com.br
		//   [pt] https://www.paulus.com.br/biblia-pastoral/_INDEX.HTM
		//   [pt] https://biblia.paulus.com.br/
		// perseus.tufts.edu
		//   !!!XML!!! [lt] https://www.perseus.tufts.edu/hopper/text?doc=Perseus%3atext%3a1999.02.0060 (Jerome. Vulgate Bible.)
		//   !!!XML!!! [el] https://www.perseus.tufts.edu/hopper/text?doc=Perseus%3atext%3a1999.01.0155 (The New Testament in the original Greek.)
		//   !!!XML!!! [en] https://www.perseus.tufts.edu/hopper/text?doc=Perseus%3atext%3a1999.01.0156 (World English Bible.)
		// scaife.perseus.org
		//   !!!XML!!! [el/en] https://scaife.perseus.org/library/urn:cts:greekLit:tlg0527/ (AT Septuaginta / World English Bible)
		//				       https://scaife.perseus.org/library/urn:cts:greekLit:tlg0031/ (The New Testament in the original Greek / World English Bible)
		// tuapalavra.com.br
		//   [várias] https://amp.tuapalavra.com.br/pt-BR
		//   [pt] https://amp.tuapalavra.com.br/pt-BR/CNBB CNBB
		// usccb.org (OFICIAL EUA)
		//	 [en] https://www.usccb.org/offices/new-american-bible/books-bible (New American Bible)
		// biblegateway.com
		//   [en] https://www.biblegateway.com/passage/?search=Genesis%201&version=NCB (New Catholic Bible)
		//   [en] https://www.biblegateway.com/passage/?search=Genesis%201&version=RSVCE (Revised Standard Version Catholic Edition)
		//   [en] https://www.biblegateway.com/passage/?search=Genesis%201&version=NRSVCE (New Revised Standard Version Catholic Edition)

		SpringApplication.run(App.class, args);
	}
}
