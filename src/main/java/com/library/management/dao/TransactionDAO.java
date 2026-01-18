package com.library.management.dao;

import com.library.management.model.BorrowingHistory;
import com.library.management.model.Status;
import com.library.management.model.Transaction;
import com.library.management.util.DatabaseConnection;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object (DAO) class responsible for performing CRUD operations
 * on the {@code transactions} table in the database.
 * <p>
 * This class encapsulates all SQL interactions related to book borrowings and returns
 * made by users in the Library Management System. It provides high-level methods to:
 * <ul>
 *     <li>Add a new transaction when a book is borrowed</li>
 *     <li>Process a book return and update relevant records</li>
 *     <li>Retrieve transactions by ID, user, or status</li>
 *     <li>Fetch borrowing history joined with book titles</li>
 *     <li>Delete transactions associated with a user</li>
 * </ul>
 * <p>
 * Each method handles JDBC resource management using try-with-resources,
 * translates SQL exceptions into runtime exceptions for upper-layer handling,
 * and ensures transactional consistency where needed (e.g., borrow/return operations).
 *
 * <p>
 * Key Models Used:
 * <ul>
 *     <li>{@link com.library.management.model.Transaction} - Core data model representing a borrowing event</li>
 *     <li>{@link com.library.management.model.Status} - Enum representing transaction state (borrowed/returned)</li>
 *     <li>{@link com.library.management.model.BorrowingHistory} - Extended view combining transaction and book title</li>
 * </ul>
 *
 * @author Fardaws Jawad
 * @see com.library.management.model.Transaction
 * @see com.library.management.util.DatabaseConnection
 */
public class TransactionDAO {

    //-----------------------------------------------------------------------
    /**
     * Adds a transaction to the database for a user borrowing a book.
     * <p>
     * This method performs the following actions as a single database transaction:
     * <ol>
     *     <li>Checks if the requested book has at least one available copy.</li>
     *     <li>If available, inserts a new transaction record into the 'transactions' table.</li>
     *     <li>Updates the 'books' table to decrement the available copy count.</li>
     * </ol>
     * If any of these steps fail, the operation is rolled back to maintain consistency.
     *
     * @param transaction the Transaction object containing user ID, book ID, borrow date, optional return date, and status
     * @return true if the transaction was successfully added and the book's availability updated; false if the book is unavailable or any step fails
     * @throws RuntimeException if a database error occurs during the process
     *
     * @see com.library.management.model.Transaction
     */
    public boolean addTransaction(Transaction transaction) {
        // SQL to check how many copies of the book are available
        String checkAvailabilitySQL = "SELECT number_of_available_copies FROM books WHERE book_id = ?";

        // SQL to insert a new transaction (borrowing record) into the transactions table
        String insertSQL = "INSERT INTO transactions (user_id, book_id, borrow_date, return_date, status) " +
                "VALUES (?, ?, ?, ?, ?)";

        // SQL to update the books table by reducing the number of available copies by 1 for the borrowed book
        String updateBookSQL = "UPDATE books SET number_of_available_copies = number_of_available_copies - 1 " +
                "WHERE book_id = ?";

        // Obtain a database connection (auto-closed at the end of the try block)
        try (Connection connection = DatabaseConnection.getConnection()) {

            connection.setAutoCommit(false); // Begin transaction manually

            // Step 1: Check if the book has at least one available copy
            try (PreparedStatement checkStmt = connection.prepareStatement(checkAvailabilitySQL)) {
                checkStmt.setInt(1, transaction.getBookId());
                try (ResultSet resultSet = checkStmt.executeQuery()) {
                    if(resultSet.next()) {
                        int availableCopies = resultSet.getInt("number_of_available_copies");
                        if(availableCopies < 1) {
                            return false; // Book exists, but no copies available
                        }
                    } else {
                        return false; // Book does not exist in the database
                    }
                }
            }

            // Step 2: Insert the new transaction record
            try (PreparedStatement insertStmt = connection.prepareStatement(insertSQL)) {
                insertStmt.setInt(1, transaction.getUserId());
                insertStmt.setInt(2, transaction.getBookId());
                insertStmt.setDate(3, Date.valueOf(transaction.getBorrowDate()));

                // Handle nullable return date
                if(transaction.getReturnDate() != null) {
                    insertStmt.setDate(4, Date.valueOf(transaction.getReturnDate()));
                } else {
                    insertStmt.setNull(4, Types.NULL);
                }

                // Convert status enum to lowercase string
                insertStmt.setString(5, transaction.getStatus().toString().toLowerCase());

                int rowsInserted = insertStmt.executeUpdate();
                if(rowsInserted == 0) {
                    connection.rollback(); // Rollback if insert failed
                    return false;
                }
            }

            // Step 3: Update the book's available copies
            try (PreparedStatement updateStmt = connection.prepareStatement(updateBookSQL)) {
                updateStmt.setInt(1, transaction.getBookId());

                int rowsUpdated = updateStmt.executeUpdate();
                if (rowsUpdated == 0) {
                    connection.rollback(); // Rollback if update failed
                    return false;
                }
            }

            connection.commit(); // All steps successful, commit the transaction
            return true;

        } catch (SQLException e) {
            // Log the error and rethrow as a RuntimeException
            System.err.println("Error while adding transaction for user ID " + transaction.getUserId() +
                    " and book ID " + transaction.getBookId() + ": " + e.getMessage());
            throw new RuntimeException("Failed to add transaction or update book availability for user ID " +
                    transaction.getUserId() + " and book ID " + transaction.getBookId(), e);        }
    }

