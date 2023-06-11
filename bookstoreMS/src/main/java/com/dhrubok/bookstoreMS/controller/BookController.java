package com.dhrubok.bookstoreMS.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
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

@Controller
public class BookController {
	
	@Autowired
	private BookRepository bRepo;
	@Autowired
	private AuthorRepository aRepo;
	
	@GetMapping({"/test"})
	public ModelAndView testMethod() {
		System.out.println("inside list");
		ModelAndView mav = new ModelAndView("app-layout");
		//mav.addObject("employees", bRepo.findAll());
		return mav;
	}
	@GetMapping("/addBook")
	public ModelAndView addBookForm() {
	    ModelAndView mav = new ModelAndView("add-book");
	    Book newBook = new Book();
	    mav.addObject("book", newBook);
	    List<Author> authors = aRepo.findAll();
	    mav.addObject("authors", authors);
	    return mav;
	}
	@GetMapping("/")
	@GetMapping("/bookList")
	public ModelAndView bookList() {
		ModelAndView mav = new ModelAndView("book-list");
		mav.addObject("books", bRepo.findAll());
		return mav;
	}
	@GetMapping("/bookDeleteList")
	public ModelAndView bookDeleteList() {
		ModelAndView mav = new ModelAndView("delete-book");
		mav.addObject("books", bRepo.findAll());
		return mav;
	}
	@GetMapping("/bookDelete")
	public String deleteBook(@RequestParam Long bookId) {
	    Optional<Book> optionalBook = bRepo.findById(bookId);
	    if (optionalBook.isPresent()) {
	        Book book = optionalBook.get();
	        book.getAuthors().forEach(author -> author.getBooks().remove(book));
	        book.getAuthors().clear(); // Remove all associations with authors
	        bRepo.delete(book);
	    }
	    return "redirect:/bookDeleteList";
	}

	@PostMapping("/bookSave")
	public String saveBook(@ModelAttribute Book book, @RequestParam("authorIds") List<Long> authorIds) {
	    List<Author> authors = aRepo.findAllById(authorIds);

	    book.setAuthors(authors);

	    if (book.getBookId() == null) {
	        // New book, save it
	        for (Author author : authors) {
	            author.getBooks().add(book);
	        }
	        bRepo.save(book);
	    } else {
	        // Existing book, merge changes
	        Book existingBook = bRepo.findById(book.getBookId()).orElse(null);
	        if (existingBook != null) {
	            existingBook.setTitle(book.getTitle());
	            existingBook.setIsbn(book.getIsbn());
	            existingBook.setPublicationYear(book.getPublicationYear());
	            existingBook.setPrice(book.getPrice());
	            for (Author author : existingBook.getAuthors()) {
	                author.getBooks().remove(existingBook);
	            }
	            existingBook.getAuthors().clear();

	            existingBook.setAuthors(authors);
	            for (Author author : authors) {
	                author.getBooks().add(existingBook);
	            }
	            bRepo.save(existingBook);
	        }
	    }

	    return "redirect:/bookList";
	}

	@GetMapping("/bookShow")
	public ModelAndView showBook(@RequestParam Long bookId) {
	    ModelAndView mav = new ModelAndView("show-book");
	    Book book = bRepo.findById(bookId).orElse(null);
	    if (book != null) {
	        List<Author> authors = book.getAuthors();
	        mav.addObject("book", book);
	        mav.addObject("authors", authors);
	    }
	    return mav;
	}
	@GetMapping("/bookUpdate")
	public ModelAndView bookUpdate(@RequestParam Long bookId, @ModelAttribute Book updatedBook) {
	    ModelAndView mav = new ModelAndView("update-book");
	    Book book = bRepo.findById(bookId).orElse(null);
	    if (book != null) {
	        List<Author> allAuthors = aRepo.findAll();
	        List<Author> selectedAuthors = book.getAuthors();
	        
	        mav.addObject("book", book);
	        mav.addObject("allAuthors", allAuthors);
	        mav.addObject("selectedAuthors", selectedAuthors);
	    }
	    return mav;
	}
}
