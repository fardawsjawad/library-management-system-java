package com.library.management.dao;

import com.library.management.model.*;
import com.library.management.util.DatabaseConnection;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Data Access Object (DAO) class for handling all user-related database operations.
 * <p>
 * This class encapsulates the logic for interacting with the {@code users} and {@code addresses} tables
 * in the database. It supports full CRUD operations (Create, Read, Update, Delete) for different types
 * of users in the system, including {@link Member} and {@link Administrator}, while managing associated
 * address data through the {@link AddressDAO}.
 * <p>
 * Features include:
 * <ul>
 *   <li>Fetching all users, administrators, or members with address data</li>
 *   <li>Inserting new users with transactional address handling</li>
 *   <li>Updating user fields, including password, username, user type, and admin type</li>
 *   <li>Dynamically updating individual member fields</li>
 *   <li>Secure retrieval of user data for authentication (with password)</li>
 *   <li>Deleting users and their related transaction data</li>
 * </ul>
 * <p>
 * This class strictly separates authentication-related access by providing a dedicated method that includes
 * the password field, while other retrieval methods exclude sensitive data by design.
 *
 * @author Fardaws Jawad
 * @see com.library.management.model.User
 * @see com.library.management.model.Member
 * @see com.library.management.model.Administrator
 * @see com.library.management.model.Address
 * @see com.library.management.dao.AddressDAO
 */
public class UserDAO {

    /**
     * DAO for handling address-related database operations.
     * <p>
     * Used by UserDAO to manage the address of a user when creating or retrieving user details.
     */
    private final AddressDAO addressDAO;

    //-----------------------------------------------------------------------
    /**
     * Constructs a UserDAO and initializes the associated AddressDAO.
     * <p>
     * This constructor sets up the dependency required for handling user-address relationships.
     */
    public UserDAO() {
        this.addressDAO = new AddressDAO();
    }

