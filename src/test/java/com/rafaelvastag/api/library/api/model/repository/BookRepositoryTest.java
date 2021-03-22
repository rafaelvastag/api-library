package com.rafaelvastag.api.library.api.model.repository;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Optional;

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
		Book book = createNewBook(isbn);

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
		Book book = createNewBook(isbn);

		entityManager.persist(book);

		// Execution
		String newIsbn = "1234";
		boolean exists = repository.existsByIsbn(newIsbn);

		// Assertion
		assertThat(exists).isFalse();

	}

	@Test
	@DisplayName("Should return a book found by id")
	void findByIdTest() {
		// Scenery
		String isbn = "123";
		Book book = createNewBook(isbn);

		book = entityManager.persist(book);

		// Execution
		Optional<Book> foundBook = repository.findById(book.getId());

		// Assertion
		assertThat(foundBook.isPresent()).isTrue();

	}

	@Test
	@DisplayName("Should return empty when find a book by id and it is a non-existing book")
	void returnEmptyWhenFindByIdAndBookNonExistsTest() {
		// Scenery
		Long id = 11L;

		// Execution
		Optional<Book> foundBook = repository.findById(id);

		// Assertion
		assertThat(foundBook.isPresent()).isFalse();

	}

	@Test
	@DisplayName("Should save a new book, creating a new ID for this one")
	void saveBookTest() {
		// Scenery
		Book book = createNewBook("12");

		// Execution
		Book savedBook = repository.save(book);

		// Assertion
		assertThat(savedBook.getId()).isNotNull();
		assertThat(savedBook.getIsbn()).isEqualTo("12");

	}

	@Test
	@DisplayName("Should delete a book")
	void deleteBookTest() {
		// Scenery
		Book book = createNewBook("12");
		book = entityManager.persist(book);
		Book bookToBeDeleted = entityManager.find(Book.class, book.getId());

		// Execution
		repository.delete(bookToBeDeleted);

		// Assertion
		Book deletedBook = entityManager.find(Book.class, book.getId());
		assertThat(deletedBook).isNull();

	}

	private Book createNewBook(String isbn) {
		Book book = Book.builder().title("Title").author("Author").isbn(isbn).build();
		return book;
	}
}
