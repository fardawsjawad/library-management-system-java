package com.library.management.model;

/**
 * Enum representing the gender of a user in the Library Management System.
 * <p>
 * This enum can be used to classify users as either male or female when registering
 * or managing user profiles within the system.
 *
 * <p>Example usage:
 * <pre>
 *     User user = new User(...);
 *     user.setGender(Gender.MALE);
 * </pre>
 *
 * @author [Your Name]
 *
 * @see com.library.management.model.User
 */
public enum Gender {

    /** Represents a male user. */
    MALE,

    /** Represents a female user. */
    FEMALE
}
