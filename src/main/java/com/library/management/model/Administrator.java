package com.library.management.model;

import java.time.LocalDate;

/**
 * Represents an administrator in the Library Management System.
 * <p>
 * An administrator is a specialized type of user who has additional responsibilities and permissions
 * based on their {@link AdminType} (e.g., SUPER or STANDARD).
 * This class extends the base {@link User} class by adding the {@code adminType} field.
 *
 * <p>Example usage:
 * <pre>
 *     Administrator admin = new Administrator("admin1", "adminPass", User_Type.ADMIN,
 *         "Alice", "Smith", LocalDate.of(1980, 5, 12), Gender.FEMALE,
 *         "alice@example.com", "9998887776", new Address(...), AdminType.SUPER);
 * </pre>
 *
 * @author Fardaws Jawad
 *
 * @see com.library.management.model.User
 * @see com.library.management.model.AdminType
 * @see com.library.management.model.User_Type
 */
public class Administrator extends User {

    /** The type of administrator (e.g., SUPER or STANDARD). */
    private AdminType adminType;

    //-----------------------------------------------------------------------
    /**
     * Default no-argument constructor.
     * <p>
     * Required by frameworks that use reflection or serialization (e.g., JDBC, JPA, Jackson).
     * Initializes an empty {@code Administrator} object with default values.
     */
    public Administrator() {

    }

    /**
     * Constructs a full Administrator with user credentials and personal info.
     * Used when creating a new admin user to be saved in the system.
     *
     * @param username      the login username
     * @param password      the login password
     * @param userType      the type of user (should be ADMIN)
     * @param firstname     first name of the admin
     * @param surname       last name of the admin
     * @param date_of_birth date of birth
     * @param gender        gender of the admin
     * @param email         email address
     * @param phoneNumber   phone number
     * @param address       address of the admin
     * @param adminType     type of administrator (SUPER or STANDARD)
     */
    public Administrator(String username, String password, User_Type userType, String firstname, String surname, LocalDate date_of_birth, Gender gender, String email, String phoneNumber, Address address, AdminType adminType) {
        super(username, password, userType, firstname, surname, date_of_birth, gender, email, phoneNumber, address);
        this.adminType = adminType;
    }

    /**
     * Constructs an Administrator with full user and system information including user ID.
     * Useful when retrieving an admin from the database.
     *
     * @param userId        unique ID of the admin
     * @param username      login username
     * @param userType      user type (ADMIN)
     * @param firstname     first name
     * @param surname       last name
     * @param date_of_birth date of birth
     * @param gender        gender
     * @param email         email address
     * @param phoneNumber   phone number
     * @param address       address
     * @param adminType     admin role (SUPER or STANDARD)
     */
    public Administrator(int userId, String username, User_Type userType, String firstname, String surname, LocalDate date_of_birth, Gender gender, String email, String phoneNumber, Address address, AdminType adminType) {
        super(userId, username, userType, firstname, surname, date_of_birth, gender, email, phoneNumber, address);
        this.adminType = adminType;
    }

    /**
     * Constructs an Administrator without login details.
     * Useful for profile-related views.
     *
     * @param userId        unique ID of the admin
     * @param firstname     first name
     * @param surname       last name
     * @param date_of_birth date of birth
     * @param gender        gender
     * @param email         email address
     * @param phoneNumber   phone number
     * @param address       address
     * @param adminType     admin type
     */
    public Administrator(int userId, String firstname, String surname, LocalDate date_of_birth, Gender gender, String email, String phoneNumber, Address address, AdminType adminType) {
        super(userId, firstname, surname, date_of_birth, gender, email, phoneNumber, address);
        this.adminType = adminType;
    }

    /**
     * Constructs an Administrator with user ID and type, excluding login credentials.
     * Typically used in admin-only restricted actions and admin listings.
     *
     * @param userId        ID of the user
     * @param userType      type of user (ADMIN)
     * @param firstname     first name
     * @param surname       last name
     * @param date_of_birth date of birth
     * @param gender        gender
     * @param email         email
     * @param phoneNumber   phone
     * @param address       address
     * @param adminType     admin type
     */
    public Administrator(int userId, User_Type userType, String firstname, String surname, LocalDate date_of_birth, Gender gender, String email, String phoneNumber, Address address, AdminType adminType) {
        super(userId, userType, firstname, surname, date_of_birth, gender, email, phoneNumber, address);
        this.adminType = adminType;
    }


    /**
     * Constructs an Administrator with complete details including credentials and ID.
     *
     * @param userId        the user ID
     * @param username      login username
     * @param password      login password
     * @param userType      user type
     * @param firstname     first name
     * @param surname       last name
     * @param date_of_birth date of birth
     * @param gender        gender
     * @param email         email
     * @param phoneNumber   phone number
     * @param address       address
     * @param adminType     admin role
     */
    public Administrator(int userId, String username, String password, User_Type userType, String firstname,
                String surname, LocalDate date_of_birth, Gender gender,
                String email, String phoneNumber, Address address, AdminType adminType) {

        super(userId, username, password, userType, firstname, surname,
                date_of_birth, gender, email, phoneNumber, address);
        this.adminType = adminType;
    }


    //-----------------------------------------------------------------------
    /** @return the administrator's type (SUPER or STANDARD) */
    public AdminType getAdminType() {
        return adminType;
    }

    /** @param adminType sets the administrator's type */
    public void setAdminType(AdminType adminType) {
        this.adminType = adminType;
    }


    //-----------------------------------------------------------------------
    /**
     * Returns a string representation of the administrator,
     * including the admin type along with all base user details.
     *
     * @return a formatted string of administrator details
     */
    @Override
    public String toString() {
        return super.toString() +
                "  Admin Type  : " + adminType + "\n";
    }
}