    //-----------------------------------------------------------------------
    /**
     * Handles the return of a borrowed book based on a transaction ID.
     * <p>
     * This method performs the following steps as a single database transaction:
     * <ol>
     *     <li>Checks whether the transaction exists and whether the book is currently marked as borrowed.</li>
     *     <li>Updates the transaction to set the return date and change the status to 'returned'.</li>
     *     <li>Fetches the associated book ID for the transaction.</li>
     *     <li>Updates the book record to increment the available copies by 1.</li>
     * </ol>
     * If any of the steps fail, the entire operation is rolled back to maintain data consistency.
     *
     * @param transactionId the ID of the transaction to update
     * @param returnDate the date on which the book is returned
     * @return true if the return was processed successfully; false if the transaction was invalid or already returned
     * @throws RuntimeException if a database error occurs during the return process
     *
     * @see com.library.management.model.Status
     */
    public boolean returnBook(int transactionId, LocalDate returnDate) {
        // SQL to check if the transaction exists and get its current status
        String checkStatusSQL = "SELECT status FROM transactions WHERE transaction_id = ?";

        // SQL to update the return date and change status to 'returned'
        String returnSQL = "UPDATE transactions SET return_date = ?, status = ? WHERE transaction_id = ?";

        // SQL to get the book ID associated with the transaction
        String getBookIdSQL = "SELECT book_id FROM transactions WHERE transaction_id = ?";

        // SQL to increment the number of available copies of the book by 1
        String updateBookSQL = "UPDATE books SET number_of_available_copies = number_of_available_copies + 1 " +
                "WHERE book_id = ?";

        try (Connection connection = DatabaseConnection.getConnection()) {

            connection.setAutoCommit(false); // Begin transaction manually

            // Step 1: Check if the transaction exists and whether it has status 'borrowed'
            try (PreparedStatement checkStatusStmt = connection.prepareStatement(checkStatusSQL)) {
                checkStatusStmt.setInt(1, transactionId);

                try (ResultSet resultSet = checkStatusStmt.executeQuery()) {
                    if (resultSet.next()) {
                        Status status = Status.valueOf(resultSet.getString("status").toUpperCase());

                        if(!status.equals(Status.BORROWED)) {
                            return false; // // Book is not currently borrowed
                        }
                    } else {
                        return false; // Transaction ID does not exist
                    }
                }
            }

            // Step 2: Update the transaction record with return date and new status
            try (PreparedStatement returnStmt = connection.prepareStatement(returnSQL)) {
                returnStmt.setDate(1, Date.valueOf(returnDate)); // Set return date
                returnStmt.setString(2, Status.RETURNED.toString().toLowerCase()); // Update status
                returnStmt.setInt(3, transactionId);

                int rowsUpdated = returnStmt.executeUpdate();
                if(rowsUpdated == 0) {
                    connection.rollback(); // Rollback if update failed
                    return false;
                }
            }

            // Step 3: Get the book ID associated with the transaction
            int book_id;
            try (PreparedStatement bookIdStmt = connection.prepareStatement(getBookIdSQL)) {
                bookIdStmt.setInt(1, transactionId);

                try (ResultSet resultSet = bookIdStmt.executeQuery()) {
                    if(resultSet.next()) {
                        book_id = resultSet.getInt("book_id");

                    } else {
                        connection.rollback();
                        return false; // No book found for the transaction
                    }
                }
            }

            // Step 4: Update the books table to increment available copies
            try (PreparedStatement updateBookStmt = connection.prepareStatement(updateBookSQL)) {
                updateBookStmt.setInt(1, book_id);

                int rowsUpdated = updateBookStmt.executeUpdate();

                if(rowsUpdated == 0) {
                    connection.rollback(); // Rollback if update failed
                    return false;
                }
            }

            connection.commit(); // All steps succeeded — commit transaction
            return true;

        } catch (SQLException e) {
            // Log the error and rethrow as a RuntimeException
            System.err.println("Error while returning book for transaction ID " + transactionId + ": " + e.getMessage());
            throw new RuntimeException("Failed to return book or update book availability for transaction ID " +
                    transactionId + ": " + e.getMessage(), e);        }
    }

