package com.Madhav.bharatYatra;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class BharatYatraApplication {

	public static void main(String[] args) {
		SpringApplication.run(BharatYatraApplication.class, args);
	}

}
