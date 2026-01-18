package com.library.management.dao;

import com.library.management.model.Book;
import com.library.management.util.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;


/**
 * The {@code BookDAO} class provides a data access layer for performing CRUD operations
 * and custom queries related to the {@link com.library.management.model.Book} entity.
 *
 * <p>This class encapsulates all interactions with the 'books' table in the database,
 * including adding new books, retrieving book records by ID, title, author, or genre,
 * updating specific fields or full book records, deleting books, and performing searches.
 * It also includes methods to retrieve books based on availability or borrowing status.
 *
 * <p>All database interactions use prepared statements to prevent SQL injection and
 * ensure performance and security. Resources such as connections and result sets are
 * managed using try-with-resources for proper cleanup.
 *
 * <p>This class follows the DAO (Data Access Object) design pattern and is intended to
 * separate persistence logic from business logic. It assumes a valid JDBC connection
 * is provided by the {@link com.library.management.util.DatabaseConnection} utility class.
 *
 * <p>Typical usage involves creating an instance of this class in the service layer
 * to perform book-related database operations in a Library Management System.
 *
 * @author Fardaws Jawad
 * @version 1.0
 * @since 2025-07-04
 * @see com.library.management.model.Book
 */
public class BookDAO {

    //-----------------------------------------------------------------------
    /**
     * Adds a new book record to the database.
     * This method inserts the provided Book object's details into the 'books' table
     * using a prepared SQL statement to ensure secure and efficient insertion.
     *
     * @param book the Book object containing information to be stored in the database
     * @return true if the book was successfully added; false otherwise
     * @throws RuntimeException if a database error occurs during the operation
     */
    public boolean addBook(Book book) {
        // SQL query to insert a new book record into the books table
        String sqlStatement = "INSERT INTO books (title, author, genre, isbn, is_available, number_of_total_copies, number_of_available_copies) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?)";

        // Use try-with-resources to ensure the connection and statement are closed automatically
        try(Connection connection = DatabaseConnection.getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(sqlStatement)) {

            // Set parameters in the query based on the Book object's fields
            preparedStatement.setString(1, book.getTitle());
            preparedStatement.setString(2, book.getAuthor());
            preparedStatement.setString(3, book.getGenre());
            preparedStatement.setString(4, book.getIsbn());
            preparedStatement.setBoolean(5, book.isAvailable());
            preparedStatement.setInt(6, book.getNumberOfTotalCopies());
            preparedStatement.setInt(7, book.getNumberOfAvailableCopies());

            // Execute the update and check if at least one row was inserted
            int rowsAffected = preparedStatement.executeUpdate();

            return rowsAffected > 0;

        } catch (SQLException e) {
            // Log error message to standard error and rethrow as runtime exception
            System.err.println("Failed to add book: " + e.getMessage());
            throw new RuntimeException("Could not insert book into database", e);
        }
    }

    //-----------------------------------------------------------------------
    /**
     * Retrieves a book from the database based on its unique book ID.
     * This method performs a SELECT query on the 'books' table and constructs
     * a Book object from the result if a matching record is found.
     *
     * @param bookId the unique ID of the book to retrieve
     * @return a Book object if the book is found; null if no matching record exists
     * @throws RuntimeException if a database access error occurs
     */
    public Book getBookById(int bookId) {
        // SQL query to fetch book details using the book_id
        String sqlStatement = "SELECT * FROM books WHERE book_id = ?";

        // Using try-with-resources to automatically manage connection and statement
        try(Connection connection = DatabaseConnection.getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(sqlStatement)) {

            // Set the bookId parameter in the prepared statement
            preparedStatement.setInt(1, bookId);

            // Execute the query and process the result set
            try(ResultSet resultSet = preparedStatement.executeQuery()) {
                // If a record is found, construct and return a Book object
                if(resultSet.next()) {
                    return new Book(
                            resultSet.getInt("book_id"),
                            resultSet.getString("title"),
                            resultSet.getString("author"),
                            resultSet.getString("genre"),
                            resultSet.getString("isbn"),
                            resultSet.getBoolean("is_available"),
                            resultSet.getInt("number_of_total_copies"),
                            resultSet.getInt("number_of_available_copies")
                    );
                }
            }

        } catch (SQLException e) {
            // Log and rethrow any SQL exceptions with context for easier debugging
            System.err.println("Error retrieving book with ID " + bookId + ": " + e.getMessage());
            throw new RuntimeException("Failed to retrieve book with ID " + bookId + ": " + e.getMessage(), e);
        }

        // If no matching record is found, return null
        return null;
    }

