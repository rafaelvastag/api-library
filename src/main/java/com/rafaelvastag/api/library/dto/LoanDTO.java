package com.rafaelvastag.api.library.dto;

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
	private String isbn;
	private String customerName;
	private BookDTO book;

}
