package com.library.management.model;

import java.time.LocalDate;

/**
 * Represents a library member in the Library Management System.
 * <p>
 * A member is a user who can borrow and return books. This class extends the {@link User} class
 * and inherits all user-related fields such as name, contact details, and address.
 * Unlike administrators, members do not have elevated privileges.
 *
 * <p>Example usage:
 * <pre>
 *     Member member = new Member("john_doe", "password123", User_Type.MEMBER,
 *         "John", "Doe", LocalDate.of(1995, 6, 15), Gender.MALE,
 *         "john@example.com", "9876543210", new Address(...));
 * </pre>
 *
 * @author Fardaws Jawad
 *
 * @see com.library.management.model.User
 * @see com.library.management.model.User_Type
 * @see com.library.management.model.Address
 */
public class Member extends User {

    //-----------------------------------------------------------------------
    /**
     * Default no-argument constructor.
     * <p>
     * Required by frameworks that use reflection or serialization (e.g., JDBC, JPA, Jackson).
     * Initializes an empty {@code Member} object with default values.
     */
    public Member() {
    }

    /**
     * Constructs a Member with login and personal details.
     * Typically used when registering a new member in the system.
     *
     * @param username      the login username
     * @param password      the login password
     * @param userType      user type (should be MEMBER)
     * @param firstname     first name
     * @param surname       last name
     * @param date_of_birth date of birth
     * @param gender        gender
     * @param email         email address
     * @param phoneNumber   contact number
     * @param address       physical address
     */
    public Member(String username, String password, User_Type userType, String firstname, String surname, LocalDate date_of_birth, Gender gender, String email, String phoneNumber, Address address) {
        super(username, password, userType, firstname, surname, date_of_birth, gender, email, phoneNumber, address);
    }

    /**
     * Constructs a Member with complete details including user ID.
     * Useful when retrieving a member record from the database.
     *
     * @param userId        unique ID of the member
     * @param username      login username
     * @param userType      user type (MEMBER)
     * @param firstname     first name
     * @param surname       last name
     * @param date_of_birth date of birth
     * @param gender        gender
     * @param email         email address
     * @param phoneNumber   contact number
     * @param address       member's address
     */
    public Member(int userId, String username, User_Type userType, String firstname, String surname, LocalDate date_of_birth, Gender gender, String email, String phoneNumber, Address address) {
        super(userId, username, userType, firstname, surname, date_of_birth, gender, email, phoneNumber, address);
    }

    /**
     * Constructs a Member without username and userType.
     * Useful for displaying profile or listing members where login details are not required.
     *
     * @param userId        ID of the member
     * @param firstname     first name
     * @param surname       last name
     * @param date_of_birth date of birth
     * @param gender        gender
     * @param email         email address
     * @param phoneNumber   contact number
     * @param address       address
     */
    public Member(int userId, String firstname, String surname, LocalDate date_of_birth, Gender gender, String email, String phoneNumber, Address address) {
        super(userId, firstname, surname, date_of_birth, gender, email, phoneNumber, address);
    }

    /**
     * Constructs a Member with userType but without login credentials.
     * Useful for internal data transfer or when credentials are handled separately.
     *
     * @param userId        member ID
     * @param userType      user type
     * @param firstname     first name
     * @param surname       last name
     * @param date_of_birth date of birth
     * @param gender        gender
     * @param email         email
     * @param phoneNumber   phone number
     * @param address       address
     */
    public Member(int userId, User_Type userType, String firstname, String surname, LocalDate date_of_birth, Gender gender, String email, String phoneNumber, Address address) {
        super(userId, userType, firstname, surname, date_of_birth, gender, email, phoneNumber, address);
    }

    /**
     * Constructs a Member with complete information including credentials and ID.
     * Used when full user data is required (e.g., authentication and profile).
     *
     * @param userId        member ID
     * @param username      username
     * @param password      password
     * @param userType      user type
     * @param firstname     first name
     * @param surname       last name
     * @param date_of_birth birth date
     * @param gender        gender
     * @param email         email address
     * @param phoneNumber   phone number
     * @param address       address
     */
    public Member(int userId, String username, String password, User_Type userType, String firstname, String surname, LocalDate date_of_birth, Gender gender, String email, String phoneNumber, Address address) {
        super(userId, username, password, userType, firstname, surname, date_of_birth, gender, email, phoneNumber, address);
    }
}
