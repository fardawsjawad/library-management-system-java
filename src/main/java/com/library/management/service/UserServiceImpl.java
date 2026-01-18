package com.library.management.service;



import com.library.management.dao.TransactionDAO;
import com.library.management.dao.UserDAO;
import com.library.management.exception.SuperAdminException;
import com.library.management.exception.UserNotFoundException;
import com.library.management.model.*;
import com.library.management.util.PasswordHasher;

import java.util.List;

/**
 * Class: UserServiceImpl
 *
 * Description:
 * - This class implements the UserService interface and provides the concrete business logic for all user-related operations
 *   in the Library Management System.
 * - Responsibilities include user registration, authentication, data updates, role assignments, and validations.
 * - It integrates with DAO classes for database operations and includes exception handling and validations for data integrity.
 *
 * Dependencies:
 * - UserDAO: Handles all persistence operations related to users.
 * - TransactionDAO: Used to delete user transactions when a user is promoted to an admin.
 * - PasswordHasher: For secure password encryption using BCrypt.
 *
 * Key Features:
 * - Secure user registration with hashed passwords.
 * - Differentiated management for members and admins.
 * - Validation for user existence and uniqueness.
 * - AdminType and UserType enforcement with appropriate safety rules (e.g., SuperAdmin protection).
 *
 * @author Fardaws Jawad
 */
public class UserServiceImpl implements UserService{

    private final UserDAO userDAO;
    private final TransactionDAO transactionDAO;

    //-----------------------------------------------------------------------
    /**
     * Constructor initializing required DAO objects.
     */
    public UserServiceImpl() {
        userDAO = new UserDAO();
        transactionDAO = new TransactionDAO();
    }

    //-----------------------------------------------------------------------
    /**
     * Registers a new user with password encryption.
     *
     * @param user User - User object with details
     * @return boolean - true if user is registered successfully
     * @throws IllegalArgumentException - for null values or duplicate usernames
     */
    @Override
    public boolean registerUser(User user) {
        if(user == null || user.getUsername() == null || user.getPassword() == null) {
            throw new IllegalArgumentException("User and essential fields cannot be null.");
        }

        if(userDAO.getUserByUsername(user.getUsername()) != null) {
            throw new IllegalArgumentException("Username already exists.");
        }

        String hashedPassword = PasswordHasher.hashPassword(user.getPassword());
        user.setPassword(hashedPassword);
        return userDAO.addUser(user);
    }

    //-----------------------------------------------------------------------
    /**
     * Retrieves a user by their ID.
     *
     * @param userId int - User ID
     * @return User - the found user
     * @throws IllegalArgumentException, UserNotFoundException
     */
    @Override
    public User getUserById(int userId) {
        if(userId <= 0) throw new IllegalArgumentException("Invalid user ID.");

        User user = userDAO.getUserById(userId);
        if (user == null) {
            throw new UserNotFoundException("User with ID " + userId + " does not exist.");
        }

        return userDAO.getUserById(userId);
    }

    //-----------------------------------------------------------------------
    /**
     * Retrieves a user by their username.
     *
     * @param username String - Username
     * @return User - the found user
     * @throws IllegalArgumentException, UserNotFoundException
     */
    @Override
    public User getUserByUsername(String username) {
        if(username == null || username.trim().isEmpty()) throw new IllegalArgumentException("Username required.");

        User user = userDAO.getUserByUsername(username);
        if (user == null) {
            throw new UserNotFoundException("User with username '" + username + "' does not exist.");
        }

        return userDAO.getUserByUsername(username.trim());
    }

    //-----------------------------------------------------------------------
    /**
     * Retrieves all users from the system.
     *
     * @return List<User> - all users
     */
    @Override
    public List<User> getAllUsers() {
        return userDAO.getAllUsers();
    }

    //-----------------------------------------------------------------------
    /**
     * Retrieves all admin users.
     *
     * @return List<Administrator> - list of admins
     */
    @Override
    public List<Administrator> getAllAdmins() {
        return userDAO.getAllAdmins();
    }

    //-----------------------------------------------------------------------
    /**
     * Retrieves all member users.
     *
     * @return List<Member> - list of members
     */
    @Override
    public List<Member> getAllMembers() {
        return userDAO.getAllMembers();
    }

    //-----------------------------------------------------------------------
    /**
     * Updates a user's details.
     *
     * @param user User - updated user object
     * @return boolean - true if updated successfully
     */
    @Override
    public boolean updateUser(User user) {
        if(user == null || user.getUserId() <= 0) {
            throw new IllegalArgumentException("Valid user required for update.");
        }

        User existingUser = userDAO.getUserById(user.getUserId());
        if (existingUser == null) {
            throw new UserNotFoundException("User with ID " + user.getUserId() + " does not exist.");
        }

        return userDAO.updateUser(user);
    }

    //-----------------------------------------------------------------------
    /**
     * Updates a user's username.
     *
     * @param userId int - user ID
     * @param newUsername String - new username
     * @return boolean - true if update is successful
     */
    @Override
    public boolean updateUsername(int userId, String newUsername) {
        if(userId <= 0 || newUsername == null || newUsername.isEmpty()) {
            throw new IllegalArgumentException("Invalid user ID or username.");
        }

        User user = userDAO.getUserById(userId);
        if (user == null) {
            throw new UserNotFoundException("User with ID " + userId + " does not exist.");
        }

        user = userDAO.getUserByUsername(newUsername);
        if(user != null) {
            throw new IllegalArgumentException("Username already exists for another user.");
        }

        return userDAO.updateUsername(userId, newUsername);
    }