    //-----------------------------------------------------------------------
    /**
     * Retrieves a transaction from the database based on the given transaction ID.
     * <p>
     * This method fetches all relevant details of a transaction, including user ID, book ID,
     * borrow date, return date (if available), and status. It maps the result to a
     * {@link com.library.management.model.Transaction} object.
     *
     * @param transactionId the ID of the transaction to retrieve
     * @return a Transaction object if found; otherwise, null
     * @throws RuntimeException if any SQL error occurs during retrieval
     *
     * @see com.library.management.model.Transaction
     * @see com.library.management.model.Status
     */
    public Transaction getTransactionById(int transactionId) {
        // SQL to select all columns from the transactions table for the given transaction ID
        String sqlStatement = "SELECT * FROM transactions WHERE transaction_id = ?";

        // Open database connection and prepare statement (auto-closed by try-with-resources)
        try (Connection connection = DatabaseConnection.getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(sqlStatement)) {

            // Set the transaction ID in the SQL query
            preparedStatement.setInt(1, transactionId);

            // Execute the query
            try (ResultSet resultSet = preparedStatement.executeQuery()) {

                // If a matching record is found, map it to a Transaction object
                if (resultSet.next()) {
                    // Retrieve the return date, which can be null
                    Date sqlReturnDate = resultSet.getDate("return_date");
                    LocalDate returnDate = (sqlReturnDate != null) ? sqlReturnDate.toLocalDate() : null;

                    // Return a fully populated Transaction object
                    return new Transaction(
                            resultSet.getInt("transaction_id"),
                            resultSet.getInt("user_id"),
                            resultSet.getInt("book_id"),
                            resultSet.getDate("borrow_date").toLocalDate(),
                            returnDate,
                            Status.valueOf(resultSet.getString("status").toUpperCase()) // Convert string to enum
                    );
                }
            }

        } catch (SQLException e) {
            // Log and rethrow as an unchecked exception for upstream handling
            System.err.println("Error retrieving transaction with ID " + transactionId + ": " + e.getMessage());
            throw new RuntimeException("Failed to retrieve transaction with ID " + transactionId + ": " + e.getMessage(), e);        }

        // No matching transaction found
        return null;
    }

    //-----------------------------------------------------------------------
    /**
     * Retrieves all transactions from the database.
     * <p>
     * This method fetches all rows from the 'transactions' table and maps each row to a
     * {@link com.library.management.model.Transaction} object. If the return date is null,
     * it is handled gracefully. The status is converted from string to the {@link com.library.management.model.Status} enum.
     *
     * @return a list of all transactions; empty list if no transactions exist
     * @throws RuntimeException if any SQL error occurs during retrieval
     *
     * @see com.library.management.model.Transaction
     * @see com.library.management.model.Status
     */
    public List<Transaction> getAllTransactions() {
        // Initialize an empty list to store all retrieved transactions
        List<Transaction> transactions = new ArrayList<>();

        // SQL query to select all transaction records
        String sqlStatement = "SELECT * FROM transactions";

        // Establish connection, prepare statement, and execute query (auto-closed by try-with-resources)
        try (Connection connection = DatabaseConnection.getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(sqlStatement);
            ResultSet resultSet = preparedStatement.executeQuery()) {

            // Loop through each result row
            while (resultSet.next()) {
                // Handle nullable return date
                Date sqlReturnDate = resultSet.getDate("return_date");
                LocalDate returnDate = (sqlReturnDate != null) ? sqlReturnDate.toLocalDate() : null;

                // Map row to a Transaction object and add to the list
                transactions.add(
                        new Transaction(
                                resultSet.getInt("transaction_id"),
                                resultSet.getInt("user_id"),
                                resultSet.getInt("book_id"),
                                resultSet.getDate("borrow_date").toLocalDate(),
                                returnDate,
                                Status.valueOf(resultSet.getString("status").toUpperCase())
                        )
                );
            }

        } catch (SQLException e) {
            // Log and rethrow in case of failure
            System.err.println("Error retrieving all transactions: " + e.getMessage());
            throw new RuntimeException("Failed to retrieve all transactions: " + e.getMessage(), e);        }

        // Return the complete list of transactions (could be empty)
        return transactions;
    }

