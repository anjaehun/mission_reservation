package com.example.mission;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@ComponentScan("com.example.mission")
@EnableScheduling
public class MissionApplication {

	public static void main(String[] args) {
		SpringApplication.run(MissionApplication.class, args);
	}

}
