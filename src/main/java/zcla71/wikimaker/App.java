package zcla71.wikimaker;

import java.io.IOException;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import zcla71.wikimaker.liturgiadashorasonline.biblia.LiturgiaDasHorasOnlineBiblia;

@SpringBootApplication
public class App {
	public static void main(String[] args) throws IOException {
		// Bíblias
		new LiturgiaDasHorasOnlineBiblia();
		// vatican.va https://www.vatican.va/archive/bible/index_po.htm
		//   [cn] https://www.vatican.va/chinese/bibbia.htm
		//   [en] https://www.vatican.va/archive/ENG0839/_INDEX.HTM
		//   [es] https://www.vatican.va/archive/ESL0506/_INDEX.HTM
		//   [it] https://www.vatican.va/archive/ITA0001/_INDEX.HTM
		//   [lt] https://www.vatican.va/archive/bible/nova_vulgata/documents/nova-vulgata_index_lt.html
		// clerus.org https://www.clerus.org/bibliaclerusonline/pt/index.htm
		//   [pt] https://www.clerus.org/bibliaclerusonline/pt/66c.htm
		// intratext.com
		//   [vários] https://www.intratext.com/BIBLE/default.htm
		// newadvent.org
		//   [el/en/lt] https://www.newadvent.org/bible/
		// paulus.com.br
		//   [pt] https://www.paulus.com.br/biblia-pastoral/_INDEX.HTM
		// perseus.tufts.edu
		//   !!!XML!!! [lt] https://www.perseus.tufts.edu/hopper/text?doc=Perseus%3atext%3a1999.02.0060 (Jerome. Vulgate Bible.)
		//   !!!XML!!! [el] https://www.perseus.tufts.edu/hopper/text?doc=Perseus%3atext%3a1999.01.0155 (The New Testament in the original Greek.)
		//   !!!XML!!! [en] https://www.perseus.tufts.edu/hopper/text?doc=Perseus%3atext%3a1999.01.0156 (World English Bible.)
		// scaife.perseus.org
		//   !!!XML!!! [el/en] https://scaife.perseus.org/library/urn:cts:greekLit:tlg0527/ (AT Septuaginta / World English Bible)
		//				       https://scaife.perseus.org/library/urn:cts:greekLit:tlg0031/ (The New Testament in the original Greek / World English Bible)
		// usccb.org (OFICIAL EUA)
		//	 [en] https://www.usccb.org/offices/new-american-bible/books-bible (New American Bible)
		// biblegateway.com
		//   [en] https://www.biblegateway.com/passage/?search=Genesis%201&version=NCB (New Catholic Bible)
		//   [en] https://www.biblegateway.com/passage/?search=Genesis%201&version=RSVCE (Revised Standard Version Catholic Edition)
		//   [en] https://www.biblegateway.com/passage/?search=Genesis%201&version=NRSVCE (New Revised Standard Version Catholic Edition)

		SpringApplication.run(App.class, args);
	}
}
