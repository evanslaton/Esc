package com.esc;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class EscApplication {

	public static void main(String[] args) {
		SpringApplication.run(EscApplication.class, args);

		System.out.println("http://localhost:8080");
	}

}

