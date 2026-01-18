package com.library.management.model;

import java.time.LocalDate;
import java.util.Objects;

/**
 * Represents a user in the Library Management System.
 * <p>
 * A user can be either an admin or a member, and contains information such as
 * login credentials, personal details, contact information, and address.
 * <p>
 * This class is used for registration, login, user profile management, and permission control.
 *
 * <p>Example usage:
 * <pre>
 *     User member = new User("john_doe", "password123", User_Type.MEMBER,
 *         "John", "Doe", LocalDate.of(1990, 5, 12), Gender.MALE,
 *         "john@example.com", "9876543210", new Address(...));
 * </pre>
 *
 * @author Fardaws Jawad
 *
 * @see com.library.management.model.User_Type
 * @see com.library.management.model.Gender
 * @see com.library.management.model.Address
 */
public class User {

    /** Unique identifier for the user (auto-generated). */
    private int userId;

    /** Unique username used for login authentication. */
    private String username;

    /** Encrypted or plain text password used for login (should be encrypted in production). */
    private String password;

    /** Role/type of the user - either ADMIN or MEMBER. */
    private User_Type userType;

    /** First name of the user. */
    private String firstname;

    /** Surname (last name) of the user. */
    private String surname;

    /** Date of birth of the user. */
    private LocalDate date_of_birth;

    /** Gender of the user - MALE or FEMALE. */
    private  Gender gender;

    /** Email address used for contact and communication. */
    private String email;

    /** Contact phone number of the user. */
    private String phoneNumber;

    /** Address object containing street, city, pin code, etc. */
    private Address address;

    //-----------------------------------------------------------------------
    /** Default constructor required for frameworks like JDBC/Hibernate. */
    public User() {
    }

    /**
     * Constructor used when fetching user details from the database (excluding password).
     *
     * @param userId        unique user ID
     * @param username      login username
     * @param userType      type of the user (ADMIN/MEMBER)
     * @param firstname     first name of the user
     * @param surname       surname of the user
     * @param date_of_birth date of birth
     * @param gender        gender
     * @param email         email address
     * @param phoneNumber   contact number
     * @param address       address object
     */
    public User(int userId, String username, User_Type userType, String firstname, String surname,
                LocalDate date_of_birth, Gender gender, String email,
                String phoneNumber, Address address) {

        this.userId = userId;
        this.username = username;
        this.userType = userType;
        this.firstname = firstname;
        this.surname = surname;
        this.date_of_birth = date_of_birth;
        this.gender = gender;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.address = address;
    }


    /**
     * Constructor used for creating a new user (typically during registration).
     *
     * @param username      login username
     * @param password      login password
     * @param userType      user type (ADMIN/MEMBER)
     * @param firstname     first name
     * @param surname       surname
     * @param date_of_birth date of birth
     * @param gender        gender
     * @param email         email address
     * @param phoneNumber   contact number
     * @param address       address object
     */
    public User(String username, String password, User_Type userType,
                String firstname, String surname, LocalDate date_of_birth, Gender gender,
                String email, String phoneNumber, Address address) {

        this.username = username;
        this.password = password;
        this.userType = userType;
        this.firstname = firstname;
        this.surname = surname;
        this.date_of_birth = date_of_birth;
        this.gender = gender;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.address = address;
    }

    /**
     * Constructor used for lightweight user data display (excluding login and type).
     *
     * @param userId        user ID
     * @param firstname     first name
     * @param surname       surname
     * @param date_of_birth date of birth
     * @param gender        gender
     * @param email         email address
     * @param phoneNumber   contact number
     * @param address       address object
     */
    public User(int userId, String firstname, String surname,
                LocalDate date_of_birth, Gender gender,
                String email, String phoneNumber, Address address) {

        this.userId = userId;
        this.firstname = firstname;
        this.surname = surname;
        this.date_of_birth = date_of_birth;
        this.gender = gender;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.address = address;
    }

    /**
     * Constructor used when username and password are not required.
     *
     * @param userId        user ID
     * @param userType      user type
     * @param firstname     first name
     * @param surname       surname
     * @param date_of_birth date of birth
     * @param gender        gender
     * @param email         email address
     * @param phoneNumber   contact number
     * @param address       address object
     */
    public User(int userId, User_Type userType, String firstname, String surname, LocalDate date_of_birth,
                Gender gender, String email, String phoneNumber, Address address) {
        
        this.userId = userId;
        this.userType = userType;
        this.firstname = firstname;
        this.surname = surname;
        this.date_of_birth = date_of_birth;
        this.gender = gender;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.address = address;
    }

