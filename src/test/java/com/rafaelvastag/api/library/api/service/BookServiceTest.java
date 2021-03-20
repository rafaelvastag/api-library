package com.rafaelvastag.api.library.api.service;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.rafaelvastag.api.library.model.entity.Book;
import com.rafaelvastag.api.library.model.repository.BookRepository;
import com.rafaelvastag.api.library.service.BookService;
import com.rafaelvastag.api.library.service.impl.BookServiceImpl;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
class BookServiceTest {

	BookService service;

	@MockBean
	BookRepository repository;

	@BeforeEach
	public void setUp() {
		this.service = new BookServiceImpl(repository);
	}

	@Test
	@DisplayName("Should persist a book")
	void saveBookTest() {

		// Scenery
		Book book = Book.builder().title("Title").isbn("1111").author("Rick").build();

		Mockito.when(repository.save(book)).thenReturn(
				Book.builder().id(11L).author(book.getAuthor()).title(book.getTitle()).isbn(book.getIsbn()).build());

		// Execution
		Book savedBook = service.save(book);
		// Assertion
		assertThat(savedBook.getId()).isNotNull();
		assertThat(savedBook.getIsbn()).isEqualTo(book.getIsbn());
		assertThat(savedBook.getTitle()).isEqualTo(book.getTitle());
		assertThat(savedBook.getAuthor()).isEqualTo(book.getAuthor());

	}

}
