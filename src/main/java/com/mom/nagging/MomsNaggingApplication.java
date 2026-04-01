package com.mom.nagging;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing
@SpringBootApplication
public class MomsNaggingApplication {

	public static void main(String[] args) {
		SpringApplication.run(MomsNaggingApplication.class, args);
	}

}