    //-----------------------------------------------------------------------

    /**
     * Updates a user's password (hashed).
     *
     * @param userId int - user ID
     * @param newPassword String - new password
     * @return boolean - true if updated
     */
    @Override
    public boolean updatePassword(int userId, String newPassword) {
        if(userId <= 0 || newPassword == null || newPassword.length() < 6) {
            throw new IllegalArgumentException("Password must be at least 6 characters and user ID must be valid.");
        }

        User user = userDAO.getUserById(userId);
        if (user == null) {
            throw new UserNotFoundException("User with ID " + userId + " does not exist.");
        }

        String hashedPassword = PasswordHasher.hashPassword(newPassword);
        return userDAO.updatePassword(userId, hashedPassword);
    }

    //-----------------------------------------------------------------------
    /**
     * Updates a user's role (e.g., MEMBER, ADMIN).
     *
     * @param userId int - user ID
     * @param userType User_Type - new user type
     * @return boolean - true if updated
     */
    @Override
    public boolean updateUserType(int userId, User_Type userType) {
        if(userId <= 0 || userType == null) {
            throw new IllegalArgumentException("Invalid user ID or usertype.");
        }

        User user = userDAO.getUserById(userId);
        if (user == null) {
            throw new UserNotFoundException("The user with the ID " + userId + " does not exist.");
        }

        if(userType.equals(user.getUserType())) {
            throw new IllegalArgumentException("User type is already " + userType);
        }

        if (userType.equals(User_Type.ADMIN)) {
            transactionDAO.deleteUserTransactions(userId);
        }

        return userDAO.updateUserType(userId, userType);
    }

    //-----------------------------------------------------------------------
    /**
     * Updates an admin’s specific AdminType (e.g., SUPER, STANDARD).
     *
     * @param userId int - user ID
     * @param adminType AdminType - new admin type
     * @return boolean - true if updated
     */
    @Override
    public boolean updateAdminType(int userId, AdminType adminType) {
        if(userId <= 0 || adminType == null) {
            throw new IllegalArgumentException("Invalid user ID or admin type.");
        }

        User user = userDAO.getUserById(userId);

        if (user == null) {
            throw new UserNotFoundException("Admin with the ID " + userId + " does not exist.");
        }

        if (!user.getUserType().equals(User_Type.ADMIN)) {
            throw new IllegalArgumentException("User with ID " + userId + " is not an admin.");
        }

        AdminType currentAdmin = userDAO.getAdminType(userId);
        if (currentAdmin.equals(adminType)) {
            throw new IllegalArgumentException("The admin is already " + adminType);
        }

        return userDAO.updateAdminType(userId, adminType);
    }

    //-----------------------------------------------------------------------
    /**
     * Dynamically updates a specific field for a member user.
     *
     * @param memberId int - member ID
     * @param field String - field name
     * @param newValue Object - new value
     * @return boolean - true if updated
     */
    @Override
    public boolean updateMemberField(int memberId, String field, Object newValue) {
        if(memberId <= 0 || field == null || field.isBlank() || newValue == null) {
            throw new IllegalArgumentException("Invalid book ID, field, or value.");
        }

        return userDAO.updateMemberField(memberId, field, newValue);
    }

    //-----------------------------------------------------------------------
    /**
     * Deletes a user from the system (with protection for Super Admin).
     *
     * @param userId int - user ID
     * @return boolean - true if deleted
     */
    @Override
    public boolean deleteUser(int userId) {
        if (userId <= 0) throw new IllegalArgumentException("Invalid user ID.");

        User user = userDAO.getUserById(userId);
        if (user == null) {
            throw new UserNotFoundException("User with ID" + userId + " does not exist.");
        }

        User_Type userType = user.getUserType();
        if (user.getUserType().equals(User_Type.ADMIN) &&
                ((Administrator) user).getAdminType().equals(AdminType.SUPER)) {
            throw new SuperAdminException("Super admin cannot be removed.");
        }

        return userDAO.deleteUser(userId);
    }

    //-----------------------------------------------------------------------
    /**
     * Returns the user type for a given user.
     *
     * @param userId int - user ID
     * @return User_Type - user's type
     */
    @Override
    public User_Type getUserType(int userId) {
        if(userId <= 0) throw new IllegalArgumentException("Invalid user ID.");

        User user = userDAO.getUserById(userId);
        if (user == null) {
            throw new UserNotFoundException("User with ID " + userId + " does not exist.");
        }

        return userDAO.getUserType(userId);
    }

    //-----------------------------------------------------------------------
    /**
     * Returns the AdminType of a user (if user is an admin).
     *
     * @param userId int - user ID
     * @return AdminType - admin’s type
     */
    @Override
    public AdminType getAdminType(int userId) {
        if(userId <= 0) throw new IllegalArgumentException("Invalid user ID.");

        User user = userDAO.getUserById(userId);
        if (user == null ||
                !user.getUserType().equals(User_Type.ADMIN)) {
            throw new UserNotFoundException("Admin with ID " + userId + " does not exist.");
        }

        return userDAO.getAdminType(userId);
    }

    //-----------------------------------------------------------------------
    /**
     * Checks if a user exists by ID.
     *
     * @param userId int - user ID
     * @return boolean - true if exists
     */
    @Override
    public boolean userExists(int userId) {
        if (userId <= 0) throw new IllegalArgumentException("Invalid user ID.");
        return userDAO.getUserById(userId) != null;
    }

}
