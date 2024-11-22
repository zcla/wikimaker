package zcla71.wikimaker;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import zcla71.wikimaker.bibliajerusalem.BibliaJerusalem;

@SpringBootApplication
public class App {
	public static void main(String[] args) {
		new BibliaJerusalem();
		SpringApplication.run(App.class, args);
	}
}