    //-----------------------------------------------------------------------
    /**
     * Retrieves all book records from the database.
     * This method performs a SELECT * query on the 'books' table and returns a list
     * of Book objects representing all records in the table.
     *
     * @return a list of all books in the database; an empty list if none are found
     * @throws RuntimeException if a database access error occurs
     */
    public List<Book> getAllBooks() {
        // Create a list to hold Book objects
        List<Book> bookList = new ArrayList<>();

        // SQL query to retrieve all rows from the books table
        String sqlStatement = "SELECT * FROM books";

        // Use try-with-resources to manage database resources automatically
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sqlStatement);
             ResultSet resultSet = preparedStatement.executeQuery()) {

            // Loop through the result set and populate the book list
            while(resultSet.next()) {
                bookList.add(
                        new Book(resultSet.getInt("book_id"),
                                resultSet.getString("title"),
                                resultSet.getString("author"),
                                resultSet.getString("genre"),
                                resultSet.getString("isbn"),
                                resultSet.getBoolean("is_available"),
                                resultSet.getInt("number_of_total_copies"),
                                resultSet.getInt("number_of_available_copies"))
                );
            }

        } catch (SQLException e) {
            // Log the error and rethrow it wrapped in a RuntimeException
            System.err.println("Failed to fetch books: " + e.getMessage());
            throw new RuntimeException("Error fetching all books: " + e.getMessage(), e);
        }

        // Return the list of books (could be empty if no books are found)
        return bookList;
    }

    //-----------------------------------------------------------------------
    /**
     * Updates the details of an existing book in the database.
     * This method performs an UPDATE operation on the 'books' table using the provided
     * Book object's data, identified by its book ID.
     *
     * @param book the Book object containing the updated information
     * @return true if the update was successful (i.e., at least one row was affected); false otherwise
     * @throws RuntimeException if a database error occurs during the update operation
     */
    public boolean updateBook(Book book) {
        // SQL query to update book fields based on book_id
        String sqlStatement = "UPDATE books SET title = ?, author = ?, genre = ?, " +
                "isbn = ?, is_available = ?, number_of_total_copies = ?, number_of_available_copies = ? " +
                "WHERE book_id = ?";

        // Use try-with-resources to ensure proper closing of resources
        try (Connection connection = DatabaseConnection.getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(sqlStatement)) {

            // Set parameters in the prepared statement from the Book object
            preparedStatement.setString(1, book.getTitle());
            preparedStatement.setString(2, book.getAuthor());
            preparedStatement.setString(3, book.getGenre());
            preparedStatement.setString(4, book.getIsbn());
            preparedStatement.setBoolean(5, book.isAvailable());
            preparedStatement.setInt(6, book.getNumberOfTotalCopies());
            preparedStatement.setInt(7, book.getNumberOfAvailableCopies());
            preparedStatement.setInt(8, book.getBookId()); // book_id used for identifying the record

            // Execute the update and return true if at least one row was affected
            return preparedStatement.executeUpdate() > 0;

        } catch (SQLException e) {
            // Log and rethrow the exception for higher-level handling
            System.err.println("Error while updating book with ID " + book.getBookId() + ": " + e.getMessage());
            throw new RuntimeException("Failed to update book with ID " + book.getBookId() + ": " + e.getMessage(), e);
        }
    }

    //-----------------------------------------------------------------------
    /**
     * Deletes a book record from the database based on the given book ID.
     * This method performs a DELETE operation on the 'books' table to remove
     * the specified book.
     *
     * @param bookId the ID of the book to be deleted
     * @return true if the deletion was successful (i.e., at least one row was affected); false otherwise
     * @throws RuntimeException if a database access error occurs during the operation
     */
    public boolean deleteBook(int bookId) {
        // SQL query to delete a book by its ID
        String sqlStatement = "DELETE FROM books WHERE book_id = ?";

        // Use try-with-resources to ensure resources are automatically closed
        try(Connection connection = DatabaseConnection.getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(sqlStatement)) {

            // Set the bookId parameter in the query
            preparedStatement.setInt(1, bookId);

            // Execute the deletion and return true if a row was affected
            return preparedStatement.executeUpdate() > 0;

        } catch (SQLException e) {
            // Log and rethrow the exception with context
            System.err.println("Error deleting book with ID " + bookId + ": " + e.getMessage());
            throw new RuntimeException("Failed to delete book", e);        }
    }

    //-----------------------------------------------------------------------
    /**
     * Retrieves a list of books that are currently available for borrowing.
     * This method queries the 'books' table and returns only those records
     * where the number of available copies is greater than zero.
     *
     * @return a list of available Book objects; an empty list if none are available
     * @throws RuntimeException if a database access error occurs
     */
    public List<Book> getAvailableBooks() {
        // List to store books with available copies
        List<Book> bookList = new ArrayList<>();

        // SQL query to fetch books that have more than 0 available copies
        String sqlStatement = "SELECT * FROM books " +
                "WHERE number_of_available_copies > 0";

        // Use try-with-resources to automatically manage database resources
        try (Connection connection = DatabaseConnection.getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(sqlStatement);
            ResultSet resultSet = preparedStatement.executeQuery()) {

            // Iterate through the result set and add each available book to the list
            while (resultSet.next()) {
                bookList.add(
                        new Book(
                                resultSet.getInt("book_id"),
                                resultSet.getString("title"),
                                resultSet.getString("author"),
                                resultSet.getString("genre"),
                                resultSet.getString("isbn"),
                                resultSet.getBoolean("is_available"),
                                resultSet.getInt("number_of_total_copies"),
                                resultSet.getInt("number_of_available_copies")
                        )
                );
            }

        } catch (SQLException e) {
            // Log and rethrow exceptions with context
            System.err.println("Error retrieving available books: " + e.getMessage());
            throw new RuntimeException("Failed to retrieve available books: " + e.getMessage(), e);
        }

        // Return the list of available books (could be empty if there are no available books)
        return bookList;
    }

    //-----------------------------------------------------------------------
    /**
     * Retrieves a list of books from the database that belong to a specific genre.
     * This method performs a parameterized query on the 'books' table to filter
     * books by the given genre.
     *
     * @param genre the genre to filter books by (e.g., "Science Fiction", "Mystery")
     * @return a list of Book objects matching the specified genre; empty list if none found
     * @throws RuntimeException if a database access error occurs
     */
    public List<Book> getBooksByGenre(String genre) {
        // List to hold books matching the specified genre
        List<Book> bookList = new ArrayList<>();

        // SQL query to fetch books by genre
        String sqlStatement = "SELECT * FROM books " +
                "WHERE genre = ?";

        // Use try-with-resources to ensure resources are properly closed
        try (Connection connection = DatabaseConnection.getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(sqlStatement)) {

            // Set the genre parameter in the query
            preparedStatement.setString(1, genre);

            // Execute the query and process the result set
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    bookList.add(
                            new Book(
                                    resultSet.getInt("book_id"),
                                    resultSet.getString("title"),
                                    resultSet.getString("author"),
                                    resultSet.getString("genre"),
                                    resultSet.getString("isbn"),
                                    resultSet.getBoolean("is_available"),
                                    resultSet.getInt("number_of_total_copies"),
                                    resultSet.getInt("number_of_available_copies")
                            )
                    );
                }
            }

        } catch (SQLException e) {
            // Log and rethrow the exception with detailed context
            System.err.println("Error retrieving books with genre '" + genre + "': " + e.getMessage());
            throw new RuntimeException("Failed to retrieve books with genre '" + genre + "': " + e.getMessage(), e);        }

        // Return the list of books matching the genre
        // (could be empty if no books with the specified genre is found)
        return bookList;
    }

    //-----------------------------------------------------------------------
    /**
     * Retrieves a book from the database based on its title.
     * This method performs a SELECT query on the 'books' table and returns the first
     * matching book record with the specified title.
     *
     * @param bookTitle the title of the book to search for
     * @return a Book object if a match is found; null if no book with the given title exists
     * @throws RuntimeException if a database access error occurs
     */
    public Book getBookByTitle(String bookTitle) {
        // SQL query to fetch book by title
        String sqlStatement = "SELECT * FROM books WHERE title = ?";

        // Use try-with-resources to manage resources efficiently
        try(Connection connection = DatabaseConnection.getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(sqlStatement)) {

            // Set the title parameter in the prepared statement
            preparedStatement.setString(1, bookTitle);

            // Execute the query and check if a result is found
            try(ResultSet resultSet = preparedStatement.executeQuery()) {
                if(resultSet.next()) {
                    // Return the matched book as a Book object
                    return new Book(
                            resultSet.getInt("book_id"),
                            resultSet.getString("title"),
                            resultSet.getString("author"),
                            resultSet.getString("genre"),
                            resultSet.getString("isbn"),
                            resultSet.getBoolean("is_available"),
                            resultSet.getInt("number_of_total_copies"),
                            resultSet.getInt("number_of_available_copies")
                    );
                }
            }
        } catch (SQLException e) {
            // Log and rethrow any exception for better visibility
            System.err.println("Error retrieving book with title " + bookTitle + ": " + e.getMessage());
            throw new RuntimeException("Failed to retrieve book with title '" + bookTitle + "': " + e.getMessage(), e);
        }

        // Return null if no matching book is found
        return null;
    }

    //-----------------------------------------------------------------------
    /**
     * Retrieves a list of books from the database that are written by a specific author.
     * This method performs a SELECT query on the 'books' table and returns all matching
     * records with the specified author.
     *
     * @param author the name of the author to filter books by
     * @return a list of Book objects written by the specified author; empty list if none found
     * @throws RuntimeException if a database access error occurs
     */
    public List<Book> getBooksByAuthor(String author) {
        // List to store books written by the specified author
        List<Book> bookList = new ArrayList<>();

        // SQL query to fetch books by author name
        String sqlStatement = "SELECT * FROM books WHERE author = ?";

        // Use try-with-resources for automatic resource management
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sqlStatement)) {

            // Set the author name in the prepared statement
            preparedStatement.setString(1, author);

            // Execute the query and process the result set
            try(ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    // Construct and add each matching book to the list
                    bookList.add(new Book (
                            resultSet.getInt("book_id"),
                            resultSet.getString("title"),
                            resultSet.getString("author"),
                            resultSet.getString("genre"),
                            resultSet.getString("isbn"),
                            resultSet.getBoolean("is_available"),
                            resultSet.getInt("number_of_total_copies"),
                            resultSet.getInt("number_of_available_copies")
                    ));
                }
            }
        } catch (SQLException e) {
            // Log the error and rethrow it with contextual information
            System.err.println("Error retrieving book with author " + author + ": " + e.getMessage());
            throw new RuntimeException("Failed to retrieve books by author '" + author + "': " + e.getMessage(), e);
        }

        // Return the list of books by the author (empty if none found)
        return bookList;
    }

    //-----------------------------------------------------------------------
    /**
     * Searches for books in the database where the title, author, or genre contains the given keyword.
     * This method uses SQL LIKE queries to perform partial matches across multiple columns.
     *
     * @param keyword the keyword to search for in title, author, or genre
     * @return a list of Book objects matching the keyword; empty list if no matches found
     * @throws RuntimeException if a database access error occurs
     */
    public List<Book> searchBooks(String keyword) {
        // List to store matching books
        List<Book> bookList = new ArrayList<>();

        // SQL query to search books by title, author, or genre using LIKE operator
        String sqlStatement = "SELECT * FROM books WHERE title LIKE ? OR author LIKE ? OR genre LIKE ?";

        // Use try-with-resources to manage database resources automatically
        try (Connection connection = DatabaseConnection.getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(sqlStatement)) {

            // Add wildcards to keyword for partial matching
            String likeKeyword = "%" + keyword + "%";

            // Set the LIKE pattern in all three placeholders
            preparedStatement.setString(1, likeKeyword);
            preparedStatement.setString(2, likeKeyword);
            preparedStatement.setString(3, likeKeyword);

            // Execute query and process results
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    bookList.add(
                            new Book(
                                    resultSet.getInt("book_id"),
                                    resultSet.getString("title"),
                                    resultSet.getString("author"),
                                    resultSet.getString("genre"),
                                    resultSet.getString("isbn"),
                                    resultSet.getBoolean("is_available"),
                                    resultSet.getInt("number_of_total_copies"),
                                    resultSet.getInt("number_of_available_copies")
                            )
                    );
                }
            }

        } catch (SQLException e) {
            // Log and rethrow the exception with context
            System.err.println("Error searching books with keyword '" + keyword + "': " + e.getMessage());
            throw new RuntimeException("Failed to search books with keyword '" + keyword + "': " + e.getMessage(), e);
        }

        // Return the list of found books (empty if none matched)
        return bookList;
    }

    //-----------------------------------------------------------------------
    /**
     * Updates a specific field of a book record in the database identified by its book ID.
     * Only allows updating certain predefined fields (e.g., title, author, genre, isbn, number_of_total_copies).
     * The method ensures type safety for the provided new value.
     *
     * @param bookId the ID of the book to be updated
     * @param field the field name to update (must be one of the allowed fields)
     * @param newValue the new value to set for the field (must be of type String, Integer, or Boolean)
     * @return true if the update was successful (i.e., at least one row was affected); false otherwise
     * @throws IllegalArgumentException if the field is not allowed or the value type is unsupported
     * @throws RuntimeException if a database access error occurs
     */
    public boolean updateBookField(int bookId, String field, Object newValue) {
        // Define a whitelist of fields that are allowed to be updated dynamically
        Set<String> allowedFields = Set.of(
                "title", "author", "genre", "isbn",
                "number_of_total_copies"
        );

        // Validate the provided field name against the allowed set
        if (!allowedFields.contains(field.toLowerCase())) {
            throw new IllegalArgumentException("Invalid field name");
        }

        // Dynamically construct the SQL update query using the validated field
        String sqlStatement = "UPDATE books SET " + field + " = ? WHERE book_id = ?";

        // Use try-with-resources for proper resource management
        try(Connection connection = DatabaseConnection.getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(sqlStatement)) {

            // Dynamically bind the value based on its type
            if(newValue instanceof Integer) {
                preparedStatement.setInt(1, ((Integer) newValue));
            } else if (newValue instanceof String) {
                preparedStatement.setString(1, newValue.toString());
            } else if (newValue instanceof Boolean) {
                preparedStatement.setBoolean(1, ((Boolean) newValue));
            } else {
                // Unsupported data types are rejected explicitly
                throw new IllegalArgumentException("Unsupported data type.");
            }

            // Set the bookId parameter
            preparedStatement.setInt(2, bookId);

            // Execute the update query
            return preparedStatement.executeUpdate() > 0;

        } catch (SQLException e) {
            // Log and rethrow the exception for higher-level handling
            System.err.println("Failed to update book: " + e.getMessage());
            throw new RuntimeException("Could not update book in the database", e);
        }
    }

    //-----------------------------------------------------------------------
    /**
     * Retrieves a list of books currently borrowed by a specific user.
     * This method performs an INNER JOIN between the 'books' and 'transactions' tables
     * to find all books where the user ID matches and the transaction status is 'borrowed'.
     *
     * @param userId the ID of the user whose borrowed books are to be retrieved
     * @return a list of Book objects that the user has currently borrowed; empty list if none found
     * @throws RuntimeException if a database access error occurs
     */
    public List<Book> getBorrowedBooksByUser(int userId) {
        // List to store books borrowed by the user
        List<Book> bookList = new ArrayList<>();

        // SQL query to fetch books borrowed by the user using a join with the transactions table
        String sqlStatement = "SELECT b.book_id, b.title, b.author, b.genre, b.isbn, b.is_available, " +
                "b.number_of_total_copies, b.number_of_available_copies " +
                "FROM books b " +
                "INNER JOIN transactions t " +
                "ON b.book_id = t.book_id " +
                "WHERE t.user_id = ? " +
                "AND t.status = 'borrowed'";

        // Use try-with-resources to manage DB resources
        try (Connection connection = DatabaseConnection.getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(sqlStatement)) {

            // Set the user ID in the prepared statement
            preparedStatement.setInt(1, userId);

            // Execute the query and populate the book list with the results
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    bookList.add(
                            new Book(
                                    resultSet.getInt("book_id"),
                                    resultSet.getString("title"),
                                    resultSet.getString("author"),
                                    resultSet.getString("genre"),
                                    resultSet.getString("isbn"),
                                    resultSet.getBoolean("is_available"),
                                    resultSet.getInt("number_of_total_copies"),
                                    resultSet.getInt("number_of_available_copies")
                            )
                    );
                }
            }

        } catch (SQLException e) {
            // Log and rethrow the exception for higher-level handling
            System.err.println("Error retrieving borrowed books for user ID " + userId + ": " + e.getMessage());
            throw new RuntimeException("Failed to retrieve borrowed books for user ID " + userId + ": " + e.getMessage(), e);        }

        // Return the list of borrowed books (empty if none found)
        return bookList;
    }

    //-----------------------------------------------------------------------
    /**
     * Retrieves a list of all books that are currently borrowed by any user.
     * This method identifies borrowed books as those where the number of available
     * copies is less than the number of total copies.
     *
     * @return a list of Book objects that are currently borrowed (i.e., not all copies are available);
     *         returns an empty list if no books are currently borrowed
     * @throws RuntimeException if a database access error occurs
     */
    public List<Book> getAllBorrowedBooks() {
        // List to store books that are currently borrowed
        List<Book> bookList = new ArrayList<>();

        // SQL query to find books where some copies are borrowed (available < total)
        String sqlStatement = "SELECT * FROM books " +
                "WHERE number_of_available_copies < number_of_total_copies";

        // Use try-with-resources for safe and automatic resource management
        try (Connection connection = DatabaseConnection.getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(sqlStatement);
            ResultSet resultSet = preparedStatement.executeQuery()) {

            // Process each result and add the borrowed book to the list
            while (resultSet.next()) {
                bookList.add(
                        new Book(
                                resultSet.getInt("book_id"),
                                resultSet.getString("title"),
                                resultSet.getString("author"),
                                resultSet.getString("genre"),
                                resultSet.getString("isbn"),
                                resultSet.getBoolean("is_available"),
                                resultSet.getInt("number_of_total_copies"),
                                resultSet.getInt("number_of_available_copies")
                        )
                );
            }

        } catch (SQLException e) {
            // Log the error and throw a wrapped exception for higher-level handling
            System.err.println("Error retrieving all borrowed books: " + e.getMessage());
            throw new RuntimeException("Failed to retrieve borrowed books: " + e.getMessage(), e);        }

        // Return the list of currently borrowed books
        // (empty if no book is borrowed)
        return bookList;
    }

    //-----------------------------------------------------------------------
    /**
     * Updates the number of available copies for a specific book in the database.
     * This method is typically called when a book is borrowed or returned.
     *
     * @param bookId the ID of the book to update
     * @param numberOfAvailableCopies the new number of available copies to set
     * @return true if the update was successful (i.e., at least one row was affected); false otherwise
     * @throws RuntimeException if a database access error occurs
     */
    public boolean updateNumberOfAvailableCopies(int bookId, int numberOfAvailableCopies) {
        // SQL query to update the number of available copies for a given book
        String sqlStatement = "UPDATE books SET number_of_available_copies = ? " +
                "WHERE book_id = ?";

        // Use try-with-resources to manage DB connection and statement
        try (Connection connection = DatabaseConnection.getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(sqlStatement)) {

            // Set parameters: new available copies and book ID
            preparedStatement.setInt(1, numberOfAvailableCopies);
            preparedStatement.setInt(2, bookId);

            // Execute the update and return success status
            return preparedStatement.executeUpdate() > 0;

        } catch (SQLException e) {
            // Log and rethrow exception with context
            System.err.println("Error updating available copies for book ID " + bookId +
                    " to " + numberOfAvailableCopies + ": " + e.getMessage());
            throw new RuntimeException("Failed to update available copies for book ID " +
                    bookId + ": " + e.getMessage(), e);        }
    }

}
