package com.rafaelvastag.api.library.service;

import java.util.List;

import org.springframework.stereotype.Component;

@Component
public interface EmailService {
	void sendMails(String message, List<String> customerEmailsLateLoansList);
}
