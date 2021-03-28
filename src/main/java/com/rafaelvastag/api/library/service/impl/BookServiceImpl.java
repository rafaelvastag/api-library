package com.rafaelvastag.api.library.service.impl;

import java.util.Optional;

import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.ExampleMatcher.StringMatcher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.rafaelvastag.api.library.exception.BusinessException;
import com.rafaelvastag.api.library.model.entity.Book;
import com.rafaelvastag.api.library.model.repository.BookRepository;
import com.rafaelvastag.api.library.service.BookService;

@Service
public class BookServiceImpl implements BookService {

	private BookRepository repository;

	public BookServiceImpl(BookRepository repo) {
		this.repository = repo;
	}

	@Override
	public Book save(Book book) {

		if (repository.existsByIsbn(book.getIsbn())) {
			throw new BusinessException("ISBN exists.");
		}

		return repository.save(book);
	}

	@Override
	public Optional<Book> findById(Long id) {
		return repository.findById(id);
	}

	@Override
	public void delete(Book book) {
		if (book == null  || book.getId() == null) {
			throw new IllegalArgumentException("Book invalid");
		}
		this.repository.delete(book);
	}

	@Override
	public Book update(Book book) {
		if (book == null  || book.getId() == null) {
			throw new IllegalArgumentException("Book invalid");
		}
		
		return repository.save(book);
	}

	@Override
	public Page<Book> find(Book filter, Pageable pageRequest) {
		
		Example<Book> criteria = Example.of(filter, 
													ExampleMatcher
														.matching()
														.withIgnoreCase()
														.withIgnoreNullValues()
														.withStringMatcher(StringMatcher.CONTAINING)
														);
		
		return repository.findAll(criteria, pageRequest);
	}

	@Override
	public Optional<Book> findBookByIsbn(String isbn) {
		return repository.findByIsbn(isbn);
	}

}
