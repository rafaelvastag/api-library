package com.rafaelvastag.api.library.api.model.repository;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.rafaelvastag.api.library.model.entity.Book;
import com.rafaelvastag.api.library.model.repository.BookRepository;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
@DataJpaTest
class BookRepositoryTest {

	@Autowired
	TestEntityManager entityManager;

	@Autowired
	BookRepository repository;

	@Test
	@DisplayName("Should return true when exists a book with the isbn inputed")
	void returnTrueWhenIsbnExists() {

		// Scenery
		String isbn = "123";
		Book book = Book.builder().title("Title").author("Author").isbn(isbn).build();

		entityManager.persist(book);

		// Execution
		boolean exists = repository.existsByIsbn(isbn);

		// Assertion
		assertThat(exists).isTrue();
	}

	@Test
	@DisplayName("Should return FALSE when NOT exists a book with the isbn inputed")
	void returnFalseWhenIsbnNotExists() {

		// Scenery
		String isbn = "123";
		Book book = Book.builder().title("Title").author("Author").isbn(isbn).build();

		entityManager.persist(book);

		// Execution
		String newIsbn = "1234";
		boolean exists = repository.existsByIsbn(newIsbn);

		// Assertion
		assertThat(exists).isFalse();
	}

}
