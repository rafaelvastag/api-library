package com.rafaelvastag.api.library;

import java.util.Arrays;
import java.util.List;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;

import com.rafaelvastag.api.library.service.EmailService;

@SpringBootApplication
@EnableScheduling
public class LibraryApplication {
	
	@Autowired
	private EmailService emailService;
	
	@Bean
	public ModelMapper modelMapper() {
		return new ModelMapper();
	}
	
	@Bean
	public CommandLineRunner runner() {
		return args -> {
			List<String> emails = Arrays.asList("libraryvastag-7a4ace@inbox.mailtrap.io");
			emailService.sendMails("Testing email service", emails);	
		};
	}
	
	public static void main(String[] args) {
		SpringApplication.run(LibraryApplication.class, args);
	}

}
