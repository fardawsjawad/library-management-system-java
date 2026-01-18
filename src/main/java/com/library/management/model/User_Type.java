package com.library.management.model;

/**
 * Enum representing the type of user in the Library Management System.
 * <p>
 * This classification helps in assigning different roles and permissions
 * within the application based on whether the user is an administrator or a regular member.
 *
 * <p>Possible values:
 * <ul>
 *     <li>{@code ADMIN} - Has administrative privileges such as managing users, books, and transactions.</li>
 *     <li>{@code MEMBER} - A regular user who can borrow and return books.</li>
 * </ul>
 *
 * <p>Example usage:
 * <pre>
 *     User user = new User(...);
 *     user.setUserType(User_Type.ADMIN);
 * </pre>
 *
 * @author Fardaws Jawad
 *
 * @see com.library.management.model.User
 * @see com.library.management.model.AdminType
 */
public enum User_Type {

    /** Represents an administrator user with elevated access. */
    ADMIN,

    /** Represents a standard library member with limited access. */
    MEMBER
}
