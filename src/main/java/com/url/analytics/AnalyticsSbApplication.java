package com.url.analytics;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing
@SpringBootApplication
public class AnalyticsSbApplication {

	public static void main(String[] args) {
		SpringApplication.run(AnalyticsSbApplication.class, args);
	}

}
