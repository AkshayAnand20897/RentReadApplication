package controllers;


import entity.Book;
import entity.Rental;
import entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import repositories.BookRepository;
import repositories.RentalRepository;
import repositories.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/books")
public class RentalController {

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private RentalRepository rentalRepository;

    @Autowired
    private UserRepository userRepository;

    @PostMapping("/{bookId}/rent")
    public String rentBook(@PathVariable Long bookId, @RequestParam Long userId) {
        Book book = bookRepository.findById(bookId).orElseThrow(() -> new RuntimeException("Book not found"));
        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));

        List<Rental> rentals = rentalRepository.findByUserId(userId);
        if (rentals.size() >= 2) {
            throw new RuntimeException("User has already rented 2 books");
        }

        Rental rental = new Rental();
        rental.setBook(book);
        rental.setUser(user);
        rental.setRentedAt(LocalDateTime.now());
        rentalRepository.save(rental);

        return "Book rented successfully";
    }

    @PostMapping("/{bookId}/return")
    public String returnBook(@PathVariable Long bookId, @RequestParam Long userId) {
        Rental rental = rentalRepository.findAll().stream()
                .filter(r -> r.getBook().getId().equals(bookId) && r.getUser().getId().equals(userId))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Rental not found"));

        rentalRepository.delete(rental);

        return "Book";
    }
}

