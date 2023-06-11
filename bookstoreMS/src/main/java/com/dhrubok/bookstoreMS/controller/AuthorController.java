package com.dhrubok.bookstoreMS.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import com.dhrubok.bookstoreMS.dao.AuthorRepository;
import com.dhrubok.bookstoreMS.dao.BookRepository;
import com.dhrubok.bookstoreMS.entity.Author;
import com.dhrubok.bookstoreMS.entity.Book;

import org.springframework.http.HttpHeaders;

@Controller
public class AuthorController {
	@Autowired
	private	AuthorRepository aRepo;
	
	@GetMapping("/authorList")
	public ModelAndView authorList() {
		ModelAndView mav = new ModelAndView("author-list");
		mav.addObject("authors", aRepo.findAll());
		return mav;
	}
	@GetMapping("/addAuthor")
	public ModelAndView addAuthor() {
		ModelAndView mav = new ModelAndView("add-author");
		Author newAuthor = new Author();
		mav.addObject("author", newAuthor);
		return mav;
	}
	@PostMapping("/saveAuthor")
	public String saveAuthor(@ModelAttribute Author author) {
		aRepo.save(author);
		return "redirect:/authorList";
	}
	@GetMapping("/authorUpdate")
	public ModelAndView authorUpdate(@RequestParam Long authorId) {
		ModelAndView mav = new ModelAndView("add-author");
		Author author = aRepo.findById(authorId).get();
		mav.addObject("author", author);
		return mav;
	}

	@PostMapping("/authorDelete")
	public ResponseEntity<String> checkAuthorDeletion(@RequestParam Long authorId) {
	    Author author = aRepo.findById(authorId).orElse(null);
	    if (author != null) {
			System.out.println("i am here");

	        List<Book> books = author.getBooks();
	        if (!books.isEmpty()) {
	            return ResponseEntity.ok("hasBooks");
	        } else {
	            return ResponseEntity.ok("noBooks");
	        }
	    }
	    return ResponseEntity.badRequest().body("Author not found");
	}

	@PostMapping("/authorDeleteConfirmed")
	public ResponseEntity<String> deleteAuthor(@RequestParam Long authorId) {
	    Author author = aRepo.findById(authorId).orElse(null);
	    if (author != null) {
	        List<Book> books = author.getBooks();
	        for (Book book : books) {
	            book.getAuthors().remove(author);
	        }
	        books.clear();
	        aRepo.delete(author);
	        return ResponseEntity.ok("Author deleted successfully");
	    } else {
	        return ResponseEntity.badRequest().body("Author not found");
	    }
	}
}
