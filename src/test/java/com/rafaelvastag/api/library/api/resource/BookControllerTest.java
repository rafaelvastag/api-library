package com.rafaelvastag.api.library.api.resource;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

import java.util.Optional;

import org.hamcrest.Matchers;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.BDDMockito;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rafaelvastag.api.library.dto.BookDTO;
import com.rafaelvastag.api.library.exception.BusinessException;
import com.rafaelvastag.api.library.model.entity.Book;
import com.rafaelvastag.api.library.service.BookService;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
@WebMvcTest
@AutoConfigureMockMvc
class BookControllerTest {

	static String BOOK_API = "/api/books";

	@Autowired
	MockMvc mvc;

	@MockBean
	BookService service;

	@Test
	@DisplayName("Should return status resource not found when no book exists with that id")
	void bookNotFoundTest() throws Exception {

		// Scenery
		BDDMockito.given(service.findById(Mockito.anyLong())).willReturn(Optional.empty());

		// Execution
		MockHttpServletRequestBuilder request = MockMvcRequestBuilders.get(BOOK_API.concat("/" + 1L))
				.accept(MediaType.APPLICATION_JSON);

		// Assertion
		mvc.perform(request).andExpect(MockMvcResultMatchers.status().isNotFound());
	}

	@Test
	@DisplayName("Should get details about a specific book")
	void getBookDetailsTest() throws Exception {

		// Scenery
		Long id = 11L;
		Book book = Book.builder().id(id).title(createNewBook().getTitle()).author(createNewBook().getAuthor())
				.isbn(createNewBook().getIsbn()).build();

		BDDMockito.given(service.findById(id)).willReturn(Optional.of(book));

		// Execution
		MockHttpServletRequestBuilder request = MockMvcRequestBuilders.get(BOOK_API.concat("/" + id))
				.accept(MediaType.APPLICATION_JSON);

		// Assertion
		mvc.perform(request).andExpect(MockMvcResultMatchers.status().isOk()).andExpect(jsonPath("id").value(id))
				.andExpect(jsonPath("title").value(createNewBook().getTitle()))
				.andExpect(jsonPath("author").value(createNewBook().getAuthor()))
				.andExpect(jsonPath("isbn").value(createNewBook().getIsbn()));
	}

	@Test
	@DisplayName("Should create a book")
	void createBookTest() throws Exception {

		// Scenery
		String json = new ObjectMapper().writeValueAsString(createNewBook());
		Book savedBook = Book.builder().id(10L).author("Author").title("my book").isbn("123456789").build();

		BDDMockito.given(service.save(Mockito.any(Book.class))).willReturn(savedBook);

		// Execution
		MockHttpServletRequestBuilder request = MockMvcRequestBuilders.post(BOOK_API)
				.contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON).content(json);

