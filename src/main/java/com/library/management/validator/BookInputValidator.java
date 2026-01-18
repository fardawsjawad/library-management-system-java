package com.library.management.validator;


/**
 * Utility class that provides validation methods for book-related input fields.
 * <p>
 * This class ensures that book data such as ID, title, author, genre, ISBN,
 * and copy counts meet specific constraints before being processed or stored.
 * </p>
 *
 * <p>
 * All methods are static and stateless, allowing direct access without instantiation.
 * </p>
 */
public class BookInputValidator {

    //-----------------------------------------------------------------------
    /**
     * Validates the book ID string to ensure it is a positive integer.
     *
     * @param bookIdStr the book ID as a string
     * @return {@code true} if the string represents a valid positive integer; {@code false} otherwise
     */
    public static boolean isValidBookId(String bookIdStr) {
        if (bookIdStr == null) return false;

        try {
            int bookId = Integer.parseInt(bookIdStr.trim());
            return bookId > 0;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    //-----------------------------------------------------------------------
    /**
     * Validates the book title to ensure it is not null or empty.
     *
     * @param title the book title
     * @return {@code true} if the title is non-null and non-empty; {@code false} otherwise
     */
    public static boolean isValidTitle(String title) {
        return title != null && !title.trim().isEmpty();
    }

    //-----------------------------------------------------------------------
    /**
     * Validates the book author name to ensure it is not null or empty.
     *
     * @param author the author's name
     * @return {@code true} if the author's name is non-null and non-empty; {@code false} otherwise
     */
    public static boolean isValidAuthor(String author) {
        return author != null && !author.trim().isEmpty();
    }

    //-----------------------------------------------------------------------
    /**
     * Validates the book genre to ensure it is not null, not empty,
     * and does not exceed 50 characters.
     *
     * @param genre the book genre
     * @return {@code true} if the genre is valid; {@code false} otherwise
     */
    public static boolean isValidGenre(String genre) {
        return genre != null && !genre.trim().isEmpty() && genre.length() <= 50;
    }

    //-----------------------------------------------------------------------
    /**
     * Validates the book's ISBN number.
     * <p>
     * The method supports both ISBN-10 (with optional 'X' or 'x' as the last character)
     * and ISBN-13 formats. Hyphens are removed before validation.
     * </p>
     *
     * @param isbn the ISBN number as a string
     * @return {@code true} if the ISBN is valid; {@code false} otherwise
     */
    public static boolean isValidIsbn(String isbn) {
        if(isbn == null) return false;

        isbn = isbn.replaceAll("-", "").trim(); // Remove hyphens and spaces

        return isbn.matches("^\\d{9}[\\dXx]$") || isbn.matches("^\\d{13}$");
    }

    //-----------------------------------------------------------------------
    /**
     * Validates the total number of copies to ensure it is a positive integer.
     *
     * @param input the total copies as a string
     * @return {@code true} if it represents a valid positive integer; {@code false} otherwise
     */
    public static boolean isValidNumberOfTotalCopies(String input) {
        if(input == null) return false;

        try {
            int totalCopies = Integer.parseInt(input.trim());
            return totalCopies > 0;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    //-----------------------------------------------------------------------
    /**
     * Validates the number of available copies to ensure it is a non-negative integer
     * and does not exceed the total number of copies.
     *
     * @param input the available copies as a string
     * @param totalCopies the total number of copies
     * @return {@code true} if the available copies are within the valid range; {@code false} otherwise
     */
    public static boolean isValidNumberOfAvailableCopies(String input, int totalCopies) {
        if(input == null) return false;

        try {
            int availableCopies = Integer.parseInt(input.trim());
            return availableCopies >= 0 && availableCopies <= totalCopies;
        } catch (NumberFormatException e) {
            return false;
        }
    }

}
