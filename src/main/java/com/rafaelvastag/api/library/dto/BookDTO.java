package com.rafaelvastag.api.library.dto;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BookDTO {
	
	private Long id;
	
	@NotEmpty
	@NotNull
	private String title;
	
	@NotEmpty
	@NotNull
	private String author;
	
	@NotEmpty
	@NotNull
	private String isbn;

}