    //-----------------------------------------------------------------------
    /**
     * Retrieves all active (currently borrowed) transactions from the database.
     * <p>
     * This method filters transactions where the status is 'borrowed', indicating
     * that the book has not yet been returned. Each row is mapped to a
     * {@link com.library.management.model.Transaction} object with careful handling of nullable return dates.
     *
     * @return a list of all active (borrowed) transactions; empty list if none are found
     * @throws RuntimeException if any SQL error occurs during the retrieval process
     *
     * @see com.library.management.model.Transaction
     * @see com.library.management.model.Status
     */
    public List<Transaction> getActiveTransactions() {
        // Create a list to store active transactions
        List<Transaction> transactions = new ArrayList<>();

        // SQL query to fetch only those transactions where status is 'borrowed'
        String sqlStatement = "SELECT * FROM transactions WHERE status = ?";

        // Establish connection and prepare the SQL statement (auto-closed by try-with-resources)
        try (Connection connection = DatabaseConnection.getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(sqlStatement)) {

            // Set the parameter to 'borrowed' to filter active transactions
            preparedStatement.setString(1, "borrowed");

            // Execute the query and process the result set
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    // Handle nullable return date
                    Date sqlReturnDate = resultSet.getDate("return_date");
                    LocalDate returnDate = (sqlReturnDate != null) ? sqlReturnDate.toLocalDate() : null;

                    // Map row data to a Transaction object and add it to the list
                    transactions.add(
                            new Transaction(
                                    resultSet.getInt("transaction_id"),
                                    resultSet.getInt("user_id"),
                                    resultSet.getInt("book_id"),
                                    resultSet.getDate("borrow_date").toLocalDate(),
                                    returnDate,
                                    Status.valueOf(resultSet.getString("status").toUpperCase())
                            )
                    );
                }
            }

        } catch (SQLException e) {
            // Log the error and rethrow as a RuntimeException for further handling
            System.err.println("Error retrieving active (borrowed) transactions: " + e.getMessage());
            throw new RuntimeException("Failed to retrieve active transactions: " + e.getMessage(), e);
        }

        // Return the list of active transactions (could be empty)
        return transactions;
    }

    //-----------------------------------------------------------------------
    /**
     * Retrieves all transactions associated with a specific user ID.
     * <p>
     * This method queries the 'transactions' table for all records where the user ID matches
     * the provided parameter. Each result is mapped to a {@link com.library.management.model.Transaction}
     * object with proper handling of nullable return dates and conversion of status to enum.
     *
     * @param userId the ID of the user whose transactions are to be fetched
     * @return a list of transactions made by the specified user; an empty list if none exist
     * @throws RuntimeException if any SQL error occurs during the retrieval process
     *
     * @see com.library.management.model.Transaction
     * @see com.library.management.model.Status
     */
    public List<Transaction> getTransactionsByUserId(int userId) {
        // List to store all transactions for the given user
        List<Transaction> transactions = new ArrayList<>();

        // SQL to retrieve all transactions where user_id matches the input
        String sqlStatement = "SELECT * FROM transactions WHERE user_id = ?";

        // Establish database connection and prepare the query
        try(Connection connection = DatabaseConnection.getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(sqlStatement)) {

            // Bind the user ID to the query parameter
            preparedStatement.setInt(1, userId);

            // Execute the query and process results
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    // Safely convert SQL return_date to LocalDate (null-safe)
                    Date sqlReturnDate = resultSet.getDate("return_date");
                    LocalDate returnDate = (sqlReturnDate != null) ? sqlReturnDate.toLocalDate() : null;

                    // Map each row to a Transaction object and add it to the list
                    transactions.add(
                            new Transaction(
                                    resultSet.getInt("transaction_id"),
                                    resultSet.getInt("user_id"),
                                    resultSet.getInt("book_id"),
                                    resultSet.getDate("borrow_date").toLocalDate(),
                                    returnDate,
                                    Status.valueOf(resultSet.getString("status").toUpperCase())
                            )
                    );
                }
            }

        } catch (SQLException e) {
            // Log the error and rethrow for upstream handling
            System.err.println("Error retrieving transactions for user ID " + userId + ": " + e.getMessage());
            throw new RuntimeException("Failed to retrieve transactions for user ID " + userId + ": " + e.getMessage(), e);        }

        // Return the list of user's transactions (could be empty)
        return transactions;
    }

    //-----------------------------------------------------------------------
    /**
     * Retrieves the borrowing history for a specific user by performing a join between
     * the 'transactions' and 'books' tables.
     * <p>
     * This method returns a list of {@link com.library.management.model.BorrowingHistory}
     * objects, each containing transaction details along with the corresponding book title.
     * The return date is handled safely in case it is null (i.e., book not yet returned).
     *
     * @param userId the ID of the user whose borrowing history is to be retrieved
     * @return a list of BorrowingHistory records for the user; empty if no history exists
     * @throws RuntimeException if any SQL error occurs during the process
     *
     * @see com.library.management.model.BorrowingHistory
     * @see com.library.management.model.Status
     */
    public List<BorrowingHistory> getBorrowingHistoryByUserId(int userId) {
        // List to store borrowing history records
        List<BorrowingHistory> borrowingHistoryList = new ArrayList<>();

        // SQL query joins transactions and books to include book titles in history
        String sqlStatement = "SELECT t.transaction_id, t.user_id, t.book_id, " +
                "b.title, t.borrow_date, t.return_date, t.status " +
                "FROM transactions t " +
                "INNER JOIN books b " +
                "ON t.book_id = b.book_id " +
                "WHERE user_id = ?";

        // Open database connection and prepare SQL statement (auto-closed by try-with-resources)
        try (Connection connection = DatabaseConnection.getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(sqlStatement)) {

            // Set the user ID in the query
            preparedStatement.setInt(1, userId);

            // Execute the query and process the results
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    // Convert SQL return date to LocalDate, null-safe
                    Date sqlReturnDate = resultSet.getDate("return_date");
                    LocalDate returnDate = (sqlReturnDate != null) ? sqlReturnDate.toLocalDate() : null;

                    // Map the result row to a BorrowingHistory object and add to the list
                    borrowingHistoryList.add(
                            new BorrowingHistory(
                                    resultSet.getInt("transaction_id"),
                                    resultSet.getInt("user_id"),
                                    resultSet.getInt("book_id"),
                                    resultSet.getString("title"),
                                    resultSet.getDate("borrow_date").toLocalDate(),
                                    returnDate,
                                    Status.valueOf(resultSet.getString("status").toUpperCase())
                            )
                    );
                }
            }

        } catch (SQLException e) {
            // Log and rethrow exception with contextual information
            System.err.println("Error retrieving borrowing history for user ID " + userId + ": " + e.getMessage());
            throw new RuntimeException("Failed to retrieve borrowing history for user ID " + userId + ": " + e.getMessage(), e);        }

        // Return the full borrowing history list (could be empty)
        return borrowingHistoryList;
    }

    //-----------------------------------------------------------------------
    /**
     * Retrieves the transaction ID for a specific combination of user ID and book ID.
     * <p>
     * This method is typically used to locate an existing transaction where a user has borrowed a book.
     * It assumes that a user can borrow the same book only once at a time.
     *
     * @param userId the ID of the user
     * @param bookId the ID of the book
     * @return the transaction ID if found; -1 if no matching transaction exists
     * @throws RuntimeException if a SQL error occurs during the process
     */
    public int getTransactionId(int userId, int bookId) {
        // SQL to fetch the transaction_id based on both user_id and book_id
        String sqlStatement = "SELECT transaction_id FROM transactions " +
                "WHERE user_id = ? AND book_id = ?";

        // Open connection and prepare the SQL statement (closed automatically by try-with-resources)
        try (Connection connection = DatabaseConnection.getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(sqlStatement)) {

            // Set user ID and book ID as parameters in the query
            preparedStatement.setInt(1, userId);
            preparedStatement.setInt(2, bookId);

            // Execute the query and process the result
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    // Return the transaction ID if a matching record is found
                    return resultSet.getInt("transaction_id");
                }
            }

        } catch (SQLException e) {
            // Log and rethrow the exception with context
            System.err.println("Error retrieving transaction ID for user ID " + userId + " and book ID " + bookId + ": " + e.getMessage());
            throw new RuntimeException("Failed to retrieve transaction ID for user ID " + userId + " and book ID " + bookId + ": " + e.getMessage(), e);        }

        // Return -1 if no matching transaction was found
        return -1;
    }

    //-----------------------------------------------------------------------
    /**
     * Retrieves the transaction ID for a book that is currently borrowed (i.e., not yet returned)
     * by a specific user.
     * <p>
     * This method ensures that only the active borrowing record is fetched, by filtering on
     * {@code status = 'borrowed'} in the SQL query. This is crucial because a user may have
     * borrowed and returned the same book multiple times, and we only want to return the transaction
     * that is still open (borrowed but not yet returned).
     * <p>
     * It is primarily used in the {@code TransactionService}, and eventually in the
     * {@code BookOperations.returnBook()} method, where the application must locate the
     * correct borrowing record to process the return.
     *
     * @param userId the ID of the user who borrowed the book
     * @param bookId the ID of the borrowed book
     * @return the transaction ID of the active borrowed record, or -1 if no such record exists
     * @throws RuntimeException if a SQL error occurs during execution
     */
    public int getTransactionIdForBorrowedBook(int userId, int bookId) {
        // SQL to fetch the transaction where book is currently borrowed by the user
        String sql = "SELECT transaction_id FROM transactions " +
                "WHERE user_id = ? AND book_id = ? AND status = 'borrowed'";

        int transactionId = -1; // Default return value if no active transaction is found

        // Try-with-resources block for connection and statement management
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            // Set the userId and bookId in the query
            preparedStatement.setInt(1, userId);
            preparedStatement.setInt(2, bookId);

            // Execute the query
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                // If a matching 'borrowed' transaction exists, retrieve its ID
                if (resultSet.next()) {
                    transactionId = resultSet.getInt("transaction_id");
                }
            }

        } catch (SQLException e) {
            // Log the error and rethrow with contextual details
            System.err.println("Error retrieving borrowed transaction ID for user ID " + userId +
                    " and book ID " + bookId + ": " + e.getMessage());
            throw new RuntimeException("Failed to retrieve transaction ID for borrowed book (user ID: " +
                    userId + ", book ID: " + bookId + "): " + e.getMessage(), e);        }

        // Return the fetched transaction ID, -1 if not founud
        return transactionId;
    }

    //-----------------------------------------------------------------------
    /**
     * Deletes all transaction records associated with a specific user from the database.
     * <p>
     * This method is useful for cleanup operations when a user account is removed,
     * or for resetting a user's borrowing history. It executes a single SQL DELETE statement
     * that removes all rows in the 'transactions' table matching the given user ID.
     *
     * @param userId the ID of the user whose transactions should be deleted
     * @return {@code true} if one or more transactions were deleted; {@code false} if no matching records existed
     * @throws RuntimeException if a SQL error occurs during the deletion process
     */
    public boolean deleteUserTransactions(int userId) {
        // SQL DELETE statement to remove all transactions by the given user ID
        String sqlStatement = "DELETE FROM transactions WHERE user_id = ?";

        // Establish database connection and prepare the SQL statement
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sqlStatement)) {

            // Set the user ID as the parameter in the DELETE statement
            preparedStatement.setInt(1, userId);

            // Execute the DELETE operation; return true if at least one row was deleted
            return preparedStatement.executeUpdate() > 0;

        } catch (SQLException e) {
            // Log the exception and rethrow a runtime exception with context
            System.err.println("Error deleting transactions for user ID " + userId + ": " + e.getMessage());
            throw new RuntimeException("Failed to delete transactions for user ID " + userId + ": " + e.getMessage(), e);        }
    }

}
