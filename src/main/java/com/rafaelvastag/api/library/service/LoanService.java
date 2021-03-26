package com.rafaelvastag.api.library.service;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.rafaelvastag.api.library.dto.LoanFilterDTO;
import com.rafaelvastag.api.library.model.entity.Book;
import com.rafaelvastag.api.library.model.entity.Loan;

public interface LoanService {

	Loan save(Loan loan);

	Optional<Loan> findById(Long id);

	Loan updateLoan(Loan loan);

	Page<Loan> find(LoanFilterDTO loan, Pageable pageRequest);

	Page<Loan> getLoansByBook(Book book, Pageable pageable);

}
