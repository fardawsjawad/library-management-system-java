package com.library.management.dao;

import com.library.management.model.Address;
import com.library.management.util.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Data Access Object (DAO) class for managing address-related operations in the 'addresses' table.
 * <p>
 * This class provides methods to perform standard CRUD (Create, Read, Update, Delete) operations
 * on the Address entity, abstracting the SQL logic and database access from higher-level components.
 * It follows the DAO design pattern, ensuring separation of persistence logic from business logic.
 * <p>
 * Common use cases include:
 * <ul>
 *     <li>Inserting a new address when a user registers</li>
 *     <li>Fetching the address for a given user ID</li>
 *     <li>Updating a user's address details</li>
 *     <li>Deleting an address record</li>
 * </ul>
 * <p>
 * This class typically works closely with {@code UserDAO}, especially when dealing with user registration
 * or profile management tasks that involve both user and address data.
 * <p>
 * All SQL operations are performed using parameterized queries (PreparedStatement) to avoid SQL injection,
 * and connections are managed using try-with-resources to ensure proper resource cleanup.
 *
 * @author Fardaws Jawad
 * @see com.library.management.model.Address
 * @see com.library.management.dao.UserDAO
 */
public class AddressDAO {

    //-----------------------------------------------------------------------
    /**
     * Adds an address for a newly created user using the same database connection.
     * This method is typically called from within the UserDAO after the user is inserted.
     * @param address Address object with all required fields
     * @param userId the ID of the user to associate the address with
     * @param connection active database connection used to execute the SQL statement
     * @return true if the address is added successfully, false otherwise
     * @throws RuntimeException if a database access error occurs
     */
    public boolean addAddress(Address address, int userId, Connection connection) {

        try {
            return executeAddressInsert(address, userId, connection);
        } catch (SQLException e) {
            // Log the error and rethrow as a RuntimeException
            System.err.println("Error while adding address for user ID " + userId + ": " + e.getMessage());
            throw new RuntimeException("Failed to add address for user ID " + userId + ": " + e.getMessage(), e);
        }
    }

    /**
     * Adds a new address for a user by establishing a new database connection.
     * This method is typically used when the connection does not need to be shared.
     * @param address Address object with all required fields
     * @param userId the ID of the user to associate the address with
     * @return true if the address is added, false otherwise
     * @throws RuntimeException if a database access error occurs
     */
    public boolean addAddress(Address address, int userId) {

        // Try-with-resources to automatically close the connection and prepared statement
        try(Connection connection = DatabaseConnection.getConnection()) {

            return executeAddressInsert(address, userId, connection);

        } catch (SQLException e) {
            // Log the error and rethrow as a RuntimeException
            System.err.println("Error while adding address for user ID " + userId + ": " + e.getMessage());
            throw new RuntimeException("Failed to add address for user ID " + userId + ": " + e.getMessage(), e);
        }
    }

    //-----------------------------------------------------------------------
    /**
     * Retrieves an address from the database using the given address ID.
     * This method establishes a new connection, queries the 'addresses' table,
     * and returns the corresponding Address object if found.
     *
     * @param addressId the unique ID of the address to retrieve
     * @return Address object if found, or null if no matching address exists
     * @throws RuntimeException if a database access error occurs
     */
    public Address getAddressById(int addressId) {
        // SQL query to retrieve address based on address ID
        String sqlStatement = "SELECT * FROM addresses " +
                "WHERE address_id = ?";

        // Try-with-resources to automatically close the connection and prepared statement
        try (Connection connection = DatabaseConnection.getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(sqlStatement)) {

            // Set the address ID in the query
            preparedStatement.setInt(1, addressId);

            // Execute the query and retrieve the result
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if(resultSet.next()) {
                    // Create and return Address object from result set
                    return new Address(
                            resultSet.getInt("address_id"),
                            resultSet.getString("street"),
                            resultSet.getString("city"),
                            resultSet.getString("pincode"),
                            resultSet.getString("state"),
                            resultSet.getString("country")
                    );
                }
            }

        } catch (SQLException e) {
            // Log the error and rethrow as a RuntimeException
            System.err.println("Error while retrieving address with ID " + addressId + ": " + e.getMessage());
            throw new RuntimeException("Failed to retrieve address with ID " + addressId + ": " + e.getMessage(), e);
        }

