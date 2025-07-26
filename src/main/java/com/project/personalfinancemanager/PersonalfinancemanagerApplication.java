package com.project.personalfinancemanager;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class PersonalfinancemanagerApplication {

	public static void main(String[] args) {
		SpringApplication.run(PersonalfinancemanagerApplication.class, args);
	}

}
