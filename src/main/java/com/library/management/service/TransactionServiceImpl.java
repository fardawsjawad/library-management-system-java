package com.library.management.service;



import com.library.management.dao.BookDAO;
import com.library.management.dao.TransactionDAO;
import com.library.management.dao.UserDAO;
import com.library.management.exception.BookNotFoundException;
import com.library.management.exception.NoAvailableCopiesException;
import com.library.management.exception.UserNotFoundException;
import com.library.management.model.*;

import java.time.LocalDate;
import java.util.List;

/**
 * Class: TransactionServiceImpl
 *
 * Description:
 * - Implements the TransactionService interface to manage borrowing and returning of books in the Library Management System.
 * - Validates user and book existence, checks for copy availability, enforces borrowing rules, and updates transaction states.
 * - Relies on DAO classes for persistence and UserServiceImpl for additional user-related business logic.
 *
 * Dependencies:
 * - TransactionDAO: Handles CRUD operations related to transactions.
 * - UserDAO, BookDAO: Used to validate existence and status of users and books.
 * - UserServiceImpl: Ensures valid member-only borrowing.
 *
 * Key Responsibilities:
 * - Borrow and return books with validations
 * - Fetch transactions by ID, user, or status
 * - Track user borrowing history and active transactions
 * - Enforce business rules related to borrowing and returning
 *
 * @author Fardaws Jawad
 */
public class TransactionServiceImpl implements TransactionService{

    private final TransactionDAO transactionDAO;
    private final UserServiceImpl userService;

    //-----------------------------------------------------------------------
    /**
     * Initializes DAO and service dependencies.
     */
    public TransactionServiceImpl() {
        this.transactionDAO = new TransactionDAO();
        this.userService = new UserServiceImpl();
    }

    //-----------------------------------------------------------------------
    /**
     * Handles the logic to borrow a book and create a new transaction.
     *
     * @param transaction Transaction - The transaction details including user, book, and dates
     * @return boolean - true if borrowing is successful
     * @throws NoAvailableCopiesException - if no copies of the book are available
     * @throws IllegalArgumentException - if any validation fails
     */
    @Override
    public boolean borrowBook(Transaction transaction) throws NoAvailableCopiesException {
        if (transaction == null) {
            throw new IllegalArgumentException("Transaction cannot be null.");
        }

        UserDAO userDAO = new UserDAO();
        BookDAO bookDAO = new BookDAO();
        User user = userDAO.getUserById(transaction.getUserId());
        Book book = bookDAO.getBookById(transaction.getBookId());

        if (user == null) {
            throw new UserNotFoundException("User or Book not found for the given ID.");
        }

        if (book == null) {
            throw new BookNotFoundException("Book not found for the given ID.");
        }

        if (book.getNumberOfAvailableCopies() == 0) {
            throw new NoAvailableCopiesException("Currently there are no copies available for this book.");
        }

        if (transaction.getBorrowDate() == null) {
            throw new IllegalArgumentException("Borrow date cannot be null.");
        }

        if (transaction.getBorrowDate().isAfter(LocalDate.now())) {
            throw new IllegalArgumentException("Borrow date cannot be in the future.");
        }

        if (transaction.getReturnDate() != null &&
                transaction.getReturnDate().isBefore(transaction.getBorrowDate())) {
            throw new IllegalArgumentException("Return date cannot be before borrow date.");
        }

        return transactionDAO.addTransaction(transaction);
    }

    //-----------------------------------------------------------------------
    /**
     * Processes the return of a borrowed book.
     *
     * @param transactionId int - ID of the transaction to be updated
     * @param returnDate LocalDate - The return date
     * @return boolean - true if the return is processed successfully
     * @throws IllegalArgumentException - if inputs are invalid or transaction already returned
     */
    @Override
    public boolean returnBook(int transactionId, LocalDate returnDate) {
        if(transactionId <= 0) {
            throw new IllegalArgumentException("Valid transaction ID required to return the book.");
        }

        Transaction transaction = transactionDAO.getTransactionById(transactionId);

        if(transaction == null) {
            throw new IllegalArgumentException("Transaction not found for the given ID.");
        }

        if(returnDate == null) {
            throw new IllegalArgumentException("Return date must not be null.");
        }

        if(transaction.getStatus() == Status.RETURNED) {
            throw new IllegalArgumentException("Book has already been returned.");
        }

        if(returnDate.isBefore(transaction.getBorrowDate())) {
            throw new IllegalArgumentException("Return date cannot be before borrow date.");
        }

        return transactionDAO.returnBook(transactionId, returnDate);
    }

