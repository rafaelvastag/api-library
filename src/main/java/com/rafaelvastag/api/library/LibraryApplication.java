package com.rafaelvastag.api.library;

import org.modelmapper.ModelMapper;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

@SpringBootApplication
@EnableScheduling
public class LibraryApplication {
	
	@Bean
	public ModelMapper modelMapper() {
		return new ModelMapper();
	}
	
	@Scheduled(cron = "0 55 15 1/1 * ? ")
	public void scheduledTasks() {

	}

	public static void main(String[] args) {
		SpringApplication.run(LibraryApplication.class, args);
	}

}
