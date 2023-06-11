package com.dhrubok.bookstoreMS.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.dhrubok.bookstoreMS.entity.Book;

@Repository
public interface BookRepository extends JpaRepository<Book, Long>{

}
