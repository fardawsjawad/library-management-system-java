package com.library.management.exception;

/**
 * Exception thrown when a user attempts to borrow a book that has no available copies.
 * <p>
 * This exception is typically used in the service layer to enforce business rules
 * around book availability and to prevent borrowing operations when all copies
 * of a book are already checked out.
 *
 * @see com.library.management.service.TransactionService
 * @see com.library.management.dao.BookDAO
 */
public class NoAvailableCopiesException extends Exception {
    /**
     * Constructs a new NoAvailableCopiesException with the specified detail message.
     *
     * @param message the detail message explaining the reason for the exception
     */
    public NoAvailableCopiesException(String message) {
        super(message);
    }

}
