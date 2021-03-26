package com.rafaelvastag.api.library.service;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.rafaelvastag.api.library.model.entity.Book;

public interface BookService {

	Book save(Book book);

	Optional<Book> findById(Long id);

	void delete(Book book);
	
	Book update(Book book);

	Page<Book> find(Book filter, Pageable pageRequest);

	Optional<Book> findBookByIsbn(String isbn);

}