    /**
     * Full constructor used for admin-level access or user management.
     *
     * @param userId        user ID
     * @param username      login username
     * @param password      login password
     * @param userType      user type
     * @param firstname     first name
     * @param surname       surname
     * @param date_of_birth date of birth
     * @param gender        gender
     * @param email         email address
     * @param phoneNumber   contact number
     * @param address       address object
     */
    public User(int userId, String username, String password, User_Type userType, String firstname,
                String surname, LocalDate date_of_birth, Gender gender,
                String email, String phoneNumber, Address address) {

        this.userId = userId;
        this.username = username;
        this.password = password;
        this.userType = userType;
        this.firstname = firstname;
        this.surname = surname;
        this.date_of_birth = date_of_birth;
        this.gender = gender;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.address = address;
    }

    //-----------------------------------------------------------------------
    /** @return the unique ID of the user */
    public int getUserId() {
        return userId;
    }

    /** @param userId sets the unique user ID */
    public void setUserId(int userId) {
        this.userId = userId;
    }

    /** @return the login username */
    public String getUsername() {
        return username;
    }

    /** @param username sets the login username */
    public void setUsername(String username) {
        this.username = username;
    }

    /** @return the login password */
    public String getPassword() {
        return password;
    }

    /** @param password sets the login password */
    public void setPassword(String password) {
        this.password = password;
    }

    /** @return the type of the user (ADMIN or MEMBER) */
    public User_Type getUserType() {
        return userType;
    }

    /** @param userType sets the type of the user */
    public void setUserType(User_Type userType) {
        this.userType = userType;
    }

    /** @return the user's first name */
    public String getFirstname() {
        return firstname;
    }

    /** @param firstname sets the user's first name */
    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    /** @return the user's last name */
    public String getSurname() {
        return surname;
    }

    /** @param surname sets the user's last name */
    public void setSurname(String surname) {
        this.surname = surname;
    }

    /** @return the user's date of birth */
    public LocalDate getDate_of_birth() {
        return date_of_birth;
    }

    /** @param date_of_birth sets the user's date of birth */
    public void setDate_of_birth(LocalDate date_of_birth) {
        this.date_of_birth = date_of_birth;
    }

    /** @return the user's gender */
    public Gender getGender() {
        return gender;
    }

    /** @param gender sets the user's gender */
    public void setGender(Gender gender) {
        this.gender = gender;
    }

    /** @return the user's email address */
    public String getEmail() {
        return email;
    }

    /** @param email sets the user's email address */
    public void setEmail(String email) {
        this.email = email;
    }

    /** @return the user's phone number */
    public String getPhoneNumber() {
        return phoneNumber;
    }

    /** @param phoneNumber sets the user's phone number */
    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    /** @return the user's address */
    public Address getAddress() {
        return address;
    }

    /** @param address sets the user's address */
    public void setAddress(Address address) {
        this.address = address;
    }

    //-----------------------------------------------------------------------
    /**
     * Returns a formatted string representation of the user for display or debugging.
     *
     * @return formatted string with user details
     */
    @Override
    public String toString() {
        return "User Details:\n" +
                "  User ID     : " + userId + "\n" +
                "  Username    : " + username + "\n" +
                "  User Type   : " + userType + "\n" +
                "  First Name  : " + firstname + "\n" +
                "  Last Name   : " + surname + "\n" +
                "  Birth Date  : " + date_of_birth + "\n" +
                "  Gender      : " + gender + "\n" +
                "  Email       : " + email + "\n" +
                "  Phone       : " + phoneNumber + "\n" +
                "  Address     : " + address + "\n";
    }

    /**
     * Compares this user to another object based on user ID.
     *
     * @param object the object to compare
     * @return true if both users have the same user ID
     */
    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;
        User user = (User) object;
        return userId == user.userId;
    }

    /**
     * Generates a hash code based on the user ID.
     *
     * @return hash code of the user
     */
    @Override
    public int hashCode() {
        return Objects.hashCode(userId);
    }
}