    //-----------------------------------------------------------------------
    /**
     * Retrieves all users from the database, including their associated address details.
     * <p>
     * This method performs a LEFT OUTER JOIN between the 'users' and 'addresses' tables to
     * ensure users are fetched even if they do not have an address. It then constructs appropriate
     * user objects (`Administrator` or `Member`) based on their user type and populates them
     * with the retrieved data.
     *
     * @return a list of all users in the database along with their address details (if available)
     * @throws RuntimeException if any SQL error occurs during data retrieval
     */
    public List<User> getAllUsers() {
        List<User> userList = new ArrayList<>();

        // SQL query joins users and addresses; LEFT JOIN ensures users with no address are still included
        String sqlStatement = "SELECT * FROM users u " +
                "LEFT OUTER JOIN addresses a " +
                "ON u.user_id = a.user_id";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sqlStatement);
             ResultSet resultSet = preparedStatement.executeQuery()) {

            while (resultSet.next()) {
                Address address = null;

                // Check if address exists before constructing Address object
                String street = resultSet.getString("street");
                if(street != null) {
                    address = new Address(
                            resultSet.getString("street"),
                            resultSet.getString("city"),
                            resultSet.getString("pincode"),
                            resultSet.getString("state"),
                            resultSet.getString("country")
                    );
                }

                // Determine user type and construct the appropriate User object (Administrator or Member)
                User_Type userType = User_Type.valueOf(resultSet.getString("user_type").toUpperCase());

                if(userType.equals(User_Type.ADMIN)) {
                    // Construct Administrator object and add to userList
                    userList.add(
                                new Administrator(
                                    resultSet.getInt("user_id"),
                                    resultSet.getString("username"),
                                    User_Type.valueOf(resultSet.getString("user_type").toUpperCase()),
                                    resultSet.getString("firstname"),
                                    resultSet.getString("surname"),
                                    resultSet.getDate("date_of_birth").toLocalDate(),
                                    Gender.valueOf(resultSet.getString("gender").toUpperCase()),
                                    resultSet.getString("email"),
                                    resultSet.getString("phone_number"),
                                    address,
                                    AdminType.valueOf(resultSet.getString("admin_type").toUpperCase())
                            )
                    );
                } else if (userType.equals(User_Type.MEMBER)) {
                    // Construct Member object and add to userList
                    userList.add(
                            new Member(
                                    resultSet.getInt("user_id"),
                                    resultSet.getString("username"),
                                    User_Type.valueOf(resultSet.getString("user_type").toUpperCase()),
                                    resultSet.getString("firstname"),
                                    resultSet.getString("surname"),
                                    resultSet.getDate("date_of_birth").toLocalDate(),
                                    Gender.valueOf(resultSet.getString("gender").toUpperCase()),
                                    resultSet.getString("email"),
                                    resultSet.getString("phone_number"),
                                    address
                            )
                    );
                }
            }

        } catch (SQLException e) {
            // Log the exception and rethrow a runtime exception with context
            System.err.println("Error retrieving all users: " + e.getMessage());
            throw new RuntimeException("Failed to retrieve all users: " + e.getMessage(), e);        }

        // Return the list of users retrieved from the database
        return userList;
    }

    //-----------------------------------------------------------------------
    /**
     * Retrieves all administrators from the database along with their associated address details.
     * <p>
     * This method performs a LEFT OUTER JOIN between the 'users' and 'addresses' tables and filters
     * for users with the type 'admin' directly in the SQL query. This ensures only administrators
     * are fetched and included in the result, even if they don't have an address.
     *
     * @return a list of Administrator users from the database, each with address information if available
     * @throws RuntimeException if a database access error occurs
     */
    public List<Administrator> getAllAdmins() {
        List<Administrator> administrators = new ArrayList<>();

        // SQL joins users and addresses, filters only admin users using WHERE clause
        String sqlStatement = "SELECT * FROM users u " +
                "LEFT OUTER JOIN addresses a " +
                "ON u.user_id = a.user_id " +
                "WHERE u.user_type = 'admin'";

        // Establish database connection and prepare SQL statement
        try (Connection connection = DatabaseConnection.getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(sqlStatement);
            ResultSet resultSet = preparedStatement.executeQuery()) {

            // Loop through result set and construct Administrator objects
            while (resultSet.next()) {
                Address address = null;

                // Check if address exists before constructing Address object
                String street = resultSet.getString("street");
                if(street != null) {
                    address = new Address(
                            resultSet.getString("street"),
                            resultSet.getString("city"),
                            resultSet.getString("pincode"),
                            resultSet.getString("state"),
                            resultSet.getString("country")
                    );
                }

                // Construct Administrator object from retrieved data and add to the list
                administrators.add(
                        new Administrator(
                                resultSet.getInt("user_id"),
                                resultSet.getString("username"),
                                User_Type.valueOf(resultSet.getString("user_type").toUpperCase()),
                                resultSet.getString("firstname"),
                                resultSet.getString("surname"),
                                resultSet.getDate("date_of_birth").toLocalDate(),
                                Gender.valueOf(resultSet.getString("gender").toUpperCase()),
                                resultSet.getString("email"),
                                resultSet.getString("phone_number"),
                                address,
                                AdminType.valueOf(resultSet.getString("admin_type").toUpperCase())
                            )
                    );
            }

        } catch (SQLException e) {
            // Log and rethrow database errors
            System.err.println("Error retrieving all administrators: " + e.getMessage());
            throw new RuntimeException("Failed to retrieve administrators: " + e.getMessage(), e);        }

        // Return the list of Administrator objects
        return administrators;
    }

    //-----------------------------------------------------------------------
    /**
     * Retrieves all member users from the database along with their associated address details.
     * <p>
     * This method performs a LEFT OUTER JOIN between the 'users' and 'addresses' tables and filters
     * for users with the type 'member' directly in the SQL query. This ensures that only member users
     * are fetched and included in the result list, even if they do not have a corresponding address.
     *
     * @return a list of Member users from the database, each with address information if available
     * @throws RuntimeException if a database access error occurs
     */
    public List<Member> getAllMembers() {
        List<Member> members = new ArrayList<>();

        // SQL joins users and addresses, and filters to include only member-type users
        String sqlStatement = "SELECT * FROM users u " +
                "LEFT OUTER JOIN addresses a " +
                "ON u.user_id = a.user_id " +
                "WHERE u.user_type = 'member'";

        // Establish a database connection and execute the SQL query
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sqlStatement);
             ResultSet resultSet = preparedStatement.executeQuery()) {

            // Loop through each result and construct Member objects
            while (resultSet.next()) {
                Address address = null;

                // If address exists, create an Address object
                String street = resultSet.getString("street");
                if(street != null) {
                    address = new Address(
                            resultSet.getString("street"),
                            resultSet.getString("city"),
                            resultSet.getString("pincode"),
                            resultSet.getString("state"),
                            resultSet.getString("country")
                    );
                }

                // Construct a Member object with all retrieved details and add to list
                members.add(
                        new Member(
                                resultSet.getInt("user_id"),
                                resultSet.getString("username"),
                                User_Type.valueOf(resultSet.getString("user_type").toUpperCase()),
                                resultSet.getString("firstname"),
                                resultSet.getString("surname"),
                                resultSet.getDate("date_of_birth").toLocalDate(),
                                Gender.valueOf(resultSet.getString("gender").toUpperCase()),
                                resultSet.getString("email"),
                                resultSet.getString("phone_number"),
                                address
                            )
                    );
            }

        } catch (SQLException e) {
            // Log and rethrow database errors
            System.err.println("Error retrieving all members: " + e.getMessage());
            throw new RuntimeException("Failed to retrieve members: " + e.getMessage(), e);        }

        // Return the list of Member objects constructed from the result set
        return members;
    }

    //-----------------------------------------------------------------------
    /**
     * Adds a new user to the database along with their associated address.
     * <p>
     * This method inserts user details into the 'users' table and their corresponding address
     * into the 'addresses' table. The process is handled within a single transaction to ensure
     * consistency — if address insertion fails, the entire transaction is rolled back.
     * <p>
     * Supports both {@code Administrator} and {@code Member} objects. Admin users will have
     * their {@code admin_type} recorded, while for members it is set to {@code NULL}.
     *
     * @param user the User object to be added (Administrator or Member)
     * @return {@code true} if the user and their address were successfully inserted; {@code false} otherwise
     * @throws RuntimeException if a database error occurs during the process
     */
    public boolean addUser(User user) {
        // SQL statement to insert a new user into the 'users' table.
        // The admin_type field will be set only for Administrator users, and NULL for Member users.
        String insetUserSQL = "INSERT INTO users " +
                "(username, password, user_type, admin_type, firstname, surname, date_of_birth, gender, email, phone_number) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection connection = DatabaseConnection.getConnection()) {

            // Disable auto-commit to manage transaction manually
            connection.setAutoCommit(false);

            // Prepare and execute the INSERT statement for the user
            try(PreparedStatement insertUserStmt = connection.prepareStatement(insetUserSQL, Statement.RETURN_GENERATED_KEYS)) {

                // Set common user fields
                insertUserStmt.setString(1, user.getUsername());
                insertUserStmt.setString(2, user.getPassword());
                insertUserStmt.setString(3, user.getUserType().toString().toLowerCase());
                insertUserStmt.setString(5, user.getFirstname());
                insertUserStmt.setString(6, user.getSurname());
                insertUserStmt.setDate(7, Date.valueOf(user.getDate_of_birth()));
                insertUserStmt.setString(8, user.getGender().toString().toLowerCase());
                insertUserStmt.setString(9, user.getEmail());
                insertUserStmt.setString(10, user.getPhoneNumber());

                // Set admin_type based on user type
                if(user instanceof Administrator) {
                    Administrator administrator = (Administrator) user;
                    insertUserStmt.setString(4, administrator.getAdminType().toString().toLowerCase());
                } else if (user instanceof Member) {
                    // Members don't have admin_type — set to NULL
                    insertUserStmt.setNull(4, Types.VARCHAR);
                }

                // Execute the INSERT statement
                int rowsInserted = insertUserStmt.executeUpdate();
                if (rowsInserted == 0) {
                    // Insertion failed, no rows affected
                    return false;
                }

                // Retrieve the generated user_id and set it in the User object
                try (ResultSet resultSet = insertUserStmt.getGeneratedKeys()) {
                    if (resultSet.next()) {
                        user.setUserId(resultSet.getInt(1));
                    } else {
                        // Failed to retrieve generated key
                        return false;
                    }
                }

            }

            // Add the address for the newly created user
            boolean addressAdded = addressDAO.addAddress(user.getAddress(), user.getUserId(), connection);

            if(!addressAdded) {
                // If address insertion fails, rollback the entire transaction
                connection.rollback();
                return false;
            }

            // All operations successful — commit the transaction
            connection.commit();
            return true;

        } catch (SQLException e) {
            // Log the error and rethrow as a runtime exception
            System.err.println("Error adding user (username: " + user.getUsername() + "): " + e.getMessage());
            throw new RuntimeException("Failed to add user to the database: " + e.getMessage(), e);
        }
    }

    //-----------------------------------------------------------------------
    /**
     * Updates an existing user's personal details and associated address in the database.
     * <p>
     * This method updates the user's basic information in the 'users' table and their address
     * in the 'addresses' table using a single database transaction. If either update fails,
     * the entire transaction is rolled back to maintain data consistency.
     *
     * <p>The following fields are updated in the 'users' table:
     * firstname, surname, date_of_birth, gender, email, phone_number.
     *
     * @param user the User object containing updated personal and address information
     * @return {@code true} if both user and address updates are successful; {@code false} otherwise
     * @throws RuntimeException if a database error occurs during the update process
     */
    public boolean updateUser(User user) {
        // SQL statement to update user details (excluding password, username, user type, and admin type)
        String updateUserSQL = "UPDATE users SET firstname = ?, " +
                "surname = ?, date_of_birth = ?, gender = ?, " +
                "email = ?, phone_number = ? " +
                "WHERE user_id = ?";

        // Establish database connection and prepare the update statement
        try (Connection connection = DatabaseConnection.getConnection();
            PreparedStatement updateUserStmt = connection.prepareStatement(updateUserSQL)) {

            // Begin transaction
            connection.setAutoCommit(false);

            // Set parameters for the user update
            updateUserStmt.setString(1, user.getFirstname());
            updateUserStmt.setString(2, user.getSurname());
            updateUserStmt.setDate(3,
                    (user.getDate_of_birth() != null) ? Date.valueOf(user.getDate_of_birth()) : null);
            updateUserStmt.setString(4, user.getGender().toString().toLowerCase());
            updateUserStmt.setString(5, user.getEmail());
            updateUserStmt.setString(6, user.getPhoneNumber());
            updateUserStmt.setInt(7, user.getUserId());

            // Execute user update
            int rowsUpdated = updateUserStmt.executeUpdate();
            if (rowsUpdated == 0) {
                // No user was updated (possibly invalid user ID)
                return false;
            }

            // Proceed to update the address
            if (user.getAddress() != null) {
                boolean addressUpdated = addressDAO.updateAddress(user.getAddress(), user.getUserId(), connection);

                if (!addressUpdated) {
                    // If address update fails, rollback the transaction
                    connection.rollback();
                    return false;
                }
            } else {
                // Address is null — rollback to prevent partial updates
                connection.rollback();
                return false;
            }

            // Commit the transaction if both updates succeeded
            connection.commit();
            return true;

        } catch (SQLException e) {
            // Log the error and rethrow as a RuntimeException
            System.err.println("Error updating user (ID: " + user.getUserId() + "): " + e.getMessage());
            throw new RuntimeException("Failed to update user and address information: " + e.getMessage(), e);
        }
    }

    //-----------------------------------------------------------------------
    /**
     * Updates the username of a specific user in the database.
     * <p>
     * This method updates the 'username' field of a user in the 'users' table using the
     * provided user ID. It returns {@code true} if the update is successful (i.e., at least
     * one row is affected), and {@code false} otherwise.
     *
     * @param userId   the unique ID of the user whose username is to be updated
     * @param username the new username to be set
     * @return {@code true} if the username was successfully updated; {@code false} otherwise
     * @throws RuntimeException if a database access error occurs
     */
    public boolean updateUsername(int userId, String username) {
        // SQL statement to update the username for a specific user ID
        String updateSQL = "UPDATE users SET username = ? " +
                "WHERE user_id = ?";

        // Establish connection, prepare and execute the update statement
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement updateStmt = connection.prepareStatement(updateSQL)) {

            updateStmt.setString(1, username); // Set new username
            updateStmt.setInt(2, userId); // Set the target user ID

            // Execute the update and return true if at least one row was affected
            return updateStmt.executeUpdate() > 0;

        } catch (SQLException e) {
            // Log and rethrow any SQL exceptions
            System.err.println("Error updating username for user ID " + userId + ": " + e.getMessage());
            throw new RuntimeException("Could not update username for user ID " + userId, e);        }
    }

    //-----------------------------------------------------------------------
    /**
     * Updates the password of a specific user in the database.
     * <p>
     * This method updates the 'password' field of the user with the specified user ID in the 'users' table.
     * It returns {@code true} if the password was successfully updated (i.e., at least one row is affected),
     * and {@code false} if no rows were modified (possibly due to an invalid user ID).
     *
     * @param userId   the ID of the user whose password is to be updated
     * @param password the new password to be set (assumed to be pre-encrypted or hashed)
     * @return {@code true} if the update was successful; {@code false} otherwise
     * @throws RuntimeException if a database access error occurs
     */
    public boolean updatePassword(int userId, String password) {
        // SQL statement to update the user's password
        String sqlStatement = "UPDATE users SET password = ? " +
                "WHERE user_id = ?";

        // Establish connection and prepare the SQL statement
        try (Connection connection = DatabaseConnection.getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(sqlStatement)) {

            // Set the new password and user ID in the prepared statement
            preparedStatement.setString(1, password);
            preparedStatement.setInt(2, userId);

            // Execute the update and return true if at least one row was affected
            return preparedStatement.executeUpdate() > 0;

        } catch (SQLException e) {
            // Log and rethrow any exception with context
            System.err.println("Error updating password for user ID " + userId + ": " + e.getMessage());
            throw new RuntimeException("Failed to update password for user ID " + userId, e);        }
    }

    //-----------------------------------------------------------------------
    /**
     * Updates the user type, of a specific user in the database, and sets the corresponding admin type if applicable.
     * <p>
     * If the user type is changed to {@code MEMBER}, the {@code admin_type} column is set to {@code NULL}.
     * If changed to {@code ADMIN}, the {@code admin_type} is defaulted to {@code STANDARD}.
     *
     * @param userId   the ID of the user whose type is to be updated
     * @param userType the new user type to assign (either {@code ADMIN} or {@code MEMBER})
     * @return {@code true} if the update was successful; {@code false} otherwise
     * @throws RuntimeException if a database access error occurs
     */
    public boolean updateUserType(int userId, User_Type userType) {
        // SQL statement to update both user_type and admin_type fields for a specific user
        String sqlStatement = "UPDATE users SET user_type = ?, admin_type = ? " +
                "WHERE user_id = ?";

        // Establish database connection and prepare the SQL statement
        try (Connection connection = DatabaseConnection.getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(sqlStatement)) {

            // Set user_type (converted to lowercase string) and user_id
            preparedStatement.setString(1, userType.toString().toLowerCase());
            preparedStatement.setInt(3, userId);

            // Conditionally set admin_type:
            // If user is a MEMBER, admin_type should be NULL
            if(userType.equals(User_Type.MEMBER)) {
                preparedStatement.setNull(2, Types.VARCHAR);
            } else {
                // If user is an ADMIN, default admin_type to STANDARD
                preparedStatement.setString(2, AdminType.STANDARD.toString().toLowerCase());
            }

            // Execute the update and return true if at least one row was modified
            return preparedStatement.executeUpdate() > 0;

        } catch (SQLException e) {
            // Log and rethrow exception with context
            System.err.println("Error updating user type for user ID " + userId + ": " + e.getMessage());
            throw new RuntimeException("Failed to update user type for user ID " + userId, e);        }
    }

    //-----------------------------------------------------------------------
    /**
     * Updates the admin type of a specific administrator in the database.
     * <p>
     * This method sets the {@code admin_type} field in the 'users' table for the specified user ID.
     * It assumes that the user is already of type {@code ADMIN}. The method returns {@code true}
     * if the update was successful (i.e., at least one row was affected), otherwise {@code false}.
     *
     * @param userId     the ID of the administrator whose admin type is to be updated
     * @param adminType  the new {@code AdminType} to assign (e.g., {@code STANDARD}, {@code SUPER})
     * @return {@code true} if the admin type was successfully updated; {@code false} otherwise
     * @throws RuntimeException if a database access error occurs
     */
    public boolean updateAdminType(int userId, AdminType adminType) {
        // SQL statement to update the admin_type for a specific user
        String updateAdminTypeSQL = "UPDATE users SET admin_type = ? WHERE user_id = ?";

        // Establish database connection and prepare the update statement
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement updateAdminTypeStmt = connection.prepareStatement(updateAdminTypeSQL)) {

            // Set the new admin_type (converted to lowercase) and user ID
            updateAdminTypeStmt.setString(1, adminType.toString().toLowerCase());
            updateAdminTypeStmt.setInt(2, userId);

            // Execute update and return true if a row was affected
            return updateAdminTypeStmt.executeUpdate() > 0;

        } catch (SQLException e) {
            // Log and rethrow the SQL exception with user ID context
            System.err.println("Error updating admin type for user ID " + userId + ": " + e.getMessage());
            throw new RuntimeException("Failed to update admin type for user ID " + userId, e);        }
    }

    //-----------------------------------------------------------------------
    /**
     * Dynamically updates a specific field of a member user in the database.
     * <p>
     * This method allows updating most user profile fields (e.g., username, password, firstname, etc.)
     * by mapping field names to database column names. It also supports updating the member's address,
     * in which case the provided value must be an {@link Address} object and is passed to the {@code addressDAO}.
     *
     * <p>Supported fields:
     * <ul>
     *     <li>username</li>
     *     <li>password</li>
     *     <li>firstname</li>
     *     <li>surname</li>
     *     <li>date_of_birth</li>
     *     <li>gender</li>
     *     <li>email</li>
     *     <li>phone_number</li>
     *     <li>address</li>
     * </ul>
     *
     * @param memberId the user ID of the member whose field is to be updated
     * @param field the name of the field to update (e.g., "email", "gender", "address")
     * @param newValue the new value to set for the specified field; must match expected type
     * @return {@code true} if the update was successful; {@code false} otherwise
     * @throws IllegalArgumentException if the field name is invalid or the newValue type is incorrect
     * @throws RuntimeException if a database access error occurs
     */
    public boolean updateMemberField(int memberId, String field, Object newValue) {
        // Map of user-facing field names to actual database column names
        Map<String, String> fieldToColumn = Map.of(
                "username", "username",
                "password", "password",
                "firstname", "firstname",
                "surname", "surname",
                "date_of_birth", "date_of_birth",
                "gender", "gender",
                "email", "email",
                "phone_number", "phone_number"
        );

        try (Connection connection = DatabaseConnection.getConnection()) {

            // Handle address updates using the AddressDAO
            if ("address".equalsIgnoreCase(field)) {
                if (!(newValue instanceof Address)) {
                    throw new IllegalArgumentException("Expected Address object for 'address' field.");
                }
                return addressDAO.updateAddress((Address) newValue, memberId, connection);
            }

            // Map the field name to the corresponding column name
            String columnName = fieldToColumn.get(field.toLowerCase());

            if (columnName == null) {
                throw new IllegalArgumentException("Invalid or unsupported field name: " + field);
            }

            // Build dynamic SQL statement for the update
            String sqlStatement = "UPDATE users SET " + columnName + " = ? " +
                    "WHERE user_id = ?";


            try (PreparedStatement preparedStatement = connection.prepareStatement(sqlStatement)) {
                // Handle the type of the new value and bind it to the prepared statement
                if (newValue instanceof String) {
                    preparedStatement.setString(1, newValue.toString());
                } else if (newValue instanceof LocalDate) {
                    preparedStatement.setDate(1, Date.valueOf(((LocalDate) newValue)));
                } else if (newValue instanceof Gender) {
                    preparedStatement.setString(1, newValue.toString().toLowerCase());
                } else {
                    throw new IllegalArgumentException("Unsupported data type for field: " + field);
                }

                // Set the member ID parameter
                preparedStatement.setInt(2, memberId);

                // Execute the update and return success status
                return preparedStatement.executeUpdate() > 0;
            }

        } catch (SQLException e) {
            // Log and rethrow as a runtime exception with context
            System.err.println("Error updating member field: " + field + " for member ID " + memberId + " - " + e.getMessage());
            throw new RuntimeException("Failed to update field: " + field + " for member ID: " + memberId, e);
        }
    }

    //-----------------------------------------------------------------------
    /**
     * Deletes a user and all associated transactions from the database.
     * <p>
     * This method performs two delete operations:
     * <ol>
     *   <li>Deletes all transaction records linked to the specified user.</li>
     *   <li>Deletes the user record itself from the {@code users} table.</li>
     * </ol>
     *
     * <p>This method does not delete the user's address separately, assuming
     * that the address record is either cascaded automatically or not required
     * to be cleaned up independently.
     *
     * @param userId the ID of the user to be deleted
     * @return {@code true} if the user was successfully deleted; {@code false} otherwise
     * @throws RuntimeException if a database access error occurs
     */
    public boolean deleteUser(int userId) {
        // SQL to delete all transactions associated with the user
        String deleteTransactionSQL = "DELETE FROM transactions WHERE user_id = ?";
        // SQL to delete the user from the users table
        String deleteUserSQL = "DELETE FROM users WHERE user_id = ?";

        // Establish connection to the database
        try (Connection connection = DatabaseConnection.getConnection()) {

            // 1. Delete all the transactions made by the user
            try (PreparedStatement deleteTransactionStmt = connection.prepareStatement(deleteTransactionSQL)) {
                deleteTransactionStmt.setInt(1, userId);
                // Execute the transaction deletion (even if 0 transactions exist)
                int deletedRows = deleteTransactionStmt.executeUpdate();
            }

            // 2. Delete the user
            try (PreparedStatement deleteUserStmt = connection.prepareStatement(deleteUserSQL)) {
                deleteUserStmt.setInt(1, userId);
                // Return true if the user was deleted (i.e., at least one row affected)
                return deleteUserStmt.executeUpdate() > 0;
            }

        } catch (SQLException e) {
            // Log error and rethrow wrapped as a runtime exception
            System.err.println("Error deleting user with ID " + userId + ": " + e.getMessage());
            throw new RuntimeException("Failed to delete user with ID: " + userId, e);        }
    }

    //-----------------------------------------------------------------------
    /**
     * Retrieves a user by their unique ID, including their address details.
     * <p>
     * This method performs a LEFT OUTER JOIN between the {@code users} and {@code addresses} tables
     * to fetch a complete user profile. It returns a {@link Member} or {@link Administrator} object
     * depending on the {@code user_type} stored in the database. If no user is found for the given ID,
     * it returns {@code null}.
     *
     * @param userId the ID of the user to retrieve
     * @return a {@link User} object ({@link Member} or {@link Administrator}) if found; {@code null} otherwise
     * @throws RuntimeException if a database access error occurs
     */
    public User getUserById(int userId) {
        // SQL to join users and addresses to retrieve full user profile
        String sqlStatement = "SELECT * FROM users u " +
                "LEFT OUTER JOIN addresses a " +
                "ON u.user_id = a.user_id " +
                "WHERE u.user_id = ?";

        // Establish DB connection and prepare statement
        try (Connection connection = DatabaseConnection.getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(sqlStatement)) {

            // Bind userId to the query
            preparedStatement.setInt(1, userId);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                // If user exists, process the result
                if (resultSet.next()) {
                    Address address = null;
                    String street = resultSet.getString("street");

                    // Construct Address object only if address fields are present
                    if (street != null) {
                        address = new Address(
                                street,
                                resultSet.getString("city"),
                                resultSet.getString("pincode"),
                                resultSet.getString("state"),
                                resultSet.getString("country")
                        );
                    }

                    // Determine user type and return appropriate subclass
                    User_Type userType = User_Type.valueOf(resultSet.getString("user_type").toUpperCase());

                    if (userType.equals(User_Type.ADMIN)) {

                        return new Administrator(
                                resultSet.getInt("user_id"),
                                resultSet.getString("username"),
                                User_Type.valueOf(resultSet.getString("user_type").toUpperCase()),
                                resultSet.getString("firstname"),
                                resultSet.getString("surname"),
                                resultSet.getDate("date_of_birth").toLocalDate(),
                                Gender.valueOf(resultSet.getString("gender").toUpperCase()),
                                resultSet.getString("email"),
                                resultSet.getString("phone_number"),
                                address,
                                AdminType.valueOf(resultSet.getString("admin_type").toUpperCase())
                        );

                    } else if (userType.equals(User_Type.MEMBER)) {

                        return new Member(
                                resultSet.getInt("user_id"),
                                resultSet.getString("username"),
                                User_Type.valueOf(resultSet.getString("user_type").toUpperCase()),
                                resultSet.getString("firstname"),
                                resultSet.getString("surname"),
                                resultSet.getDate("date_of_birth").toLocalDate(),
                                Gender.valueOf(resultSet.getString("gender").toUpperCase()),
                                resultSet.getString("email"),
                                resultSet.getString("phone_number"),
                                address
                        );

                    }
                }

            }

        } catch (SQLException e) {
            // Log and wrap SQL error
            System.err.println("SQL error while retrieving user with ID " + userId + ": " + e.getMessage());
            throw new RuntimeException("Failed to retrieve user with ID: " + userId, e);        }

        // Return null if no user is found
        return null;
    }

    //-----------------------------------------------------------------------
    /**
     * Retrieves a user by their unique username, including their address information if available.
     * <p>
     * This method performs a LEFT OUTER JOIN between the {@code users} and {@code addresses} tables,
     * ensuring that user data is retrieved even if the address is missing. Based on the {@code user_type},
     * it returns either a {@link Member} or {@link Administrator} object populated with the user's details.
     *
     * @param username the username of the user to retrieve
     * @return a {@link User} object (either {@link Member} or {@link Administrator}) if found, otherwise {@code null}
     * @throws RuntimeException if a database access error occurs
     */
    public User getUserByUsername(String username) {
        // SQL to retrieve user and address by username using LEFT JOIN
        String sqlStatement = "SELECT * FROM users u " +
                "LEFT OUTER JOIN addresses a " +
                "ON u.user_id = a.user_id " +
                "WHERE u.username = ?";

        // Open connection and prepare statement
        try (Connection connection = DatabaseConnection.getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(sqlStatement)) {

            // Bind the username parameter
            preparedStatement.setString(1, username);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                // If a user exists with the given username, process the result
                if(resultSet.next()) {
                    Address address = null;
                    String street = resultSet.getString("street");

                    // Create Address object only if address fields are not null
                    if(street != null) {
                        address = new Address(
                                street,
                                resultSet.getString("city"),
                                resultSet.getString("pincode"),
                                resultSet.getString("state"),
                                resultSet.getString("country")
                        );
                    }

                    // Get the user type and create the corresponding object
                    User_Type userType = User_Type.valueOf(resultSet.getString("user_type").toUpperCase());

                    if (userType.equals(User_Type.ADMIN)) {

                        return new Administrator(
                                resultSet.getInt("user_id"),
                                resultSet.getString("username"),
                                User_Type.valueOf(resultSet.getString("user_type").toUpperCase()),
                                resultSet.getString("firstname"),
                                resultSet.getString("surname"),
                                resultSet.getDate("date_of_birth").toLocalDate(),
                                Gender.valueOf(resultSet.getString("gender").toUpperCase()),
                                resultSet.getString("email"),
                                resultSet.getString("phone_number"),
                                address,
                                AdminType.valueOf(resultSet.getString("admin_type").toUpperCase())
                        );

                    } else if (userType.equals(User_Type.MEMBER)) {

                        return new Member(
                                resultSet.getInt("user_id"),
                                resultSet.getString("username"),
                                User_Type.valueOf(resultSet.getString("user_type").toUpperCase()),
                                resultSet.getString("firstname"),
                                resultSet.getString("surname"),
                                resultSet.getDate("date_of_birth").toLocalDate(),
                                Gender.valueOf(resultSet.getString("gender").toUpperCase()),
                                resultSet.getString("email"),
                                resultSet.getString("phone_number"),
                                address
                        );

                    }
                }
            }

        } catch (SQLException e) {
            // Log and rethrow the SQL exception with context
            System.err.println("SQL error while retrieving user with username '" + username + "': " + e.getMessage());
            throw new RuntimeException("Failed to retrieve user by username: " + username, e);        }

        // Return null if no user found with the provided username
        return null;
    }

    //-----------------------------------------------------------------------
    /**
     * Retrieves the user type (ADMIN or MEMBER) for a specific user ID.
     * <p>
     * This method queries the {@code users} table for the {@code user_type} of a given user.
     * The result is converted to the corresponding {@link User_Type} enum.
     * <p>
     * Returns {@code null} if no user is found with the provided ID.
     *
     * @param userId the ID of the user whose type is to be fetched
     * @return {@link User_Type} enum representing the user's type, or {@code null} if the user doesn't exist
     * @throws RuntimeException if a database access error occurs
     */
    public User_Type getUserType(int userId) {
        // SQL to retrieve only the user_type for the given user_id
        String sqlStatement = "SELECT user_type FROM users " +
                "WHERE user_id = ?";

        // Establish database connection and prepare the SQL statement
        try (Connection connection = DatabaseConnection.getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(sqlStatement)) {

            // Bind the user ID to the statement
            preparedStatement.setInt(1, userId);

            // Execute the query and check if a result is returned
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    // Convert the result to enum (case-insensitive)
                    return User_Type.valueOf(resultSet.getString("user_type").toUpperCase());
                }
            }

        } catch (SQLException e) {
            // Log and rethrow as unchecked exception
            System.err.println("SQL error while retrieving user type for user_id " + userId + ": " + e.getMessage());
            throw new RuntimeException("Failed to retrieve user type", e);        }

        // Return null if user does not exist
        return null;
    }

    //-----------------------------------------------------------------------
    /**
     * Retrieves the {@link AdminType} of a user with the specified user ID.
     * <p>
     * This method queries the {@code users} table to get the {@code admin_type}
     * for a user who is expected to be an administrator.
     * <p>
     * Returns {@code null} if the user is not found or if the {@code admin_type} is {@code null}.
     *
     * @param userId the ID of the user whose admin type is to be fetched
     * @return the {@link AdminType} of the user, or {@code null} if not found or not set
     * @throws RuntimeException if a database access error occurs
     */
    public AdminType getAdminType(int userId) {
        // SQL to fetch the admin_type column for a given user_id
        String sqlStatement = "SELECT admin_type FROM users WHERE user_id = ?";

        // Establish DB connection and prepare the query
        try (Connection connection = DatabaseConnection.getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(sqlStatement)) {

            // Bind the userId to the SQL parameter
            preparedStatement.setInt(1, userId);

            // Execute the query and process the result
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next() && resultSet.getString("admin_type") != null) {
                    // Convert admin_type string to enum (ignoring case and trimming spaces)
                    return AdminType.valueOf(
                            resultSet.getString("admin_type").trim().toUpperCase()
                    );
                }
            }

        } catch (SQLException e) {
            // Log the error and rethrow as a RuntimeException
            System.err.println("SQL error while retrieving admin type for user_id " + userId + ": " + e.getMessage());
            throw new RuntimeException("Failed to retrieve admin type", e);        }

        // Return null if admin_type is not present or user does not exist
        return null;
    }

    //-----------------------------------------------------------------------
    /**
     * Retrieves full user details, including the password, for authentication purposes.
     * <p>
     * This method performs a LEFT OUTER JOIN between the {@code users} and {@code addresses} tables
     * to retrieve both user credentials and profile information based on the given username.
     * <p>
     * This is the only method in the DAO that exposes the user's password field, and should be used
     * strictly within authentication logic (e.g., {@code AuthenticationService}).
     * <p>
     * Returns {@code null} if no user with the specified username exists.
     *
     * @param username the username of the user attempting to authenticate
     * @return a {@link User} object (either {@link Member} or {@link Administrator}) including password, or {@code null} if not found
     * @throws RuntimeException if a database access error occurs
     */
    public User getUserForAuthentication(String username) {
        // SQL query joins users and addresses to retrieve all required fields for login verification
        String sqlStatement = "SELECT * FROM users u " +
                "LEFT OUTER JOIN addresses a " +
                "ON u.user_id = a.user_id " +
                "WHERE u.username = ?";

        // Open connection and prepare statement
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sqlStatement)) {

            // Bind the input username to the SQL query
            preparedStatement.setString(1, username);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if(resultSet.next()) {
                    Address address = null;
                    String street = resultSet.getString("street");

                    // Construct address only if street (primary field) is not null
                    if(street != null) {
                        address = new Address(
                                street,
                                resultSet.getString("city"),
                                resultSet.getString("pincode"),
                                resultSet.getString("state"),
                                resultSet.getString("country")
                        );
                    }

                    // Determine user type and construct appropriate object with password
                    User_Type userType = User_Type.valueOf(resultSet.getString("user_type").toUpperCase());

                    if (userType.equals(User_Type.ADMIN)) {

                        return new Administrator(
                                resultSet.getInt("user_id"),
                                resultSet.getString("username"),
                                resultSet.getString("password"), // includes password for authentication
                                User_Type.valueOf(resultSet.getString("user_type").toUpperCase()),
                                resultSet.getString("firstname"),
                                resultSet.getString("surname"),
                                resultSet.getDate("date_of_birth").toLocalDate(),
                                Gender.valueOf(resultSet.getString("gender").toUpperCase()),
                                resultSet.getString("email"),
                                resultSet.getString("phone_number"),
                                address,
                                AdminType.valueOf(resultSet.getString("admin_type").toUpperCase())
                        );

                    } else if (userType.equals(User_Type.MEMBER)) {

                        return new Member(
                                resultSet.getInt("user_id"),
                                resultSet.getString("username"),
                                resultSet.getString("password"), // includes password for authentication
                                User_Type.valueOf(resultSet.getString("user_type").toUpperCase()),
                                resultSet.getString("firstname"),
                                resultSet.getString("surname"),
                                resultSet.getDate("date_of_birth").toLocalDate(),
                                Gender.valueOf(resultSet.getString("gender").toUpperCase()),
                                resultSet.getString("email"),
                                resultSet.getString("phone_number"),
                                address
                        );

                    }
                }
            }

        } catch (SQLException e) {
            // Log and rethrow exception with context
            System.err.println("SQL error during authentication lookup for username: " + username + " - " + e.getMessage());
            throw new RuntimeException("Failed to retrieve user for authentication", e);        }

        // Return null if user not found
        return null;
    }
}
