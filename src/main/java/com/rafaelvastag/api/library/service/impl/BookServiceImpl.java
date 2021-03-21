package com.rafaelvastag.api.library.service.impl;

import org.springframework.stereotype.Service;

import com.rafaelvastag.api.library.exception.BusinessException;
import com.rafaelvastag.api.library.model.entity.Book;
import com.rafaelvastag.api.library.model.repository.BookRepository;
import com.rafaelvastag.api.library.service.BookService;

@Service
public class BookServiceImpl implements BookService{
	
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

}
