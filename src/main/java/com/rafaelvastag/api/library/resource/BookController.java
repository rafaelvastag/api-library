package com.rafaelvastag.api.library.resource;

import java.util.List;
import java.util.stream.Collectors;

import javax.validation.Valid;

import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.rafaelvastag.api.library.dto.BookDTO;
import com.rafaelvastag.api.library.dto.LoanDTO;
import com.rafaelvastag.api.library.model.entity.Book;
import com.rafaelvastag.api.library.model.entity.Loan;
import com.rafaelvastag.api.library.service.BookService;
import com.rafaelvastag.api.library.service.LoanService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.AllArgsConstructor;

@RestController
@RequestMapping("/api/books")
@AllArgsConstructor
@Api("Book API")
public class BookController {

	private final BookService service;
	private final LoanService loanService;
	private final ModelMapper modelMapper;

	@GetMapping("{id}")
	@ResponseStatus(HttpStatus.OK)
	@ApiOperation("Find book by id")
	public BookDTO getSingleBook(@PathVariable(name = "id") Long id) {
		return service.findById(id).map(book -> modelMapper.map(book, BookDTO.class))
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
	}

	@GetMapping
	public Page<BookDTO> find(BookDTO book, Pageable pageRequest) {
		Book filter = modelMapper.map(book, Book.class);

		Page<Book> result = service.find(filter, pageRequest);

		List<BookDTO> list = result.getContent().stream().map(entity -> modelMapper.map(entity, BookDTO.class))
				.collect(Collectors.toList());

		return new PageImpl<BookDTO>(list, pageRequest, result.getTotalElements());
	}

	@GetMapping("{id}/loans")
	@ApiOperation("Obtains a list of loans by book")
	public Page<LoanDTO> loansByBook(@PathVariable Long id, Pageable pageable) {
		Book book = service.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

		Page<Loan> loansByBook = loanService.getLoansByBook(book, pageable);

		List<LoanDTO> listLoansDTO = loansByBook.getContent().stream().map(loan -> {
			Book loanedBook = loan.getBook();
			BookDTO bookDTO = modelMapper.map(loanedBook, BookDTO.class);
			LoanDTO loanDTO = modelMapper.map(loan, LoanDTO.class);
			loanDTO.setBook(bookDTO);

			return loanDTO;
		}).collect(Collectors.toList());

		return new PageImpl<LoanDTO>(listLoansDTO, pageable, loansByBook.getTotalElements());
	}

	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	@ApiOperation("Create a new book in storage")
	public BookDTO create(@RequestBody @Valid BookDTO book) {
		Book entity = modelMapper.map(book, Book.class);
		entity = service.save(entity);

		return modelMapper.map(entity, BookDTO.class);
	}

	@PutMapping("{id}")
	@ApiOperation("Update a registered book")
	public BookDTO update(@PathVariable Long id, @RequestBody BookDTO bookDTO) throws Exception {
		return service.findById(id).map(book -> {
			book.setAuthor(bookDTO.getAuthor());
			book.setTitle(bookDTO.getTitle());
			book = service.update(book);
			return modelMapper.map(book, BookDTO.class);
		}).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
	}

	@DeleteMapping("{id}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	@ApiOperation("Delete a book by id")
	@ApiResponses({
		@ApiResponse(code = 204, message = "Book succesfully deleted")
	})
	public void delete(@PathVariable Long id) {
		Book book = service.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

		service.delete(book);
	}

}