    //-----------------------------------------------------------------------
    /**
     * Retrieves a transaction by its ID.
     *
     * @param transactionId int - ID of the transaction
     * @return Transaction - The transaction object, or null if not found
     */
    @Override
    public Transaction getTransactionById(int transactionId) {
        if (transactionId <= 0) {
            throw new IllegalArgumentException("Valid transaction ID required.");
        }

        return transactionDAO.getTransactionById(transactionId);
    }

    //-----------------------------------------------------------------------
    /**
     * Retrieves all transactions in the system.
     *
     * @return List<Transaction> - List of all transactions
     */
    @Override
    public List<Transaction> getAllTransactions() {
        return transactionDAO.getAllTransactions();
    }

    //-----------------------------------------------------------------------
    /**
     * Retrieves all currently active (unreturned) transactions.
     *
     * @return List<Transaction> - List of active transactions
     */
    @Override
    public List<Transaction> getActiveTransactions() {
        return transactionDAO.getActiveTransactions();
    }

    //-----------------------------------------------------------------------
    /**
     * Retrieves all transactions associated with a specific user.
     *
     * @param userId int - ID of the user
     * @return List<Transaction> - List of the user's transactions
     */
    @Override
    public List<Transaction> getTransactionsByUserId(int userId) {
        if (userId <= 0) {
            throw new IllegalArgumentException("Valid user ID required.");
        }

        return transactionDAO.getTransactionsByUserId(userId);
    }

    //-----------------------------------------------------------------------
    /**
     * Retrieves the borrowing history of a specific user.
     *
     * @param userId int - ID of the user
     * @return List<BorrowingHistory> - List of the user's past borrow records
     * @throws UserNotFoundException - if the user does not exist
     * @throws IllegalArgumentException - if the user is not a member
     */
    @Override
    public List<BorrowingHistory> getBorrowingHistoryByUserId(int userId) {
        if (userId < 0) throw new IllegalArgumentException("Invalid user ID.");

        User user = userService.getUserById(userId);
        if (user == null) {
            throw new UserNotFoundException("User with the ID " + userId + " does not exist.");
        }

        if (user.getUserType().equals(User_Type.ADMIN)) {
            throw new IllegalArgumentException("Book borrowing is restricted to members only. Administrators cannot borrow books.");
        }

        return transactionDAO.getBorrowingHistoryByUserId(userId);
    }

    //-----------------------------------------------------------------------
    /**
     * Retrieves the transaction ID for a user-book combination.
     *
     * @param userId int - User ID
     * @param bookId int - Book ID
     * @return int - Transaction ID, or -1 if not found
     */
    @Override
    public int getTransactionId(int userId, int bookId) {
        if (userId <= 0 || bookId <= 0) throw new IllegalArgumentException("Invalid user ID or book ID.");

        return transactionDAO.getTransactionId(userId, bookId);
    }

    //-----------------------------------------------------------------------
    /**
     * Retrieves the transaction ID specifically for returning a book.
     *
     * @param userId int - User ID
     * @param bookId int - Book ID
     * @return int - Transaction ID if found, otherwise -1
     */
    @Override
    public int getTransactionIdForReturnBook(int userId, int bookId) {
        if (userId <= 0 || bookId <= 0) throw new IllegalArgumentException("Invalid user ID or book ID.");

        return transactionDAO.getTransactionIdForBorrowedBook(userId, bookId);
    }

    //-----------------------------------------------------------------------
    /**
     * Checks if a user has currently borrowed a specific book.
     *
     * @param userId int - User ID
     * @param bookId int - Book ID
     * @return boolean - true if book is currently borrowed by the user, false otherwise
     */
    @Override
    public boolean isBookBorrowedByUser(int userId, int bookId) {
        if (userId <= 0 || bookId <= 0) throw new IllegalArgumentException("Invalid user ID or book ID.");

        return transactionDAO.getTransactionIdForBorrowedBook(userId, bookId) != -1;
    }
}
