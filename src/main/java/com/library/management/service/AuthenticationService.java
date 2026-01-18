package com.library.management.service;

import com.library.management.dao.UserDAO;
import com.library.management.model.User;
import com.library.management.util.PasswordHasher;

/**
 * Class: AuthenticationService
 *
 * Description:
 * - Provides authentication functionality for users attempting to log into the Library Management System.
 * - Fetches user data using the username and verifies the provided password using a secure hashing utility.
 * - Serves as a core utility service for validating login credentials before granting access to the system.
 *
 * Dependencies:
 * - UserDAO: Retrieves user information from the database for authentication.
 * - PasswordHasher: Compares plaintext password with stored hashed password securely.
 *
 * Key Responsibilities:
 * - Authenticate users by validating credentials (username and password)
 * - Return the authenticated User object if credentials are valid
 *
 * @author Fardaws Jawad
 */
public class AuthenticationService {

    private final UserDAO userDAO = new UserDAO();

    /**
     * Authenticates a user based on provided username and password.
     *
     * Description:
     * - Retrieves the user from the database using the provided username.
     * - Verifies the password using BCrypt hashing to ensure secure authentication.
     * - Returns the corresponding User object if credentials are valid.
     *
     * @param username String - The username entered by the user
     * @param password String - The plaintext password entered by the user
     * @return User - The authenticated user object if credentials are valid; null otherwise
     */
    public User authenticate(String username, String password) {
        User user = userDAO.getUserForAuthentication(username);
        if (user != null && PasswordHasher.checkPassword(password, user.getPassword())) {
            return user;
        }

        return null;
    }

}
