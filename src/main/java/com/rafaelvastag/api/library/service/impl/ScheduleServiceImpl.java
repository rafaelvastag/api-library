package com.rafaelvastag.api.library.service.impl;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.rafaelvastag.api.library.model.entity.Loan;
import com.rafaelvastag.api.library.service.EmailService;
import com.rafaelvastag.api.library.service.LoanService;
import com.rafaelvastag.api.library.service.ScheduleService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ScheduleServiceImpl implements ScheduleService{

	private static final String CRON_LATE_LOANS = "0 0 0 1/1 * ?";
	
	@Value("${application.mail.lateloans.message}")
	private String message; 
	
	private final LoanService loanService;
	private final EmailService emailService;
	
	@Override
	@Scheduled(cron = CRON_LATE_LOANS)
	public void sendMailToLateLoans() {
		List<Loan> allLateLoans = loanService.getAllLateLoans();
		List<String> customerEmailsLateLoansList = allLateLoans.stream().map( loan -> loan.getEmailCustomer()).collect(Collectors.toList());
		
		emailService.sendMails(message, customerEmailsLateLoansList);
	}

}
