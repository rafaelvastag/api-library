package com.rafaelvastag.api.library.api.model.repository;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.rafaelvastag.api.library.model.entity.Book;
import com.rafaelvastag.api.library.model.entity.Loan;
import com.rafaelvastag.api.library.model.repository.LoanRepository;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("teste")
@DataJpaTest
class LoanRepositoryTest {

	@Autowired
	private TestEntityManager entityManager;

	@Autowired
	private LoanRepository repository;

	@Test
	@DisplayName("Should verify if exists a loan not returned for this book")
	void existsByBookAndNotReturnedTest() {
		Book book = createNewBook("123");
		Loan loan = createLoan(book);

		entityManager.persist(book);
		entityManager.persist(loan);

		boolean exists = repository.existsByBookAndNotReturned(book);

		assertThat(exists).isTrue();
	}

	@Test
	@DisplayName("Should find a loan by book isbn or customer name")
	void findByBookIsbnOrCustomerName() {
		createAndPersistLoanWithBook();

		Page<Loan> result = repository.findByBookIsbnOrCustomer("123", "Customer", PageRequest.of(0, 10));
		
		assertThat(result.getContent()).hasSize(1);
		assertThat(result.getPageable().getPageSize()).isEqualTo(10);
		assertThat(result.getPageable().getPageNumber()).isZero();
		assertThat(result.getTotalElements()).isOne();
	}

	private Book createNewBook(String isbn) {
		Book book = Book.builder().title("Title").author("Author").isbn(isbn).build();
		return book;
	}

	private Loan createLoan(Book book) {
		return Loan.builder().book(book).customer("Customer").loanDate(LocalDate.now()).build();
	}

	private void createAndPersistLoanWithBook() {
		Book book = createNewBook("123");
		Loan loan = createLoan(book);

		entityManager.persist(book);
		entityManager.persist(loan);
	}

}
