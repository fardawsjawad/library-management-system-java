package com.library.management.validator;


import com.library.management.model.Status;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;

/**
 * Utility class that provides validation methods for transaction-related inputs.
 * <p>
 * This class ensures that data provided for transactions—such as transaction ID, user ID,
 * book ID, borrow and return dates, and transaction status—is valid and well-formatted
 * before being processed or stored.
 * </p>
 *
 * <p>
 * All methods are static and can be used without creating an instance of this class.
 * </p>
 */
public class TransactionInputValidator {

    //-----------------------------------------------------------------------
    /**
     * Validates the transaction ID string to ensure it is a positive integer.
     *
     * @param transactionIdStr the transaction ID as a string
     * @return {@code true} if the ID is a valid positive integer; {@code false} otherwise
     */
    public static boolean isValidTransactionId(String transactionIdStr) {
        if (transactionIdStr == null) return false;
        try {
            int transactionId = Integer.parseInt(transactionIdStr.trim());
            return transactionId > 0;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    //-----------------------------------------------------------------------
    /**
     * Validates the user ID string to ensure it is a positive integer.
     *
     * @param userIdStr the user ID as a string
     * @return {@code true} if the ID is a valid positive integer; {@code false} otherwise
     */
    public static boolean isValidUserId(String userIdStr) {
        if(userIdStr == null) return false;

        try {
            int userId = Integer.parseInt(userIdStr.trim());
            return userId > 0;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    //-----------------------------------------------------------------------
    /**
     * Validates the book ID string to ensure it is a positive integer.
     *
     * @param bookIdStr the book ID as a string
     * @return {@code true} if the ID is a valid positive integer; {@code false} otherwise
     */
    public static boolean isValidBookId(String bookIdStr) {
        if(bookIdStr == null) return false;

        try {
            int bookId = Integer.parseInt(bookIdStr.trim());
            return bookId > 0;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    //-----------------------------------------------------------------------
    /**
     * Validates the borrow date string to ensure it is a valid ISO-formatted date
     * and not a future date.
     *
     * @param borrowDateStr the borrow date as a string
     * @return {@code true} if the date is valid and not in the future; {@code false} otherwise
     */
    public static boolean isValidBorrowDate(String borrowDateStr) {
        if (borrowDateStr == null) return false;

        try {
            LocalDate borrowDate = LocalDate.parse(borrowDateStr);
            return !borrowDate.isAfter(LocalDate.now());
        } catch (DateTimeParseException e) {
            return false;
        }
    }

    //-----------------------------------------------------------------------
    /**
     * Validates the return date string against the borrow date string.
     * <p>
     * The return date can be null or empty (to represent a book not yet returned),
     * but if provided, it must not be before the borrow date.
     * </p>
     *
     * @param borrowDateStr the borrow date as a string
     * @param returnDateStr the return date as a string
     * @return {@code true} if the return date is null/empty or comes after the borrow date; {@code false} otherwise
     */
    public static boolean isValidReturnDate(String borrowDateStr, String returnDateStr) {
        if(borrowDateStr == null || borrowDateStr.trim().isEmpty()) return false;

        try {
            LocalDate borrowDate = LocalDate.parse(borrowDateStr);

            if(returnDateStr == null || returnDateStr.trim().isEmpty()) {
                return true;
            }

            LocalDate returnDate = LocalDate.parse(returnDateStr);
            return !returnDate.isBefore(borrowDate);
        } catch (DateTimeParseException e) {
            return false;
        }
    }

    //-----------------------------------------------------------------------
    /**
     * Validates the transaction status string to ensure it matches a valid {@link Status} value.
     * <p>
     * Acceptable values (case-insensitive): {@code BORROWED}, {@code RETURNED}.
     * </p>
     *
     * @param statusStr the status as a string
     * @return {@code true} if the status is valid; {@code false} otherwise
     */
    public static boolean isValidStatus(String statusStr) {
        if(statusStr == null || statusStr.trim().isEmpty()) return false;

        try {
            Status status = Status.valueOf(statusStr.trim().toUpperCase());
            return status.equals(Status.BORROWED) || status.equals(Status.RETURNED);
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

}