        // Return null if no matching address is found
        return null;
    }

    //-----------------------------------------------------------------------
    /**
     * Retrieves the address associated with a specific user ID from the database.
     * This method establishes a new connection, queries the 'addresses' table using the user ID,
     * and returns the corresponding Address object if found.
     *
     * Note: If a user has multiple addresses, only the first matched address is returned.
     *
     * @param userId the ID of the user whose address is to be retrieved
     * @return Address object if found, or null if no matching address exists
     * @throws RuntimeException if a database access error occurs
     */
    public Address getAddressByUserId(int userId) {
        // SQL query to fetch address by user ID
        String sqlStatement = "SELECT * FROM addresses WHERE user_id = ?";

        // Try-with-resources to ensure connection and statement are closed automatically
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sqlStatement)) {

            // Set the user ID parameter in the query
            preparedStatement.setInt(1, userId);

            // Execute the query
            try (ResultSet resultSet = preparedStatement.executeQuery()) {

                // If a matching address is found, construct and return the Address object
                if (resultSet.next()) {
                    return new Address(
                            resultSet.getInt("address_id"),
                            resultSet.getString("street"),
                            resultSet.getString("city"),
                            resultSet.getString("pincode"),
                            resultSet.getString("state"),
                            resultSet.getString("country")
                    );
                }
            }

        } catch (SQLException e) {
            // Log and rethrow the exception as a RuntimeException for higher-level handling
            System.err.println("Error while retrieving address for user ID " + userId + ": " + e.getMessage());
            throw new RuntimeException("Failed to retrieve address for user ID " + userId + ": " + e.getMessage(), e);
        }

        // Return null if no address is found for the given user ID
        return null;
    }

    //-----------------------------------------------------------------------
    /**
     * Updates the address of a user in the database using a shared database connection.
     * <p>
     * This method is primarily used from the UserDAO when updating a user's information,
     * allowing both user and address updates to be handled within the same database connection (e.g., inside a transaction).
     *
     * @param address    the Address object containing updated address fields
     * @param userId     the ID of the user whose address is to be updated
     * @param connection the existing database connection used to execute the update
     * @return true if the address was updated successfully; false otherwise
     * @throws RuntimeException if a database access error occurs
     */
    public boolean updateAddress(Address address, int userId, Connection connection) {
        // SQL statement to update address details for a specific user
        String sqlStatement = "UPDATE addresses SET street = ?, city = ?, " +
                "pincode = ?, state = ?, country = ? WHERE user_id = ?";

        // Try-with-resources to ensure connection and statement are closed automatically
        try (PreparedStatement preparedStatement = connection.prepareStatement(sqlStatement)) {
            // Set the updated address fields in the prepared statement
            preparedStatement.setString(1, address.getStreet());
            preparedStatement.setString(2, address.getCity());
            preparedStatement.setString(3, address.getPinCode());
            preparedStatement.setString(4, address.getState());
            preparedStatement.setString(5, address.getCountry());
            preparedStatement.setInt(6, userId);

            // Execute update and return true if at least one row was affected
            return preparedStatement.executeUpdate() > 0;

        } catch (SQLException e) {
            // Log the error and rethrow as a RuntimeException
            System.err.println("Error while updating address for user ID " + userId + ": " + e.getMessage());
            throw new RuntimeException("Failed to update address for user ID " + userId + ": " + e.getMessage(), e);
        }
    }

    //-----------------------------------------------------------------------
    /**
     * Updates an existing address in the database for the specified user ID by creating a new connection.
     * This method is typically used when there's no existing connection to reuse.
     *
     * @param address the Address object containing updated address fields
     * @param userId  the ID of the user whose address needs to be updated
     * @return true if the address was updated successfully; false otherwise
     * @throws RuntimeException if a database access error occurs
     */
    public boolean updateAddress(Address address, int userId) {
        // SQL statement to update address details for a specific user
        String sqlStatement = "UPDATE addresses SET street = ?, city = ?, " +
                "pincode = ?, state = ?, country = ? WHERE user_id = ?";

        // Establish a new connection and execute the update
        // Try-with-resources to ensure connection and statement are closed automatically
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sqlStatement)) {

            // Set the updated address fields in the prepared statement
            preparedStatement.setString(1, address.getStreet());
            preparedStatement.setString(2, address.getCity());
            preparedStatement.setString(3, address.getPinCode());
            preparedStatement.setString(4, address.getState());
            preparedStatement.setString(5, address.getCountry());
            preparedStatement.setInt(6, userId);

            // Execute update and return true if at least one row was affected
            return preparedStatement.executeUpdate() > 0;

        } catch (SQLException e) {
            // Log the error and rethrow as a RuntimeException
            System.err.println("Error while updating address for user ID " + userId + ": " + e.getMessage());
            throw new RuntimeException("Failed to update address for user ID " + userId + ": " + e.getMessage(), e);
        }
    }

    //-----------------------------------------------------------------------
    /**
     * Deletes an address from the database based on the given address ID.
     * <p>
     * This method establishes a new connection, prepares a DELETE statement,
     * and removes the address record from the 'addresses' table.
     *
     * @param addressId the unique ID of the address to be deleted
     * @return true if the address was successfully deleted; false if no matching record was found
     * @throws RuntimeException if a database access error occurs
     */
    public boolean deleteAddress(int addressId) {
        // SQL query to delete an address by its ID
        String sqlStatement = "DELETE FROM addresses WHERE address_id = ?";

        // Try-with-resources to ensure connection and prepared statement are closed automatically
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sqlStatement)) {

            // Set the address ID parameter in the query
            preparedStatement.setInt(1, addressId);

            // Execute the delete operation; return true if a row was affected
            return preparedStatement.executeUpdate() > 0;

        } catch (SQLException e) {
            // Log the error and rethrow as a RuntimeException
            System.err.println("Error while deleting address with ID " + addressId + ": " + e.getMessage());
            throw new RuntimeException("Failed to delete address with ID " + addressId + ": " + e.getMessage(), e);
        }
    }

    //-----------------------------------------------------------------------
    /**
     * Deletes the address associated with the specified user ID from the database.
     * <p>
     * This method is typically used when deleting a user to ensure
     * that the corresponding address record is also removed.
     *
     * @param userId the ID of the user whose address should be deleted
     * @return true if the address was successfully deleted; false if no matching record was found
     * @throws RuntimeException if a database access error occurs
     */
    public boolean deleteAddressByUserId(int userId) {
        // SQL query to delete address where user_id matches
        String sqlStatement = "DELETE FROM addresses WHERE user_id = ?";

        // Try-with-resources to ensure connection and prepared statement are closed automatically
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sqlStatement)) {

            // Set userId parameter in the query
            preparedStatement.setInt(1, userId);

            // Execute the delete and return true if any row was affected
            return preparedStatement.executeUpdate() > 0;

        } catch (SQLException e) {
            // Log error and rethrow as unchecked exception for higher-level handling
            System.err.println("Error while deleting address for user ID " + userId + ": " + e.getMessage());
            throw new RuntimeException("Failed to delete address for user ID " + userId + ": " + e.getMessage(), e);
        }
    }

    //-----------------------------------------------------------------------
    /**
     * Helper method to execute the SQL insert for an address using the given connection.
     *
     * @param address Address object with all required fields
     * @param userId the ID of the user to associate the address with
     * @param connection active database connection used to execute the SQL statement
     * @return true if the address was added successfully, false otherwise
     * @throws SQLException if a database error occurs (propagated to calling method)
     */
    private boolean executeAddressInsert(Address address, int userId, Connection connection) throws SQLException {
        // SQL query to insert the provided address
        String sqlStatement = "INSERT INTO addresses (user_id, street, city, pincode, state, country) " +
                "VALUES (?, ?, ?, ?, ?, ?)";

        // Try-with-resources to automatically close the prepared statement
        try (PreparedStatement preparedStatement = connection.prepareStatement(sqlStatement)) {
            preparedStatement.setInt(1, userId);
            preparedStatement.setString(2, address.getStreet());
            preparedStatement.setString(3, address.getCity());
            preparedStatement.setString(4, address.getPinCode());
            preparedStatement.setString(5, address.getState());
            preparedStatement.setString(6, address.getCountry());

            // Return true if the record is added. The database returns at least one row affected
            return preparedStatement.executeUpdate() > 0;
        }
    }

}
