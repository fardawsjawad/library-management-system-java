package com.library.management.presentation;

import com.library.management.model.Address;
import com.library.management.model.Gender;
import com.library.management.model.Member;
import com.library.management.model.User_Type;
import com.library.management.service.UserServiceImpl;
import com.library.management.validator.AddressInputValidator;
import com.library.management.validator.MemberInputValidator;

import java.time.LocalDate;
import java.util.Scanner;

/**
 * The {@code SignUpMenu} class is part of the presentation layer of the
 * Library Management System. It provides a console-based interface for
 * registering new members into the system.
 *
 * <p>
 * This class is responsible for:
 * </p>
 * <ul>
 *   <li>Collecting user input for credentials, personal details, and address</li>
 *   <li>Validating input using the {@code MemberInputValidator} and {@code AddressInputValidator}</li>
 *   <li>Constructing a {@link Member} object from validated input</li>
 *   <li>Delegating user registration to the {@link UserServiceImpl} in the service layer</li>
 * </ul>
 *
 * <p>
 * If validation fails at any step, the signup process is halted and
 * a relevant message is displayed.
 * </p>
 *
 * <p><strong>Dependencies:</strong></p>
 * <ul>
 *   <li>{@code Scanner} — for capturing user input</li>
 *   <li>{@code UserServiceImpl} — to handle user registration logic</li>
 * </ul>
 *
 * @author Fardaws Jawad
 */
public class SignUpMenu {

    /** Scanner object used to capture user input from the console during signup. */
    private final Scanner scanner;

    /** Service layer implementation used to handle business logic for user registration. */
    private final UserServiceImpl userService;

    //-----------------------------------------------------------------------
    /**
     * Constructs a {@code SignUpMenu} with the provided {@code Scanner} for reading input.
     * <p>
     * Initializes the service layer dependency responsible for persisting user data.
     * </p>
     *
     * @param scanner the {@code Scanner} object used to capture input from the user
     */
    public SignUpMenu(Scanner scanner) {
        this.scanner = scanner;
        this.userService = new UserServiceImpl();
    }

    //-----------------------------------------------------------------------
    /**
     * Handles the signup process for a new library member.
     * <p>
     * This method collects and validates user input including credentials,
     * personal details, and address information. It uses the {@link MemberInputValidator}
     * class for input validation and then constructs a {@link Member} object.
     * If validation is successful, the new member is registered using the
     * {@link UserServiceImpl}.
     * </p>
     * <p>
     * Validation failure at any step halts the registration process and displays
     * an appropriate error message.
     * </p>
     */
    public void signUpNewMember() {
        System.out.println("\n--- Add New Member ---");

        // Prompt and validate username
        System.out.print("Enter username: ");
        String username = scanner.nextLine();
        if (!MemberInputValidator.isValidUsername(username)) {
            System.out.println("Invalid username format.");
            return;
        }

        // Prompt and validate password
        System.out.print("Enter password: ");
        String password = scanner.nextLine();
        if (!MemberInputValidator.isValidPassword(password)) {
            System.out.println("Password does not meet the requirements.");
            return;
        }

        // Prompt and validate first name
        System.out.print("Enter firstname: ");
        String firstname = scanner.nextLine();
        if (!MemberInputValidator.isValidFirstName(firstname)) {
            System.out.println("Invalid firstname. Firstname must be alphabetic and non-empty.");
            return;
        }

        // Prompt and validate surname
        System.out.print("Enter surname: ");
        String surname = scanner.nextLine();
        if (!MemberInputValidator.isValidSurname(surname)) {
            System.out.println("Invalid surname. Last name must be alphabetic and non-empty.");
            return;
        }

        // Prompt and validate date of birth
        System.out.print("Enter date of birth in (YYYY-MM-DD) format: ");
        String dateOfBirthStr = scanner.nextLine();
        if (!MemberInputValidator.isValidDateOfBirth(dateOfBirthStr)) {
            System.out.println("Invalid date format. Please enter in YYYY-MM-DD format.");
            return;
        }

        // Prompt and validate gender
        System.out.print("Enter gender (male, female): ");
        String genderStr = scanner.nextLine();
        if (!MemberInputValidator.isValidGender(genderStr)) {
            System.out.println("Invalid gender. Must be Male or Female.");
            return;
        }

        // Prompt and validate email
        System.out.print("Enter email address (example@123.com): ");
        String email = scanner.nextLine();
        if (!MemberInputValidator.isValidEmail(email)) {
            System.out.println("Invalid email format. Email must be in \"example@123.com\" format.");
            return;
        }

        // Prompt and validate phone number
        System.out.print("Enter phone number: ");
        String phoneNumber = scanner.nextLine();
        if (!MemberInputValidator.isValidPhoneNumber(phoneNumber)) {
            System.out.println("Invalid phone number. Phone number must be 10 to 15 digits with an option + in the beginning.");
            return;
        }

        // Prompt for address and validate it using helper method
        Address address = takeAddressInput();

        // Proceed only if address is valid
        if (address != null) {
            try {
                // Convert string values to proper data types
                LocalDate dateOfBirth = LocalDate.parse(dateOfBirthStr);
                Gender gender = Gender.valueOf(genderStr.toUpperCase());

                // Create the new Member object
                Member member = new Member(
                        username, password, User_Type.MEMBER, firstname, surname,
                        dateOfBirth, gender, email, phoneNumber,
                        address
                );

                // Register user through service layer
                boolean userAdded = userService.registerUser(member);
                if (userAdded) {
                    System.out.println("You have been successfully registered.\n");
                }

            } catch (IllegalArgumentException illegalArgumentException) {
                System.out.println("Error: " + illegalArgumentException.getMessage());
            } catch (Exception e) {
                System.out.println("Failed to convert DOB and Gender values.");
            }

        }
    }

    //-----------------------------------------------------------------------
    /**
     * Collects and validates address information from the user.
     * <p>
     * This method prompts the user for street, city, pincode, state, and country.
     * Each input is validated using the {@link AddressInputValidator}. If any input
     * fails validation, an appropriate message is shown and the method returns {@code null}.
     * </p>
     *
     * @return a valid {@link Address} object if all fields are correct; {@code null} otherwise
     */
    private Address takeAddressInput() {
        System.out.println("\nAddress Details: ");

        // Street input and validation
        System.out.print("Enter street: ");
        String street = scanner.nextLine();
        if(!AddressInputValidator.isValidStreet(street)) {
            System.out.println("Invalid street. Please provide a valid street.");
            return null;
        }

        // City input and validation
        System.out.print("Enter city: ");
        String city = scanner.nextLine();
        if(!AddressInputValidator.isValidCity(city)) {
            System.out.println("Invalid city. Please provide a valid city.");
            return null;
        }

        // Pincode input and validation
        System.out.print("Enter pincode: ");
        String pincode = scanner.nextLine();
        if(!AddressInputValidator.isValidPincode(pincode)) {
            System.out.println("Invalid pincode. Please provide a valid pincode.");
            return null;
        }

        // State input and validation
        System.out.print("Enter state: ");
        String state = scanner.nextLine();
        if(!AddressInputValidator.isValidState(state)) {
            System.out.println("Invalid state. Please provide a valid state.");
            return null;
        }

        // Country input and validation
        System.out.print("Enter country: ");
        String country = scanner.nextLine();
        if(!AddressInputValidator.isValidCountry(country)){
            System.out.println("Invalid country.");
            return null;
        }

        // Return validated Address object
        return new Address(street, city, pincode, state, country);
    }

}
