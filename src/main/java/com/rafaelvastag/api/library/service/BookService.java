package com.rafaelvastag.api.library.service;

import java.util.Optional;

import com.rafaelvastag.api.library.model.entity.Book;

public interface BookService {

	Book save(Book book);

	Optional<Book> findById(Long id);

	void delete(Book book);
	
	Book update(Book book);

}
