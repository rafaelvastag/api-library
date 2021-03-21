package com.rafaelvastag.api.library.api.resource;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

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
	@DisplayName("Should create a book")
	void createBookTest() throws Exception {

		BookDTO book = createNewBook();

		Book savedBook = Book.builder().id(10L).author("Author").title("my book").isbn("123456789").build();

		BDDMockito.given(service.save(Mockito.any(Book.class))).willReturn(savedBook);

		String json = new ObjectMapper().writeValueAsString(book);

		MockHttpServletRequestBuilder request = MockMvcRequestBuilders.post(BOOK_API)
				.contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON).content(json);

		mvc.perform(request).andExpect(MockMvcResultMatchers.status().isCreated())
				.andExpect(jsonPath("id").value(savedBook.getId())).andExpect(jsonPath("title").value(book.getTitle()))
				.andExpect(jsonPath("author").value(book.getAuthor()))
				.andExpect(jsonPath("isbn").value(book.getIsbn()));

	}

	@Test
	@DisplayName("Should throw validate error when haven't datas to create a book")
	void createInvalidBookTest() throws Exception {

		String json = new ObjectMapper().writeValueAsString(new BookDTO());

		MockHttpServletRequestBuilder request = MockMvcRequestBuilders.post(BOOK_API)
				.contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON).content(json);

		mvc.perform(request).andExpect(MockMvcResultMatchers.status().isBadRequest());

	}

	@Test
	@DisplayName("Shouldn't create a new book register if already have a book with the ISBN indicated")
	void createBookWithDuplicatedIsbn() throws Exception {

		BookDTO book = createNewBook();
		String json = new ObjectMapper().writeValueAsString(book);
		String errorMessage = "ISBN exists.";

		MockHttpServletRequestBuilder request = MockMvcRequestBuilders.post(BOOK_API)
				.contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON).content(json);

		BDDMockito.given(service.save(Mockito.any(Book.class))).willThrow(new BusinessException(errorMessage));

		mvc.perform(request).andExpect(MockMvcResultMatchers.status().isBadRequest())
				.andExpect(jsonPath("errors", Matchers.hasSize(1)))
				.andExpect(jsonPath("errors[0]").value(errorMessage));

	}

	private BookDTO createNewBook() {
		BookDTO book = BookDTO.builder().author("Author").title("my book").isbn("123456789").build();
		return book;
	}

}
