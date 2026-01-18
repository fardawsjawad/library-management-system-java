package com.library.management.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Utility class for managing database connections in the Library Management System.
 * <p>
 * This class provides a single method to establish and retrieve a connection
 * to the MySQL database using JDBC. It centralizes the connection configuration,
 * making it easier to manage and reuse throughout the application.
 * </p>
 *
 * <p>Example usage:</p>
 * <pre>{@code
 * try (Connection connection = DatabaseConnection.getConnection()) {
 *     // Use the connection
 * } catch (SQLException e) {
 *     e.printStackTrace();
 * }
 * }</pre>
 *
 * @author Fardaws Jawad
 */
public class DatabaseConnection {

    /** The JDBC URL for the MySQL database */
    private static String URL = "jdbc:mysql://localhost:3306/LibraryManagementSystem_db";

    /** The database username */
    private static String USER = "root";

    /** The database password (should be secured in production) */
    private static String PASSWORD = "@R@$#420?";

    /**
     * Establishes and returns a connection to the Library Management System database.
     *
     * @return a {@link Connection} object to interact with the database
     * @throws SQLException if a database access error occurs or the connection cannot be established
     */
    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }

}