		// Assertion
		mvc.perform(request).andExpect(MockMvcResultMatchers.status().isCreated())
				.andExpect(jsonPath("id").value(savedBook.getId()))
				.andExpect(jsonPath("title").value(createNewBook().getTitle()))
				.andExpect(jsonPath("author").value(createNewBook().getAuthor()))
				.andExpect(jsonPath("isbn").value(createNewBook().getIsbn()));

	}

	@Test
	@DisplayName("Should throw validate error when haven't datas to create a book")
	void createInvalidBookTest() throws Exception {

		// Scenery
		String json = new ObjectMapper().writeValueAsString(new BookDTO());

		// Execution
		MockHttpServletRequestBuilder request = MockMvcRequestBuilders.post(BOOK_API)
				.contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON).content(json);

		// Assertion
		mvc.perform(request).andExpect(MockMvcResultMatchers.status().isBadRequest());

	}

	@Test
	@DisplayName("Shouldn't create a new book register if already have a book with the ISBN indicated")
	void createBookWithDuplicatedIsbn() throws Exception {

		// Scenery
		String json = new ObjectMapper().writeValueAsString(createNewBook());
		String errorMessage = "ISBN exists.";

		BDDMockito.given(service.save(Mockito.any(Book.class))).willThrow(new BusinessException(errorMessage));

		// Execution
		MockHttpServletRequestBuilder request = MockMvcRequestBuilders.post(BOOK_API)
				.contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON).content(json);

		// Assertion
		mvc.perform(request).andExpect(MockMvcResultMatchers.status().isBadRequest())
				.andExpect(jsonPath("errors", Matchers.hasSize(1)))
				.andExpect(jsonPath("errors[0]").value(errorMessage));

	}

	@Test
	@DisplayName("Should delete the book that has the id inputed")
	void deleteInexistsBookTest() throws Exception {

		// Scenery
		BDDMockito.given(service.findById(Mockito.anyLong())).willReturn(Optional.empty());

		// Execution
		MockHttpServletRequestBuilder request = MockMvcRequestBuilders.delete(BOOK_API.concat("/" + 100L))
				.accept(MediaType.APPLICATION_JSON);

		// Assertion
		mvc.perform(request).andExpect(MockMvcResultMatchers.status().isNotFound());

	}

	@Test
	@DisplayName("Should throws resource not found when try delete a book that no exists")
	void deleteBookTest() throws Exception {

		// Scenery
		BDDMockito.given(service.findById(Mockito.anyLong())).willReturn(Optional.of(Book.builder().id(1L).build()));

		// Execution
		MockHttpServletRequestBuilder request = MockMvcRequestBuilders.delete(BOOK_API.concat("/" + 100L))
				.accept(MediaType.APPLICATION_JSON);

		// Assertion
		mvc.perform(request).andExpect(MockMvcResultMatchers.status().isNoContent());

	}

	@Test
	@DisplayName("Should update a book if it exists")
	void updateBook() throws Exception {

		// Scenery
		Long id = 11L;
		String json = new ObjectMapper().writeValueAsString(createNewBook());
		Book updatingBook = createNewEntityBook(id);

		BDDMockito.given(service.findById(id)).willReturn(Optional.of(updatingBook));
		BDDMockito.given(service.update(updatingBook)).willReturn(updatingBook);

		// Execution
		MockHttpServletRequestBuilder request = MockMvcRequestBuilders.put(BOOK_API.concat("/" + id))
				.content(json)
				.accept(MediaType.APPLICATION_JSON)
				.contentType(MediaType.APPLICATION_JSON);

		// Assertion
		mvc.perform(request).andExpect(MockMvcResultMatchers.status().isOk()).andExpect(jsonPath("id").value(id))
				.andExpect(jsonPath("title").value(createNewBook().getTitle()))
				.andExpect(jsonPath("author").value(createNewBook().getAuthor()))
				.andExpect(jsonPath("isbn").value(createNewBook().getIsbn()));

	}

	@Test
	@DisplayName("Should throws not found exception when trying to update a non-existing book")
	void updateNotExistingBook() throws Exception {

		// Scenery
		String json = new ObjectMapper().writeValueAsString(createNewBook());

		BDDMockito.given(service.findById(Mockito.anyLong())).willReturn(Optional.empty());

		// Execution
		MockHttpServletRequestBuilder request = MockMvcRequestBuilders.put(BOOK_API.concat("/" + 1L))
				.content(json)
				.accept(MediaType.APPLICATION_JSON)
				.contentType(MediaType.APPLICATION_JSON);

		// Assertion
		mvc.perform(request).andExpect(MockMvcResultMatchers.status().isNotFound());

	}

	private BookDTO createNewBook() {
		BookDTO book = BookDTO.builder().author("AuthorDTO").title("my bookDTO").isbn("123456").build();
		return book;
	}

	private Book createNewEntityBook(Long id) {
		return Book.builder().id(id).title("newTitle").author("newAuthor").isbn("123456").build();
	}

}
