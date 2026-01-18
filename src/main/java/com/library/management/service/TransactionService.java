package com.library.management.service;


import com.library.management.exception.NoAvailableCopiesException;
import com.library.management.model.BorrowingHistory;
import com.library.management.model.Transaction;

import java.time.LocalDate;
import java.util.List;

/**
 * Interface: TransactionService
 *
 * Description:
 * - Defines the contract for managing book borrowing and return operations in the Library Management System.
 * - Handles transaction-related operations such as borrowing, returning, tracking active and historical transactions.
 * - Also includes validation logic to ensure business rules like book availability and borrowing status.
 *
 * Key Responsibilities:
 * - Record and manage book borrowing transactions
 * - Process book returns and update transaction details
 * - Track all active and historical transactions for users
 * - Retrieve transaction IDs and borrowing status for validations
 *
 * @author Fardaws Jawad
 */
public interface TransactionService {

    //-----------------------------------------------------------------------
    /**
     * Borrows a book by creating a new transaction entry.
     *
     * @param transaction Transaction - The transaction object containing user, book, and borrow details
     * @return boolean - true if the transaction is successful, false otherwise
     * @throws NoAvailableCopiesException - if the book has no available copies
     */
    boolean borrowBook(Transaction transaction) throws NoAvailableCopiesException;

    //-----------------------------------------------------------------------
    /**
     * Processes the return of a book and updates the return date in the transaction.
     *
     * @param transactionId int - The transaction ID to be updated
     * @param returnDate LocalDate - The return date to be recorded
     * @return boolean - true if return is processed successfully, false otherwise
     */
    boolean returnBook(int transactionId, LocalDate returnDate);

    //-----------------------------------------------------------------------
    /**
     * Retrieves a transaction by its unique transaction ID.
     *
     * @param transactionId int - ID of the transaction
     * @return Transaction - The transaction object if found, otherwise null
     */
    Transaction getTransactionById(int transactionId);

    //-----------------------------------------------------------------------
    /**
     * Retrieves all transactions in the system (both active and returned).
     *
     * @return List<Transaction> - A list of all transaction records
     */
    List<Transaction> getAllTransactions();

    //-----------------------------------------------------------------------
    /**
     * Retrieves all currently active transactions (books not yet returned).
     *
     * @return List<Transaction> - A list of active transactions
     */
    List<Transaction> getActiveTransactions();

    //-----------------------------------------------------------------------
    /**
     * Retrieves all transactions associated with a specific user.
     *
     * @param userId int - ID of the user
     * @return List<Transaction> - A list of the user's transaction history
     */
    List<Transaction> getTransactionsByUserId(int userId);

    //-----------------------------------------------------------------------
    /**
     * Retrieves the borrowing history for a specific user.
     *
     * @param userId int - ID of the user
     * @return List<BorrowingHistory> - A list of borrowing history entries for the user
     */
    List<BorrowingHistory> getBorrowingHistoryByUserId(int userId);

    //-----------------------------------------------------------------------
    /**
     * Retrieves the transaction ID for a specific user-book pair (used for validation or lookups).
     *
     * @param user_id int - ID of the user
     * @param book_id int - ID of the book
     * @return int - The transaction ID if it exists, otherwise -1
     */
    int getTransactionId(int user_id, int book_id);

    //-----------------------------------------------------------------------
    /**
     * Retrieves the transaction ID needed specifically for returning a book.
     *
     * @param userId int - ID of the user
     * @param bookId int - ID of the book
     * @return int - The transaction ID for the return operation, or -1 if not found
     */
    int getTransactionIdForReturnBook(int userId, int bookId);

    //-----------------------------------------------------------------------
    /**
     * Checks if a specific user has currently borrowed a specific book.
     *
     * @param userId int - ID of the user
     * @param bookId int - ID of the book
     * @return boolean - true if the book is borrowed by the user, false otherwise
     */
    boolean isBookBorrowedByUser(int userId, int bookId);

}
