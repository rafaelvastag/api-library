package com.rafaelvastag.api.library.service;

import java.util.List;

public interface EmailService {

	void sendMails(String message, List<String> customerEmailsLateLoansList);
}
