package zcla71.wikimaker;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import zcla71.mybible.MyBibleSQLite3GithubComBibleProjectsBelieversSwordNextAveMaria1959;
import zcla71.mybible.MyBibleSQLite3GithubComBibleProjectsBelieversSwordNextMatosSoares1950;
import zcla71.mybible.MyBibleZipPh4OrgBam;
import zcla71.mybible.MyBibleZipPh4OrgBep;
import zcla71.mybible.MyBibleZipPh4OrgBjrd;
import zcla71.mybible.MyBibleZipPh4OrgBpt09d;
import zcla71.mybible.MyBibleZipPh4OrgCnbb;
import zcla71.mybible.MyBibleZipPh4OrgDbfc;
import zcla71.mybible.MyBibleZipPh4OrgDif;
import zcla71.mybible.MyBibleZipPh4OrgEunsa;
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
		new MyBibleSQLite3GithubComBibleProjectsBelieversSwordNextAveMaria1959();
        new MyBibleSQLite3GithubComBibleProjectsBelieversSwordNextMatosSoares1950();
		new MyBibleZipPh4OrgBam();
		new MyBibleZipPh4OrgBep();
		new MyBibleZipPh4OrgBjrd();
		new MyBibleZipPh4OrgBpt09d();
		new MyBibleZipPh4OrgCnbb();
        new MyBibleZipPh4OrgDbfc();
        new MyBibleZipPh4OrgDif();
		new MyBibleZipPh4OrgEunsa();
		new VaticanVaBibleNovaVulgataLt();

		SpringApplication.run(App.class, args);
	}
}
