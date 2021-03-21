package com.rafaelvastag.api.library.resource;

import javax.validation.Valid;

import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ExceptionHandler;
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
import com.rafaelvastag.api.library.errors.ApiErrors;
import com.rafaelvastag.api.library.exception.BusinessException;
import com.rafaelvastag.api.library.model.entity.Book;
import com.rafaelvastag.api.library.service.BookService;

@RestController
@RequestMapping("/api/books")
public class BookController {

	private BookService service;
	private ModelMapper modelMapper;

	public BookController(BookService service, ModelMapper modelMapper) {
		this.service = service;
		this.modelMapper = modelMapper;
	}

	@GetMapping("{id}")
	@ResponseStatus(HttpStatus.OK)
	public BookDTO getSingleBook(@PathVariable(name = "id") Long id) {
		return service.findById(id).map(book -> modelMapper.map(book, BookDTO.class))
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
	}

	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	public BookDTO create(@RequestBody @Valid BookDTO book) {
		Book entity = modelMapper.map(book, Book.class);
		entity = service.save(entity);

		return modelMapper.map(entity, BookDTO.class);
	}

	@PutMapping("{id}")
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
	public void delete(@PathVariable Long id) {
		Book book = service.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

		service.delete(book);
	}

	@ExceptionHandler(MethodArgumentNotValidException.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	public ApiErrors handleValidationExceptions(MethodArgumentNotValidException ex) {
		BindingResult bindingResult = ex.getBindingResult();

		return new ApiErrors(bindingResult);
	}

	@ExceptionHandler(BusinessException.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	public ApiErrors handleBusinessExceptions(BusinessException ex) {
		return new ApiErrors(ex);
	}

}
