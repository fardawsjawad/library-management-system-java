package com.library.management.model;

import java.time.LocalDate;
import java.util.Objects;

/**
 * Represents a single borrowing transaction in the Library Management System.
 * <p>
 * This class captures details about when a book was borrowed and returned by a user,
 * along with the transaction ID, user ID, book ID, book title, borrowing/return dates,
 * and current status of the transaction.
 *
 * <p>Example usage:
 * <pre>
 *     BorrowingHistory history = new BorrowingHistory(1, 101, 205, "The Alchemist",
 *         LocalDate.of(2023, 1, 10), LocalDate.of(2023, 1, 25), Status.RETURNED);
 * </pre>
 *
 * @author Fardaws Jawad
 *
 * @see com.library.management.model.Status
 * @see com.library.management.model.User
 * @see com.library.management.model.Book
 */
public class BorrowingHistory {

    /** Unique ID for the borrowing transaction. */
    private int transaction_id;

    /** ID of the user who borrowed the book. */
    private int user_id;

    /** ID of the borrowed book. */
    private int book_id;

    /** Title of the borrowed book (denormalized for quick reference). */
    private String title;

    /** Date when the book was borrowed. */
    private LocalDate borrowDate;

    /** Date when the book was returned. */
    private LocalDate returnDate;

    /** Status of the borrowing transaction (e.g., BORROWED, RETURNED). */
    private Status status;

    //-----------------------------------------------------------------------
    /**
     * Constructs a BorrowingHistory object with all fields.
     *
     * @param transaction_id  unique transaction ID
     * @param user_id         ID of the user who borrowed the book
     * @param book_id         ID of the book borrowed
     * @param title           title of the book
     * @param borrowDate      date the book was borrowed
     * @param returnDate      date the book was returned
     * @param status          current status of the transaction
     */
    public BorrowingHistory(int transaction_id, int user_id, int book_id, String title,
                            LocalDate borrowDate, LocalDate returnDate, Status status) {

        this.transaction_id = transaction_id;
        this.user_id = user_id;
        this.book_id = book_id;
        this.title = title;
        this.borrowDate = borrowDate;
        this.returnDate = returnDate;
        this.status = status;
    }

    //-----------------------------------------------------------------------
    /** @return the transaction ID */
    public int getTransaction_id() {
        return transaction_id;
    }

    /** @param transaction_id sets the transaction ID */
    public void setTransaction_id(int transaction_id) {
        this.transaction_id = transaction_id;
    }

    /** @return the user ID associated with the borrowing record */
    public int getUser_id() {
        return user_id;
    }

    /** @param user_id sets the user ID */
    public void setUser_id(int user_id) {
        this.user_id = user_id;
    }

    /** @return the book ID associated with the borrowing record */
    public int getBook_id() {
        return book_id;
    }

    /** @param book_id sets the book ID */
    public void setBook_id(int book_id) {
        this.book_id = book_id;
    }

    /** @return the title of the borrowed book */
    public String getTitle() {
        return title;
    }

    /** @param title sets the title of the borrowed book */
    public void setTitle(String title) {
        this.title = title;
    }

    /** @return the date when the book was borrowed */
    public LocalDate getBorrowDate() {
        return borrowDate;
    }

    /** @param borrowDate sets the date when the book was borrowed */
    public void setBorrowDate(LocalDate borrowDate) {
        this.borrowDate = borrowDate;
    }

    /** @return the date when the book was returned */
    public LocalDate getReturnDate() {
        return returnDate;
    }

    /** @param returnDate sets the date when the book was returned */
    public void setReturnDate(LocalDate returnDate) {
        this.returnDate = returnDate;
    }

    /** @return the status of the transaction */
    public Status getStatus() {
        return status;
    }

    /** @param status sets the status of the transaction */
    public void setStatus(Status status) {
        this.status = status;
    }

    //-----------------------------------------------------------------------
    /**
     * Returns a formatted string representation of the borrowing record.
     *
     * @return a readable string with transaction details
     */
    @Override
    public String toString() {
        return "Borrowing History Record:\n" +
                "  Transaction ID  : " + transaction_id + "\n" +
                "  User ID         : " + user_id + "\n" +
                "  Book ID         : " + book_id + "\n" +
                "  Title           : " + title + "\n" +
                "  Borrow Date     : " + borrowDate + "\n" +
                "  Return Date     : " + returnDate + "\n" +
                "  Status          : " + status + "\n";
    }

    /**
     * Checks equality between two BorrowingHistory objects based on their transaction ID.
     *
     * @param object the object to compare with
     * @return true if both have the same transaction ID, false otherwise
     */
    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;
        BorrowingHistory that = (BorrowingHistory) object;
        return transaction_id == that.transaction_id;
    }

    /**
     * Generates a hash code based on the transaction ID.
     *
     * @return hash code for this borrowing record
     */
    @Override
    public int hashCode() {
        return Objects.hashCode(transaction_id);
    }
}
