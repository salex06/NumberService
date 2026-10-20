package github.salex9206.number_service;

import org.springframework.boot.SpringApplication;

public class TestNumberServiceApplication {

	public static void main(String[] args) {
		SpringApplication.from(NumberServiceApplication::main).with(TestcontainersConfiguration.class).run(args);
	}

}
