package com.rafaelvastag.api.library.api.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.BDDMockito;
import org.mockito.Mockito;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.rafaelvastag.api.library.dto.LoanFilterDTO;
import com.rafaelvastag.api.library.exception.BusinessException;
import com.rafaelvastag.api.library.model.entity.Book;
import com.rafaelvastag.api.library.model.entity.Loan;
import com.rafaelvastag.api.library.model.repository.LoanRepository;
import com.rafaelvastag.api.library.service.LoanService;
import com.rafaelvastag.api.library.service.impl.LoanServiceImpl;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("teste")
class LoanServiceTest {

	@MockBean
	private LoanRepository repository;

	private LoanService service;

	@BeforeEach
	void setUp() {
		this.service = new LoanServiceImpl(repository);
	}

	@Test
	@DisplayName("Should save a loan")
	void saveLoanTest() {
		Book book = createBook();
		Loan loan = createLoan(book);

		BDDMockito.given(repository.existsByBookAndNotReturned(book)).willReturn(false);
		Mockito.when(repository.save(loan)).thenReturn(loan);

		Loan loanSaved = service.save(loan);

		assertThat(loanSaved.getId()).isEqualTo(loan.getId());
		assertThat(loanSaved.getBook().getId()).isEqualTo(loan.getBook().getId());
		assertThat(loanSaved.getCustomer()).isEqualTo(loan.getCustomer());
		assertThat(loanSaved.getLoanDate()).isEqualTo(loan.getLoanDate());

	}

	@Test
	@DisplayName("Should not save a loaned book")
	void errorOnSaveLoanedBookTest() {
		Book book = createBook();
		Loan loan = createLoan(book);

		BDDMockito.given(repository.existsByBookAndNotReturned(book)).willReturn(true);

		Throwable exception = catchThrowable(() -> service.save(loan));

		assertThat(exception).isInstanceOf(BusinessException.class).hasMessage("Book already loaned");

		verify(repository, never()).save(loan);

	}

	@Test
	@DisplayName("Should return a Loan found by id")
	void findLoanById() {
		// Scenery
		Loan loan = createLoan(createBook());
		Mockito.when(repository.findById(loan.getId())).thenReturn(Optional.of(loan));

		// Execution
		Optional<Loan> result = service.findById(loan.getId());

		// Assertion
		assertThat(result.isPresent()).isTrue();
		assertThat(result.get().getId()).isEqualTo(loan.getId());
		assertThat(result.get().getBook().getId()).isEqualTo(loan.getBook().getId());
		assertThat(result.get().getCustomer()).isEqualTo(loan.getCustomer());
		assertThat(result.get().getLoanDate()).isEqualTo(loan.getLoanDate());

		verify(repository).findById(loan.getId());

	}

	@Test
	@DisplayName("Shoud update a Loan")
	void updateBook() {
		// Scenery
		Loan loan = createLoan(createBook());
		loan.setReturned(true);

		when(service.updateLoan(loan)).thenReturn(loan);

		// Execution
		Loan updatedLoan = service.updateLoan(loan);

		// Assertion
		assertThat(updatedLoan.getId()).isEqualTo(loan.getId());
		verify(repository, times(1)).save(loan);

	}

	@SuppressWarnings("unchecked")
	@Test
	@DisplayName("Should filter loan by properties")
	void findLoanTest() {
		// Scenery
		Loan loan = createLoan(createBook());
		LoanFilterDTO loanFilter = LoanFilterDTO.builder().customerName("Customer").isbn("123456").build();
		List<Loan> listLoan = Arrays.asList(loan);
		PageRequest pageRequest = PageRequest.of(0, 10);

		Page<Loan> page = new PageImpl<Loan>(listLoan, PageRequest.of(0, 10), 1L);
		Mockito.when(repository.findAll((Mockito.any(Example.class)), Mockito.any(Pageable.class))).thenReturn(page);

		when(repository.findByBookIsbnOrCustomer(Mockito.anyString(), Mockito.anyString(),
				Mockito.any(PageRequest.class))).thenReturn(page);

		// Execution
		Page<Loan> result = service.find(loanFilter, pageRequest);

		// Assertion
		assertThat(result.getTotalElements()).isEqualTo(1);
		assertThat(result.getContent()).isEqualTo(listLoan);
		assertThat(result.getPageable().getPageNumber()).isZero();
		assertThat(result.getPageable().getPageSize()).isEqualTo(10);

	}

	private Loan createLoan(Book book) {
		return Loan.builder().id(1L).book(book).customer("Customer").loanDate(LocalDate.now()).build();
	}

	private Book createBook() {
		return Book.builder().id(11L).title("Title").author("Author").isbn("123456").build();
	}

}
