package com.library.management.model;

/**
 * Enum representing the status of a book borrowing transaction in the Library Management System.
 * <p>
 * This enum is used to indicate whether a borrowed book is still with the user or has been returned.
 *
 * <p>Possible values:
 * <ul>
 *     <li>{@code BORROWED} - The book is currently checked out and not yet returned.</li>
 *     <li>{@code RETURNED} - The book has been returned by the user.</li>
 * </ul>
 *
 * <p>Example usage:
 * <pre>
 *     BorrowingHistory history = new BorrowingHistory(...);
 *     history.setStatus(Status.BORROWED);
 * </pre>
 *
 * @author Fardaws Jawad
 *
 * @see com.library.management.model.BorrowingHistory
 */
public enum Status {

    /** Indicates the book has been borrowed and not yet returned. */
    BORROWED,

    /** Indicates the book has been returned by the user. */
    RETURNED
}
