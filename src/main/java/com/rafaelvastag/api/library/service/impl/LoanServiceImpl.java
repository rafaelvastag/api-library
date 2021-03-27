package com.rafaelvastag.api.library.service.impl;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.rafaelvastag.api.library.dto.LoanFilterDTO;
import com.rafaelvastag.api.library.exception.BusinessException;
import com.rafaelvastag.api.library.model.entity.Book;
import com.rafaelvastag.api.library.model.entity.Loan;
import com.rafaelvastag.api.library.model.repository.LoanRepository;
import com.rafaelvastag.api.library.service.LoanService;

@Service
public class LoanServiceImpl implements LoanService {

	private LoanRepository repository;

	public LoanServiceImpl(LoanRepository repository) {
		this.repository = repository;
	}

	@Override
	public Loan save(Loan loan) {

		if (repository.existsByBookAndNotReturned(loan.getBook())) {
			throw new BusinessException("Book already loaned");
		}

		return repository.save(loan);
	}

	@Override
	public Optional<Loan> findById(Long id) {
		return repository.findById(id);
	}

	@Override
	public Loan updateLoan(Loan loan) {
		return repository.save(loan);
	}

	@Override
	public Page<Loan> find(LoanFilterDTO filter, Pageable pageRequest) {

		return repository.findByBookIsbnOrCustomer(filter.getIsbn(), filter.getCustomerName(), pageRequest);
	}

	@Override
	public Page<Loan> getLoansByBook(Book book, Pageable pageable) {
		return repository.findByBook(book, pageable);
	}

	
	@Override
	public List<Loan> getAllLateLoans() {
		final Integer loanMaxDays = 4;
		LocalDate threeDaysAgo = LocalDate.now().minusDays(loanMaxDays);

		return repository.findByLoanDateLessThanAndNotReturned(threeDaysAgo);
	}

}
