package com.library.management.exception;


/**
 * Exception thrown when an operation violates restrictions related to the Super Admin user.
 * <p>
 * This exception is typically used to prevent unauthorized modification or deletion
 * of the Super Admin account, ensuring the integrity and security of the system's top-level administrative access.
 *
 */
public class SuperAdminException extends RuntimeException{
    /**
     * Constructs a new SuperAdminException with the specified detail message.
     *
     * @param message the detail message explaining the reason for the exception
     */
    public SuperAdminException(String message) {
        super(message);
    }

}
