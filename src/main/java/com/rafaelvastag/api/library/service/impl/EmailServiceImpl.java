package com.rafaelvastag.api.library.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import com.rafaelvastag.api.library.service.EmailService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class EmailServiceImpl implements EmailService {

	private final JavaMailSender javaMailSender;

	@Value("${application.mail.default.remetent}")
	private String mailRemetent;

	@Value("${application.mail.default.subject-message}")
	private String mailSubject;

	@Override
	public void sendMails(String message, List<String> customerEmailsLateLoansList) {
		String[] listOfEmailsToBeSend = customerEmailsLateLoansList.toArray(new String[customerEmailsLateLoansList.size()]);
		
		SimpleMailMessage mailMessage = new SimpleMailMessage();
		mailMessage.setFrom(mailRemetent);
		mailMessage.setSubject(mailSubject);
		mailMessage.setText(message);
		mailMessage.setTo(listOfEmailsToBeSend);

		javaMailSender.send(mailMessage);
	}

}
