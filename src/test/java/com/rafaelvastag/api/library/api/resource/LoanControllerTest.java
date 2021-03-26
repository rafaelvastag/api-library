package com.rafaelvastag.api.library.api.resource;

import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDate;
import java.util.Arrays;
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
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rafaelvastag.api.library.dto.LoanDTO;
import com.rafaelvastag.api.library.dto.LoanFilterDTO;
import com.rafaelvastag.api.library.dto.ReturnedLoanDTO;
import com.rafaelvastag.api.library.exception.BusinessException;
import com.rafaelvastag.api.library.model.entity.Book;
import com.rafaelvastag.api.library.model.entity.Loan;
import com.rafaelvastag.api.library.resource.LoanController;
import com.rafaelvastag.api.library.service.BookService;
import com.rafaelvastag.api.library.service.LoanService;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
@WebMvcTest(controllers = LoanController.class)
@AutoConfigureMockMvc
class LoanControllerTest {

	static final String LOAN_API = "/api/loans";

	@Autowired
	MockMvc mvc;

	@MockBean
	private BookService bookService;

	@MockBean
	private LoanService loanService;

	@Test
	@DisplayName("Should make a loan")
	void createLoanTest() throws Exception {

		// Scenery
		Book book = Book.builder().id(11L).isbn("123456").build();

		String json = new ObjectMapper().writeValueAsString(createLoanDTO());

		BDDMockito.given(bookService.findBookByIsbn(createLoanDTO().getIsbn())).willReturn(Optional.of(book));

		BDDMockito.given(loanService.save(Mockito.any(Loan.class))).willReturn(createLoan(book));

		// Execution
		MockHttpServletRequestBuilder request = MockMvcRequestBuilders.post(LOAN_API)
				.contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON).content(json);

		// Assertion
		mvc.perform(request).andExpect(MockMvcResultMatchers.status().isCreated()).andExpect(content().string("1"));

	}

	@Test
	@DisplayName("Should return a exception when isbn isn't exists")
	void invalidIsbnCreateLoanTest() throws Exception {

		// Scenery
		String json = new ObjectMapper().writeValueAsString(createLoanDTO());

		BDDMockito.given(bookService.findBookByIsbn(createLoanDTO().getIsbn())).willReturn(Optional.empty());

		// Execution
		MockHttpServletRequestBuilder request = MockMvcRequestBuilders.post(LOAN_API)
				.contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON).content(json);

		// Assertion
		mvc.perform(request).andExpect(MockMvcResultMatchers.status().isBadRequest())
				.andExpect(jsonPath("errors", Matchers.hasSize(1)))
				.andExpect(jsonPath("errors[0]").value("Book not found for this ISBN"));

	}

	@Test
	@DisplayName("Should return a error when trying create a loan of a book that already have been loaned")
	void errorOnCreateLoanAlreadyLoanedTest() throws Exception {

		// Scenery
		String json = new ObjectMapper().writeValueAsString(createLoanDTO());
		Book book = Book.builder().id(11L).isbn("123456").build();

		BDDMockito.given(bookService.findBookByIsbn(createLoanDTO().getIsbn())).willReturn(Optional.of(book));

		BDDMockito.given(loanService.save(Mockito.any(Loan.class)))
				.willThrow(new BusinessException("Book already have been loaned"));

		// Execution
		MockHttpServletRequestBuilder request = MockMvcRequestBuilders.post(LOAN_API)
				.contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON).content(json);

		// Assertion
		mvc.perform(request).andExpect(MockMvcResultMatchers.status().isBadRequest())
				.andExpect(jsonPath("errors", Matchers.hasSize(1)))
				.andExpect(jsonPath("errors[0]").value("Book already have been loaned"));
	}

	@Test
	@DisplayName("Should return a book")
	void returnBookTest() throws Exception {

		// Scenery
		ReturnedLoanDTO returnedBook = ReturnedLoanDTO.builder().returned(true).build();
		String json = new ObjectMapper().writeValueAsString(returnedBook);
		Loan loan = Loan.builder().id(1L).build();

		BDDMockito.given(loanService.findById(Mockito.anyLong())).willReturn(Optional.of(loan));
		Mockito.when(loanService.updateLoan(Mockito.any(Loan.class))).thenReturn(loan);

		// Execution
		MockHttpServletRequestBuilder request = MockMvcRequestBuilders.patch(LOAN_API.concat("/1"))
				.contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON).content(json);

		// Assertion
		mvc.perform(request).andExpect(status().isOk());
		verify(loanService, Mockito.times(1)).updateLoan(loan);

	}

	@Test
	@DisplayName("Should return a book")
	void returnNotFoundBookTest() throws Exception {

		// Scenery
		ReturnedLoanDTO returnedBook = ReturnedLoanDTO.builder().returned(true).build();
		String json = new ObjectMapper().writeValueAsString(returnedBook);

		BDDMockito.given(loanService.findById(Mockito.anyLong())).willReturn(Optional.empty());

		// Execution
		MockHttpServletRequestBuilder request = MockMvcRequestBuilders.patch(LOAN_API.concat("/1"))
				.contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON).content(json);

		// Assertion
		mvc.perform(request).andExpect(status().isNotFound());
		verify(loanService, Mockito.times(0)).updateLoan(Mockito.any(Loan.class));

	}

	@Test
	@DisplayName("Should find loans")
	@SuppressWarnings("unchecked")
	void findLoansByProperties() throws Exception {

		// Scenery
		Loan loan = createLoan(createNewEntityBook(1L));

		BDDMockito.given(loanService.find(Mockito.any(LoanFilterDTO.class), Mockito.any(Pageable.class)))
				.willReturn(new PageImpl(Arrays.asList(loan), PageRequest.of(0, 10), 1L));

		String queryString = String.format("?isbn=%s&customerName=%s&page=0&size=10", loan.getBook().getIsbn(),
				loan.getCustomer());

		// Execution
		MockHttpServletRequestBuilder request = MockMvcRequestBuilders.get(LOAN_API.concat(queryString))
				.accept(MediaType.APPLICATION_JSON).contentType(MediaType.APPLICATION_JSON);

		// Assertion
		mvc.perform(request).andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(jsonPath("content", Matchers.hasSize(1))).andExpect(jsonPath("totalElements").value(1))
				.andExpect(jsonPath("pageable.pageSize").value(10)).andExpect(jsonPath("pageable.pageNumber").value(0));
	}

	private Loan createLoan(Book book) {
		return Loan.builder().id(1L).customer("Customer Name").emailCustomer("customer@email.com").book(book)
				.loanDate(LocalDate.now()).build();
	}

	private LoanDTO createLoanDTO() {
		return LoanDTO.builder().customerName("Customer Name").emailCustomer("customer@email.com").isbn("123456")
				.build();
	}

	private Book createNewEntityBook(Long id) {
		return Book.builder().id(id).title("newTitle").author("newAuthor").isbn("123456").build();
	}

	// Scenery

	// Execution

	// Assertion

}
