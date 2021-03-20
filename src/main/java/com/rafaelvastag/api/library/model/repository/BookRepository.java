package com.rafaelvastag.api.library.model.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.rafaelvastag.api.library.model.entity.Book;

public interface BookRepository extends JpaRepository<Book, Long>{

}
