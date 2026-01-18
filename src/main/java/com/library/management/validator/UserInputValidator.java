package com.library.management.validator;



import com.library.management.model.Gender;
import com.library.management.model.User_Type;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;

/**
 * Utility class that provides validation methods for user-related input fields.
 * <p>
 * This class includes methods to validate user IDs, usernames, passwords, personal details,
 * contact information, gender, and user roles. All methods are static and can be invoked
 * without instantiating the class.
 * </p>
 */
public class UserInputValidator {

    //-----------------------------------------------------------------------
    /**
     * Validates the user ID string to ensure it is a positive integer.
     *
     * @param userIdStr the user ID as a string
     * @return {@code true} if the ID is a valid positive integer; {@code false} otherwise
     */
    public static boolean isValidUserId(String userIdStr) {
        if (userIdStr == null) return false;

        try {
            int userId = Integer.parseInt(userIdStr.trim());
            return userId > 0;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    //-----------------------------------------------------------------------
    /**
     * Validates the username for length and allowed characters.
     * <p>
     * A valid username:
     * <ul>
     *     <li>Is between 3 and 20 characters long</li>
     *     <li>Contains only letters, digits, dots, underscores, hyphens, and @ symbols</li>
     * </ul>
     * </p>
     *
     * @param username the username to validate
     * @return {@code true} if valid; {@code false} otherwise
     */
    public static boolean isValidUsername(String username) {
        if(username == null || username.trim().isEmpty()) return false;

        username = username.trim();

        //Length check
        if(username.length() < 3 || username.length() > 20) return false;

        //Character check
        return username.matches("^[a-zA-Z0-9._@-]+$");
    }

    //-----------------------------------------------------------------------
    /**
     * Validates the password for length and complexity.
     * <p>
     * A valid password:
     * <ul>
     *     <li>Is at least 8 characters long</li>
     *     <li>Contains at least one digit</li>
     *     <li>Contains at least one lowercase letter</li>
     *     <li>Contains at least one uppercase letter</li>
     *     <li>Contains at least one special character (@#$%^&+=!)</li>
     * </ul>
     * </p>
     *
     * @param password the password to validate
     * @return {@code true} if valid; {@code false} otherwise
     */
    public static boolean isValidPassword(String password) {
        if(password == null || password.trim().isEmpty()) return false;

        password = password.trim();

        if(password.length() < 8) return false;

        // At least one digit, one lowercase, one uppercase, one special character
        return password.matches("^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=!]).+$");
    }

    //-----------------------------------------------------------------------
    /**
     * Validates the user type string against {@link User_Type} enum values.
     * <p>
     * Accepts only {@code ADMIN} or {@code MEMBER} (case-insensitive).
     * </p>
     *
     * @param userTypeStr the user type string
     * @return {@code true} if valid; {@code false} otherwise
     */
    public static boolean isValidUserType(String userTypeStr) {
        if(userTypeStr == null || userTypeStr.trim().isEmpty()) return false;

        try {
            User_Type userType = User_Type.valueOf(userTypeStr.trim().toUpperCase());
            return userType.equals(User_Type.ADMIN) || userType.equals(User_Type.MEMBER);
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    //-----------------------------------------------------------------------
    /**
     * Validates the first name to ensure it contains only letters, spaces, or hyphens and is at least 2 characters long.
     *
     * @param firstname the first name
     * @return {@code true} if valid; {@code false} otherwise
     */
    public static boolean isValidFirstName(String firstname) {
        if (firstname == null || firstname.trim().isEmpty()) return false;

        firstname = firstname.trim();

        return firstname.length() >= 2 && firstname.matches("^[A-Za-z\\-\\s]+$");
    }

    //-----------------------------------------------------------------------
    /**
     * Validates the surname to ensure it contains only letters, spaces, or hyphens and is at least 2 characters long.
     *
     * @param surname the surname
     * @return {@code true} if valid; {@code false} otherwise
     */
    public static boolean isValidSurname(String surname) {
        if (surname == null || surname.trim().isEmpty()) return false;

        surname = surname.trim();

        return surname.length() >= 2 && surname.matches("^[A-Za-z\\-\\s]+$");
    }

    //-----------------------------------------------------------------------
    /**
     * Validates the date of birth string in ISO format (YYYY-MM-DD).
     * <p>
     * Accepts birth dates between 5 and 120 years ago.
     * </p>
     *
     * @param dateOfBirthStr the date of birth as a string
     * @return {@code true} if within valid age range; {@code false} otherwise
     */
    public static boolean isValidDateOfBirth(String dateOfBirthStr) {
        if (dateOfBirthStr == null || dateOfBirthStr.trim().isEmpty()) return false;

        try {
            LocalDate dateOfBirth = LocalDate.parse(dateOfBirthStr);
            LocalDate today = LocalDate.now();

            LocalDate earliestValid = today.minusYears(120);
            LocalDate latestValid = today.minusYears(5);

            return !dateOfBirth.isAfter(latestValid) &&
                    !dateOfBirth.isBefore(earliestValid);
        } catch (DateTimeParseException e) {
            return false;
        }
    }

    //-----------------------------------------------------------------------
    /**
     * Validates the gender string against {@link Gender} enum values.
     * <p>
     * Accepts only {@code MALE} or {@code FEMALE} (case-insensitive).
     * </p>
     *
     * @param genderStr the gender string
     * @return {@code true} if valid; {@code false} otherwise
     */
    public static boolean isValidGender(String genderStr) {
        if (genderStr == null || genderStr.trim().isEmpty()) return false;

        try {
            Gender gender = Gender.valueOf(genderStr.trim().toUpperCase());
            return gender.equals(Gender.MALE) || gender.equals(Gender.FEMALE);
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    //-----------------------------------------------------------------------
    /**
     * Validates the email format.
     *
     * @param email the email address
     * @return {@code true} if the email matches standard email pattern; {@code false} otherwise
     */
    public static boolean isValidEmail(String email) {
        if (email == null || email.trim().isEmpty()) return false;

        String emailPatter = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$";
        return email.trim().matches(emailPatter);
    }

    //-----------------------------------------------------------------------
    /**
     * Validates the phone number format.
     * <p>
     * A valid phone number contains 10 to 15 digits and may optionally begin with a '+'.
     * </p>
     *
     * @param phoneNumber the phone number string
     * @return {@code true} if valid; {@code false} otherwise
     */
    public static boolean isValidPhoneNumber(String phoneNumber) {
        if (phoneNumber == null || phoneNumber.trim().isEmpty()) return false;

        String phonePattern = "^\\+?[0-9]{10,15}$";
        return phoneNumber.trim().matches(phonePattern);
    }

}
