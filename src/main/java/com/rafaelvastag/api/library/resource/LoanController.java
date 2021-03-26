package com.rafaelvastag.api.library.resource;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.rafaelvastag.api.library.dto.BookDTO;
import com.rafaelvastag.api.library.dto.LoanDTO;
import com.rafaelvastag.api.library.dto.LoanFilterDTO;
import com.rafaelvastag.api.library.dto.ReturnedLoanDTO;
import com.rafaelvastag.api.library.model.entity.Book;
import com.rafaelvastag.api.library.model.entity.Loan;
import com.rafaelvastag.api.library.service.BookService;
import com.rafaelvastag.api.library.service.LoanService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/loans")
@RequiredArgsConstructor
public class LoanController {

	private final LoanService service;
	private final BookService bookService;
	private final ModelMapper modelMapper;

	@GetMapping
	public Page<LoanDTO> find(LoanFilterDTO loan, Pageable pageRequest) {
		Page<Loan> result = service.find(loan, pageRequest);

		List<LoanDTO> loans = result.getContent()
				.stream()
				.map(entity -> {

					Book book = entity.getBook();
					BookDTO bookDTO = modelMapper.map(book, BookDTO.class);
					LoanDTO loanDTO = modelMapper.map(entity, LoanDTO.class);
					loanDTO.setBook(bookDTO);

				return loanDTO;

		}).collect(Collectors.toList());

		return new PageImpl<LoanDTO>(loans, pageRequest, result.getTotalElements());
	}

	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	public Long create(@RequestBody LoanDTO loan) {
		Book book = bookService.findBookByIsbn(loan.getIsbn())
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Book not found for this ISBN"));
		Loan entity = Loan.builder().customer(loan.getCustomerName()).book(book).emailCustomer(loan.getCustomerName()).loanDate(LocalDate.now()).build();

		entity = service.save(entity);

		return entity.getId();
	}

	@PatchMapping("{id}")
	@ResponseStatus(HttpStatus.OK)
	public void returnBook(@PathVariable Long id, @RequestBody ReturnedLoanDTO returnedBook) {
		Loan loanFound = new Loan();
		Optional<Loan> loan = service.findById(id);

		if (loan.isPresent()) {

			loanFound = loan.get();
			loanFound.setReturned(returnedBook.getReturned());
			service.updateLoan(loanFound);

		} else {

			throw new ResponseStatusException(HttpStatus.NOT_FOUND);
		}
	}

}
