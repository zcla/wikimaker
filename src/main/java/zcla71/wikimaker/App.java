package zcla71.wikimaker;

import java.io.IOException;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import zcla71.wikimaker.bibliajerusalem.BibliaJerusalem;

@SpringBootApplication
public class App {
	public static void main(String[] args) throws IOException {
		new BibliaJerusalem();
		SpringApplication.run(App.class, args);
	}
}
