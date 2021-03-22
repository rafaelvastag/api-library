package com.rafaelvastag.api.library.api.service;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Optional;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.rafaelvastag.api.library.exception.BusinessException;
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
		Book book = createNewBook();

		Mockito.when(repository.existsByIsbn(Mockito.anyString())).thenReturn(false);

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

	@Test
	@DisplayName("Shouldn't save a book with a duplicated ISBN")
	void shouldNotSaveBookWithDuplicatedIsbn() {

		// Scenery
		Book book = createNewBook();

		Mockito.when(repository.existsByIsbn(Mockito.anyString())).thenReturn(true);

		// Execution
		Throwable exception = Assertions.catchThrowable(() -> service.save(book));

		// Assertion
		assertThat(exception).isInstanceOf(BusinessException.class).hasMessage("ISBN exists.");

		Mockito.verify(repository, Mockito.never()).save(book);

	}

	@Test
	@DisplayName("Should find and return a book by id")
	void getBookByIdTest() {

		// Scenery
		Long id = 11L;
		Book book = createNewBook();
		book.setId(11L);
		Mockito.when(repository.findById(id)).thenReturn(Optional.of(book));

		// Execution
		Optional<Book> foundBook = service.findById(id);

		// Assertion
		assertThat(foundBook.isPresent()).isTrue();
		assertThat(foundBook.get().getId()).isEqualTo(id);
		assertThat(foundBook.get().getAuthor()).isEqualTo(book.getAuthor());
		assertThat(foundBook.get().getTitle()).isEqualTo(book.getTitle());
		assertThat(foundBook.get().getIsbn()).isEqualTo(book.getIsbn());

	}

	@Test
	@DisplayName("Should return empty when find a non-existing book")
	void bookNotFoundWhenFindById() {
		// Scenery
		Long id = 11L;
		Mockito.when(repository.findById(Mockito.anyLong())).thenReturn(Optional.empty());

		// Execution
		Optional<Book> foundBook = service.findById(id);

		// Assertion
		assertThat(foundBook.isPresent()).isFalse();
	}

	@Test
	@DisplayName("Should delete a book")
	void deleteBookTest() {
		// Scenery
		Book book = Book.builder().id(11L).build();

		// Execution
		org.junit.jupiter.api.Assertions.assertDoesNotThrow(() -> service.delete(book));

		// Assertion
		Mockito.verify(repository, Mockito.times(1)).delete(book);
	}

	@Test
	@DisplayName("Should throw exception when to try delete a non-existent book")
	void deleteInvalidBookTest() {
		// Scenery
		Book book = new Book();

		// Execution
		org.junit.jupiter.api.Assertions.assertThrows(IllegalArgumentException.class, () -> service.delete(book));

		// Assertion
		Mockito.verify(repository, Mockito.never()).delete(book);
	}

	@Test
	@DisplayName("Should update a book existent")
	void updateBook() {
		// Scenery
		Long id = 11L;
		Book bookToBeUpdated = Book.builder().id(id).build();
		Book updatedBook = createNewBook();
		updatedBook.setId(id);

		Mockito.when(repository.save(bookToBeUpdated)).thenReturn(updatedBook);

		// Execution

		Book bookReturned = service.update(bookToBeUpdated);

		// Assertion
		assertThat(bookReturned.getId()).isEqualTo(id);
		assertThat(bookReturned.getAuthor()).isEqualTo(updatedBook.getAuthor());
		assertThat(bookReturned.getTitle()).isEqualTo(updatedBook.getTitle());
		assertThat(bookReturned.getIsbn()).isEqualTo(updatedBook.getIsbn());
		org.junit.jupiter.api.Assertions.assertDoesNotThrow(() -> service.update(bookToBeUpdated));
	}

	@Test
	@DisplayName("Should throw exception when to try update a non-existent book")
	void updateInvalidBookTest() {
		// Scenery
		Book book = new Book();

		// Execution
		org.junit.jupiter.api.Assertions.assertThrows(IllegalArgumentException.class, () -> service.update(book));

		// Assertion
		Mockito.verify(repository, Mockito.never()).save(book);
	}

	private Book createNewBook() {
		return Book.builder().title("Title").isbn("1111").author("Rick").build();
	}
}
