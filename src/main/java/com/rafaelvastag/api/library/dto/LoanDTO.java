package com.rafaelvastag.api.library.dto;

import javax.validation.constraints.NotEmpty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class LoanDTO {
	
	private Long id;
	
	@NotEmpty(message = "ISBN required")
	private String isbn;
	
	@NotEmpty(message = "Customer Name required")
	private String customerName;
	
	@NotEmpty(message = "Email required")
	private String emailCustomer;
	
	private BookDTO book;

}
