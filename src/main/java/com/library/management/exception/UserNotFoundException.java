package com.library.management.exception;

/**
 * Exception thrown when a requested user is not found in the system.
 * <p>
 * This exception is typically used in the DAO or service layer to indicate
 * that the user with a given ID, username, or email does not exist in the database.
 *
 * @see com.library.management.dao.UserDAO
 * @see com.library.management.service.UserService
 */
public class UserNotFoundException extends RuntimeException{
    /**
     * Constructs a new UserNotFoundException with the specified detail message.
     *
     * @param message the detail message explaining the reason for the exception
     */
    public UserNotFoundException(String message) {
        super(message);
    }

}
