package com.hermes;

import com.hermes.autoconfigure.EnableHermes;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@EnableHermes
public class HermesServerApplication {

	public static void main(String[] args) {
		SpringApplication.run(HermesServerApplication.class, args);
	}

}
