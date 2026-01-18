package com.library.management.operation;

import com.library.management.exception.SuperAdminException;
import com.library.management.exception.UserNotFoundException;
import com.library.management.model.*;
import com.library.management.service.BookServiceImpl;
import com.library.management.service.TransactionServiceImpl;
import com.library.management.service.UserServiceImpl;
import com.library.management.validator.AddressInputValidator;
import com.library.management.validator.AdministratorInputValidator;
import com.library.management.validator.MemberInputValidator;
import com.library.management.validator.UserInputValidator;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.Scanner;

import static java.lang.System.exit;

/**
 * Handles all user-related operations within the library management system.
 * <p>
 * This class serves as the interface between the user and the system's backend
 * services for managing members, administrators, and general user accounts.
 * It interacts with the user via the console to capture input and perform
 * validations before passing data to the appropriate service layers.
 * </p>
 *
 * <p><b>Main functionalities include:</b></p>
 * <ul>
 *     <li>Registering new members and admins</li>
 *     <li>Finding users by ID or username</li>
 *     <li>Viewing all users, members, or admins</li>
 *     <li>Updating user details including password, username, and user type</li>
 *     <li>Removing users</li>
 *     <li>Viewing user types and borrowing history</li>
 *     <li>Allowing members to update their own profile information</li>
 * </ul>
 *
 * <p>
 * This class depends on {@link UserServiceImpl}, {@link BookServiceImpl},
 * and {@link TransactionServiceImpl} to delegate business logic. It also
 * uses a {@link Scanner} instance to collect user input from the console.
 * </p>
 *
 * @author Fardaws Jawad
 */
public class UserOperations {

    // Dependencies used across user operations
    private final Scanner scanner;                              // For reading user input
    private final UserServiceImpl userService;                  // Service to handle user-related business logic
    private final BookServiceImpl bookService;                  // Service to retrieve book-related information
    private final TransactionServiceImpl transactionService;    // Service to manage borrowing and returning transactions

    //-----------------------------------------------------------------------
    /**
     * Constructs a {@code UserOperations} instance and initializes required services.
     * <p>
     * This constructor sets up the operational layer for user-specific interactions
     * in the Library Management System. It receives a {@code Scanner} instance for
     * interactive input, and instantiates services to delegate business logic.
     * </p>
     *
     * @param scanner the {@code Scanner} object used for reading user input from the console
     */
    public UserOperations(Scanner scanner) {
        this.scanner = scanner;
        this.userService = new UserServiceImpl();
        this.bookService = new BookServiceImpl();
        this.transactionService = new TransactionServiceImpl();
    }

    //-----------------------------------------------------------------------
    /**
     * Adds a new library member to the system.
     * <p>
     * This method collects input from the user through the console for all required
     * member details, including personal information and address. It validates each
     * input field using {@code MemberInputValidator}. If all inputs are valid, the
     * data is wrapped into a {@code Member} object and passed to {@code UserServiceImpl}
     * for registration.
     * </p>
     *
     * <p><strong>Input Fields:</strong></p>
     * <ul>
     *     <li>Username</li>
     *     <li>Password</li>
     *     <li>First name</li>
     *     <li>Surname</li>
     *     <li>Date of Birth</li>
     *     <li>Gender</li>
     *     <li>Email</li>
     *     <li>Phone number</li>
     *     <li>Address (via helper method {@code takeAddressInput()})</li>
     * </ul>
     *
     * <p><strong>Validation:</strong> All user inputs are strictly validated before proceeding.</p>
     * <p><strong>Exceptions Handled:</strong></p>
     * <ul>
     *     <li>{@code IllegalArgumentException} – for invalid enum or value parsing</li>
     *     <li>{@code Exception} – for general failures in conversion (DOB/Gender)</li>
     * </ul>
     */
    public void addNewMember() {
        System.out.println("\n--- Add New Member ---");

        // Prompt for username
        System.out.print("Enter username: ");
        String username = scanner.nextLine();
        if (!MemberInputValidator.isValidUsername(username)) {
            System.out.println("Invalid username format.");
            return;
        }

        // Prompt for password
        System.out.print("Enter password: ");
        String password = scanner.nextLine();
        if (!MemberInputValidator.isValidPassword(password)) {
            System.out.println("Password does not meet the requirements.");
            return;
        }

        // Prompt for first name
        System.out.print("Enter firstname: ");
        String firstname = scanner.nextLine();
        if (!MemberInputValidator.isValidFirstName(firstname)) {
            System.out.println("Invalid firstname. Firstname must be alphabetic and non-empty.");
            return;
        }

        // Prompt for surname
        System.out.print("Enter surname: ");
        String surname = scanner.nextLine();
        if (!MemberInputValidator.isValidSurname(surname)) {
            System.out.println("Invalid surname. Last name must be alphabetic and non-empty.");
            return;
        }

        // Prompt for date of birth
        System.out.print("Enter date of birth in (YYYY-MM-DD) format: ");
        String dateOfBirthStr = scanner.nextLine();
        if (!MemberInputValidator.isValidDateOfBirth(dateOfBirthStr)) {
            System.out.println("Invalid date format. Please enter in YYYY-MM-DD format.");
            return;
        }

        // Prompt for gender
        System.out.print("Enter gender (male, female): ");
        String genderStr = scanner.nextLine();
        if (!MemberInputValidator.isValidGender(genderStr)) {
            System.out.println("Invalid gender. Must be Male or Female.");
            return;
        }

        // Prompt for email
        System.out.print("Enter email address (example@123.com): ");
        String email = scanner.nextLine();
        if (!MemberInputValidator.isValidEmail(email)) {
            System.out.println("Invalid email format. Email must be in \"example@123.com\" format.");
            return;
        }

        // Prompt for phone number
        System.out.print("Enter phone number: ");
        String phoneNumber = scanner.nextLine();
        if (!MemberInputValidator.isValidPhoneNumber(phoneNumber)) {
            System.out.println("Invalid phone number. Phone number must be 10 to 15 digits with an option + in the beginning.");
            return;
        }

        // Collect address details from user using helper method
        Address address = takeAddressInput();

        if (address != null) {
            try {
                // Convert and validate date and gender input
                LocalDate dateOfBirth = LocalDate.parse(dateOfBirthStr);
                Gender gender = Gender.valueOf(genderStr.toUpperCase());

                // Create a new Member object with the collected data
                Member member = new Member(
                        username, password, User_Type.MEMBER, firstname, surname,
                        dateOfBirth, gender, email, phoneNumber,
                        address
                );

                // Register the member using the user service
                boolean userAdded = userService.registerUser(member);
                if (userAdded) {
                    System.out.println("Member successfully added.\n");
                }

            } catch (IllegalArgumentException illegalArgumentException) {
                // Handle incorrect enum conversion or invalid values
                System.out.println("Error: " + illegalArgumentException.getMessage());
            } catch (Exception e) {
                // Handle other parsing or logic exceptions
                System.out.println("Failed to convert DOB and Gender values.");
            }

        }
    }

    //-----------------------------------------------------------------------
    /**
     * Adds a new administrator to the system.
     * <p>
     * This operation is restricted to super administrators only. It prompts the super admin
     * for all required input fields, validates them using the {@code MemberInputValidator},
     * constructs a {@code Administrator} object, and registers the new admin via the {@code UserServiceImpl}.
     * </p>
     *
     * @param administrator the currently logged-in administrator (must be of type SUPER)
     */
    public void addNewAdmin(Administrator administrator) {
        // Check if the requesting admin has SUPER privileges
        if (!administrator.getAdminType().equals(AdminType.SUPER)) {
            System.out.println("Only super admin can add a new admin to the system.\n");
            return;
        }

        System.out.println("\n--- Add New Admin ---");

        // ----------- Step 1: Take and validate admin input -----------

        System.out.print("Enter username: ");
        String username = scanner.nextLine();
        if (!MemberInputValidator.isValidUsername(username)) {
            System.out.println("Invalid username format.");
            return;
        }

        System.out.print("Enter password: ");
        String password = scanner.nextLine();
        if (!MemberInputValidator.isValidPassword(password)) {
            System.out.println("Password does not meet the requirements.");
            return;
        }

        System.out.print("Enter firstname: ");
        String firstname = scanner.nextLine();
        if (!MemberInputValidator.isValidFirstName(firstname)) {
            System.out.println("Invalid firstname. Firstname must be alphabetic and non-empty.");
            return;
        }

        System.out.print("Enter surname: ");
        String surname = scanner.nextLine();
        if (!MemberInputValidator.isValidSurname(surname)) {
            System.out.println("Invalid surname. Last name must be alphabetic and non-empty.");
            return;
        }

        System.out.print("Enter date of birth in (YYYY-MM-DD) format: ");
        String dateOfBirthStr = scanner.nextLine();
        if (!MemberInputValidator.isValidDateOfBirth(dateOfBirthStr)) {
            System.out.println("Invalid date format. Please enter in YYYY-MM-DD format.");
            return;
        }

        System.out.print("Enter gender (male, female): ");
        String genderStr = scanner.nextLine();
        if (!MemberInputValidator.isValidGender(genderStr)) {
            System.out.println("Invalid gender. Must be Male or Female.");
            return;
        }

        System.out.print("Enter email address (example@123.com): ");
        String email = scanner.nextLine();
        if (!MemberInputValidator.isValidEmail(email)) {
            System.out.println("Invalid email format. Email must be in \"example@123.com\" format.");
            return;
        }

        System.out.print("Enter phone number: ");
        String phoneNumber = scanner.nextLine();
        if (!MemberInputValidator.isValidPhoneNumber(phoneNumber)) {
            System.out.println("Invalid phone number. Phone number must be 10 to 15 digits with an option + in the beginning.");
            return;
        }

        // ----------- Step 2: Collect address using helper method -----------

        Address address = takeAddressInput(); // Prompts for and validates address details

        if (address != null) {
            try {
                // Convert string inputs to appropriate enum/date types
                LocalDate dateOfBirth = LocalDate.parse(dateOfBirthStr);
                Gender gender = Gender.valueOf(genderStr.toUpperCase());

                // Create a new Administrator object with default type STANDARD
                Administrator administratorToAdd = new Administrator(
                        username, password, User_Type.ADMIN, firstname, surname,
                        dateOfBirth, gender, email, phoneNumber,
                        address, AdminType.STANDARD
                );

                // Register the admin using the user service
                boolean userAdded = userService.registerUser(administratorToAdd);
                if (userAdded) {
                    System.out.println("Admin successfully added.\n");
                }

            } catch (IllegalArgumentException illegalArgumentException) {
                // Handle incorrect enum values or invalid inputs
                System.out.println("Error: " + illegalArgumentException.getMessage());

            } catch (Exception e) {
                // Handle any parsing or unexpected runtime errors
                System.out.println("Failed to convert DOB and Gender values.");
            }

        }
    }

    //-----------------------------------------------------------------------
    /**
     * Finds and displays a user by their user ID.
     * <p>
     * Prompts the admin to input a user ID, validates it, and retrieves the corresponding
     * {@code User} object using the {@code UserServiceImpl}. Displays the user's details
     * if found; otherwise, displays appropriate error messages.
     * </p>
     */
    public void findUserByUserId() {
        System.out.println("\n--- Find User by User ID ---");

        // Prompt for user ID
        System.out.print("Enter user ID: ");
        String userIdStr = scanner.nextLine();

        // Validate user ID format
        if (!UserInputValidator.isValidUserId(userIdStr)) {
            System.out.println("Invalid user ID. User ID must be a positive number.");
            return;
        }

        try {
            // Parse the user ID to integer
            int userId = Integer.parseInt(userIdStr);

            // Retrieve the user by ID from the service layer
            User user = userService.getUserById(userId);

            // Display the user details
            System.out.println(user + "\n");

        } catch (UserNotFoundException userNotFoundException) {
            // Handle case where user does not exist
            System.out.println(userNotFoundException.getMessage() + "\n");

        } catch (IllegalArgumentException illegalArgumentException) {
            // Handle parsing errors or invalid data access
            System.out.println("Error: " + illegalArgumentException.getMessage());
        }
    }

    //-----------------------------------------------------------------------
    /**
     * Finds and displays a user by their username.
     * <p>
     * Prompts the admin to enter a username and retrieves the corresponding
     * {@code User} object using the {@code UserServiceImpl}. If the user exists,
     * their details are displayed; otherwise, appropriate error messages are shown.
     * </p>
     */
    public void findUserByUsername() {
        System.out.println("\n--- Find User by Username ---");

        // Prompt for username input
        System.out.print("Enter username: ");
        String username = scanner.nextLine();

        try {
            // Retrieve the user by username from the service layer
            User user = userService.getUserByUsername(username);

            // Display the user details
            System.out.println(user + "\n");

        } catch (UserNotFoundException userNotFoundException) {
            // Handle case where user does not exist
            System.out.println(userNotFoundException.getMessage() + "\n");

        } catch (IllegalArgumentException illegalArgumentException) {
            // Handle invalid arguments or internal service errors
            System.out.println("Error: " + illegalArgumentException.getMessage());
        }
    }

    //-----------------------------------------------------------------------
    /**
     * Displays all registered members in the library system.
     * <p>
     * Retrieves a list of all {@code Member} objects using the {@code UserServiceImpl},
     * sorts them alphabetically by surname and then firstname, and prints the member list.
     * If no members exist, an appropriate message is displayed.
     * </p>
     */
    public void viewAllMembers() {
        // Retrieve all members from the service layer
        List<Member> members = userService.getAllMembers();

        if (!members.isEmpty()) {
            // Sort members alphabetically by surname, then firstname
            members.sort(Comparator.comparing(Member::getSurname)
                    .thenComparing(Member::getFirstname));

            // Display sorted list of members
            System.out.println("\n--- Members in the Library ---");
            members.forEach(System.out::println);
            System.out.println();
        } else {
            // Handle case when no members are registered
            System.out.println("No members exist in the library.\n");
        }
    }

    //-----------------------------------------------------------------------
    /**
     * Displays all registered administrators in the system.
     * <p>
     * Retrieves a list of all {@code Administrator} objects from the {@code UserServiceImpl},
     * sorts them alphabetically by surname and then firstname, and prints each administrator’s details.
     * If no administrators are found, a message is displayed.
     * </p>
     */
    public void viewAllAdmins() {
        // Retrieve all administrators from the service layer
        List<Administrator> administrators = userService.getAllAdmins();

        if (!administrators.isEmpty()) {
            // Sort administrators by surname, then firstname
            administrators.sort(Comparator.comparing(Administrator::getSurname)
                                    .thenComparing(Administrator::getFirstname));

            // Display sorted list of administrators
            System.out.println("\n--- Admins in the System ---");
            administrators.forEach(System.out::println);
            System.out.println();
        } else {
            // Handle case when no administrators are registered
            System.out.println("No admins exist in the system.\n");
        }
    }

    //-----------------------------------------------------------------------
    /**
     * Displays all users registered in the library system, including members and administrators.
     * <p>
     * Retrieves all {@code User} objects using the {@code UserServiceImpl}, sorts them
     * alphabetically by surname and then firstname, and prints each user's details.
     * If no users are found, an appropriate message is displayed.
     * </p>
     */
    public void viewAllUsers() {
        // Retrieve all users from the service layer
        List<User> userList = userService.getAllUsers();

        if (!userList.isEmpty()) {
            // Sort users by surname, then firstname
            userList.sort(Comparator.comparing(User::getSurname)
                    .thenComparing(User::getFirstname));

            // Display the sorted list of users
            System.out.println("\n--- All Users in the Library ---");
            userList.forEach(System.out::println);
            System.out.println();
        } else {
            // Handle case when no users are registered
            System.out.println("No users exist in the library.\n");
        }
    }

    //-----------------------------------------------------------------------
    /**
     * Updates the details of an existing user in the system.
     * <p>
     * Prompts the admin to enter a valid user ID, then collects updated details such as firstname, surname,
     * date of birth, gender, email, phone number, and address. It validates each input before constructing
     * a new {@code User} object with the updated values and passing it to the service layer for persistence.
     * </p>
     */
    public void updateUser() {
        System.out.println("\n--- Update User ---");

        // Prompt for user ID and validate it
        System.out.print("Enter user ID: ");
        String userIdStr = scanner.nextLine();
        if (!UserInputValidator.isValidUserId(userIdStr)) {
            System.out.println("Invalid user ID.");
            return;
        }

        int userId = Integer.parseInt(userIdStr);

        // Check if the user exists in the system
        try {
            if (!userService.userExists(userId)) {
                System.out.println("User not found.\n");
                return;
            }
        } catch (IllegalArgumentException e) {
            System.out.println("Error: " + e.getMessage());
        }

        // Collect and validate updated user information
        System.out.print("Enter firstname: ");
        String firstname = scanner.nextLine();
        if (!UserInputValidator.isValidFirstName(firstname)) {
            System.out.println("Invalid firstname. Firstname must be alphabetic and non-empty.");
            return;
        }

        System.out.print("Enter surname: ");
        String surname = scanner.nextLine();
        if (!UserInputValidator.isValidSurname(surname)) {
            System.out.println("Invalid surname. Last name must be alphabetic and non-empty.");
            return;
        }

        System.out.print("Enter date of birth in (YYYY-MM-DD) format: ");
        String dateOfBirthStr = scanner.nextLine();
        if (!UserInputValidator.isValidDateOfBirth(dateOfBirthStr)) {
            System.out.println("Invalid date format. Please enter in YYYY-MM-DD format.");
            return;
        }

        System.out.print("Enter gender (male, female): ");
        String genderStr = scanner.nextLine();
        if (!MemberInputValidator.isValidGender(genderStr)) {
            System.out.println("Invalid gender. Must be Male or Female.");
            return;
        }

        System.out.print("Enter email address (example@123.com): ");
        String email = scanner.nextLine();
        if (!MemberInputValidator.isValidEmail(email)) {
            System.out.println("Invalid email format. Email must be in \"example@123.com\" format.");
            return;
        }

        System.out.print("Enter phone number: ");
        String phoneNumber = scanner.nextLine();
        if (!MemberInputValidator.isValidPhoneNumber(phoneNumber)) {
            System.out.println("Invalid phone number. Phone number must be 10 to 15 digits with an option + in the beginning.");
            return;
        }

        // Collect and validate address input
        Address address = takeAddressInput();

        if (address != null) {
            try {
                // Convert date and gender to appropriate types
                LocalDate dateOfBirth = LocalDate.parse(dateOfBirthStr);
                Gender gender = Gender.valueOf(genderStr.toUpperCase());

                // Construct updated User object
                User user = new User(
                        userId, firstname, surname,
                        dateOfBirth, gender, email,
                        phoneNumber, address
                );

                // Attempt to update user via service layer
                boolean userUpdated = userService.updateUser(user);

                if (userUpdated) {
                    System.out.println("User details successfully updated.\n");
                } else {
                    System.out.println("User details could not be updated.\n");
                }

            } catch (UserNotFoundException userNotFoundException) {
                System.out.println(userNotFoundException.getMessage() + "\n");
            } catch (IllegalArgumentException illegalArgumentException) {
                System.out.println("Error: " + illegalArgumentException.getMessage());
            } catch (Exception e) {
                System.out.println("Failed to convert DOB, Gender, or User ID values.");
            }
        }
    }

    //-----------------------------------------------------------------------
    /**
     * Updates the username of a user in the system.
     * <p>
     * Prompts the admin to enter a valid user ID and then a new username. It validates both inputs and
     * delegates the update operation to the {@code UserService}. Provides appropriate messages based
     * on the outcome.
     * </p>
     */
    public void updateUsername() {
        System.out.println("\n--- Update Username ---");

        // Prompt for user ID and validate it
        System.out.print("Enter user ID: ");
        String userIdStr = scanner.nextLine();
        if (!UserInputValidator.isValidUserId(userIdStr)) {
            System.out.println("Invalid user ID.");
            return;
        }

        int userId = Integer.parseInt(userIdStr);

        // Check if user exists
        try {
            if (!userService.userExists(userId)) {
                System.out.println("User not found.\n");
                return;
            }
        } catch (IllegalArgumentException e) {
            System.out.println("Error: " + e.getMessage());
        }

        // Prompt for and validate new username
        System.out.print("Enter username: ");
        String username = scanner.nextLine();
        if (!UserInputValidator.isValidUsername(username)) {
            System.out.println("Invalid username format.");
            return;
        }

        // Attempt to update username through service layer
        try {

            boolean usernameUpdated = userService.updateUsername(userId, username);

            if (usernameUpdated) {
                System.out.println("Username updated successfully.\n");
            } else {
                System.out.println("Username could not be updated.\n");
            }

        } catch (UserNotFoundException userNotFoundException) {
            System.out.println(userNotFoundException.getMessage() + "\n");
        } catch (IllegalArgumentException illegalArgumentException) {
            System.out.println("Error: " + illegalArgumentException.getMessage());
        }
    }

    //-----------------------------------------------------------------------
    /**
     * Updates the password of a user in the system.
     * <p>
     * Prompts the admin to enter a user ID and a new password, validates both inputs,
     * and updates the password using the {@code UserService}. Displays appropriate
     * success or error messages based on the outcome.
     * </p>
     */
    public void updatePassword() {
        System.out.println("\n--- Update Password ---");

        // Prompt for and validate user ID
        System.out.print("Enter user ID: ");
        String userIdStr = scanner.nextLine();
        if (!UserInputValidator.isValidUserId(userIdStr)) {
            System.out.println("Invalid user ID.\n");
            return;
        }

        int userId = Integer.parseInt(userIdStr);

        // Check if user exists in the system
        try {
            if (!userService.userExists(userId)) {
                System.out.println("User not found.\n");
                return;
            }
        } catch (IllegalArgumentException illegalArgumentException) {
            System.out.println("Error: " + illegalArgumentException.getMessage());
        }

        // Prompt for and validate the new password
        System.out.print("Enter new password: ");
        String password = scanner.nextLine();
        if (!UserInputValidator.isValidPassword(password)) {
            System.out.println("Password does not meet the requirements.");
            return;
        }

        // Attempt to update the password using the service layer
        try {
            boolean passwordUpdated = userService.updatePassword(userId, password);

            if (passwordUpdated) {
                System.out.println("Password successfully updated.\n");
            } else {
                System.out.println("Password could not be updated.\n");
            }

        } catch (UserNotFoundException userNotFoundException) {
            System.out.println(userNotFoundException.getMessage() + "\n");
        } catch (IllegalArgumentException illegalArgumentException) {
            System.out.println("Error: " + illegalArgumentException.getMessage());
        }
    }

    //-----------------------------------------------------------------------
    /**
     * Updates the user_type of a specified user in the system (e.g., from MEMBER to ADMIN).
     * <p>
     * This operation is restricted to users with SUPER admin privileges.
     * The method prompts for the user ID and the new user type,
     * validates them, and updates the user type using the {@code UserService}.
     * </p>
     *
     * @param administrator The currently logged-in administrator attempting to perform the update.
     */
    public void updateUserType(Administrator administrator) {
        // Ensure only SUPER admin can update user types
        if (!administrator.getAdminType().equals(AdminType.SUPER)) {
            System.out.println("Only super admin can change the user type of a user.");
            return;
        }

        System.out.println("\n--- Update User Type ---");

        // Prompt and validate user ID
        System.out.print("Enter user ID: ");
        String userIdStr = scanner.nextLine();
        if (!UserInputValidator.isValidUserId(userIdStr)) {
            System.out.println("Invalid user ID.");
            return;
        }

        int userId = Integer.parseInt(userIdStr);

        // Verify if the user exists
        try {
            if(!userService.userExists(userId)) {
                System.out.println("User not found.\n");
                return;
            };
        } catch (IllegalArgumentException e) {
            System.out.println("Error: " + e.getMessage());
        }

        // Prompt and validate new user type
        System.out.print("Enter user type (Admin, Member): ");
        String userTypeStr = scanner.nextLine();
        if (!UserInputValidator.isValidUserType(userTypeStr)) {
            System.out.println("Invalid user type choose (Admin or Member).\n");
            return;
        }

        // Attempt to update user types
        try {

            User_Type userType = User_Type.valueOf(userTypeStr.toUpperCase());
            boolean userTypeUpdated = userService.updateUserType(userId, userType);

            if (userTypeUpdated) {
                System.out.println("User type successfully updated.\n");
            } else {
                System.out.println("User type could not be updated.\n");
            }

        } catch (UserNotFoundException | IllegalArgumentException e) {
            System.out.println(e.getMessage());
        } catch (Exception e) {
            System.out.println("Failed to convert user ID or User Type.\n");
        }
    }

    //-----------------------------------------------------------------------
    /**
     * Updates the type of an existing admin (e.g., from STANDARD to SUPER).
     * <p>
     * This operation is restricted to SUPER administrators only.
     * The method prompts for the admin's user ID and the new admin type,
     * validates the input, and then attempts to update the admin type
     * through the {@code UserService}.
     * </p>
     *
     * @param administrator The currently logged-in administrator attempting the update.
     */
    public void updateAdminType(Administrator administrator) {
        // Check if the current admin has SUPER privileges
        if (!administrator.getAdminType().equals(AdminType.SUPER)) {
            System.out.println("Only super admin can update an admin type.\n");
            return;
        }

        System.out.println("\n--- Update Admin Type");

        // Prompt and validate admin ID
        System.out.print("Enter admin ID: ");
        String userIdStr = scanner.nextLine();
        if (!UserInputValidator.isValidUserId(userIdStr)) {
            System.out.println("Invalid user ID.\n");
            return;
        }

        int userId = Integer.parseInt(userIdStr);

        // Check if the admin exists
        try {
            if (!userService.userExists(userId)) {
                System.out.println("Admin with the ID " + userId + " does not exist.");
                return;
            }
        } catch (IllegalArgumentException e) {
            System.out.println("Error: " + e.getMessage());
        }

        // Prompt and validate the new admin type
        System.out.print("Enter admin type (Super, Standard): ");
        String adminTypeStr = scanner.nextLine();
        if (!AdministratorInputValidator.isValidAdminType(adminTypeStr)) {
            System.out.println("Invalid admin type, enter (Super or Standard).\n");
            return;
        }

        // Attempt to update the admin type
        try {
            AdminType adminType = AdminType.valueOf(adminTypeStr.toUpperCase());

            boolean adminUpdated = userService.updateAdminType(userId, adminType);

            if (adminUpdated) {
                System.out.println("Admin type successfully updated.\n");
            } else {
                System.out.println("Admin type could not be updated.\n");
            }
        } catch (UserNotFoundException | IllegalArgumentException runtimeException) {
            System.out.println(runtimeException.getMessage() + "\n");
        } catch (Exception e) {
            System.out.println("Failed to convert user ID or admin type.\n");
        }
    }

    //-----------------------------------------------------------------------
    /**
     * Removes a user from the system.
     * <p>
     * Prompts the admin for a user ID, validates the input, confirms the existence of the user,
     * and asks for a final confirmation before proceeding with deletion. This method ensures
     * that critical users (like the Super Admin) cannot be removed accidentally or maliciously.
     * </p>
     *
     * Handles input validation, user existence checks, and business rule exceptions such as
     * {@code UserNotFoundException} and {@code SuperAdminException}.
     */
    public void removeUser() {
        System.out.println("\n--- Remove User ---");

        // Prompt for user ID input
        System.out.print("Enter user ID: ");
        String userIdStr = scanner.nextLine();
        if (!UserInputValidator.isValidUserId(userIdStr)) {
            System.out.println("Invalid user ID.\n");
            return;
        }

        int userId = Integer.parseInt(userIdStr);

        // Check if the user exists
        try {
            if (!userService.userExists(userId)) {
                System.out.println("User not found.\n");
                return;
            }
        } catch (IllegalArgumentException illegalArgumentException) {
            System.out.println("Error: " + illegalArgumentException.getMessage());
        }

        User user = userService.getUserById(userId);

        if (user.getUserType().equals(User_Type.ADMIN) &&
                ((Administrator) user).getAdminType().equals(AdminType.SUPER)) {
            System.out.println("Super admin cannot be removed. Contact technical team.\n");
            return;
        }

        // Ask for confirmation before deleting the user
        System.out.print("Are you sure you want to delete this user? (yes/no): ");
        String confirmation = scanner.nextLine().trim().toLowerCase();
        if (!confirmation.equals("yes")) {
            System.out.println("User deletion cancelled.\n");
            return;
        }

        // Proceed with user deletion
        try {
            boolean userRemoved = userService.deleteUser(userId);

            if (userRemoved) {
                System.out.println("User successfully removed.\n");
            } else {
                System.out.println("User could not be removed.\n");
            }

        } catch (UserNotFoundException | SuperAdminException e) {
            System.out.println(e.getMessage() + "\n");
        } catch (IllegalArgumentException illegalArgumentException) {
            System.out.println("Error: " + illegalArgumentException.getMessage());
        }

    }

    //-----------------------------------------------------------------------
    /**
     * Displays the user type (ADMIN or MEMBER) of a user based on their user ID.
     * <p>
     * This method prompts the administrator to enter a user ID, validates the input,
     * fetches the corresponding user type from the system, and displays it.
     * </p>
     *
     * Handles invalid input, user not found scenarios, and exceptions such as
     * {@code IllegalArgumentException} and {@code UserNotFoundException}.
     */
    public void viewUserType() {
        System.out.println("\n--- View User Type ---");

        // Prompt for user ID input
        System.out.print("Enter user ID: ");
        String userIdStr = scanner.nextLine();

        // Validate the user ID format
        if (!UserInputValidator.isValidUserId(userIdStr)) {
            System.out.println("Invalid user ID.\n");
            return;
        }

        try {
            // Parse user ID and retrieve user type
            int userId = Integer.parseInt(userIdStr);
            User_Type userType = userService.getUserType(userId);

            // Display the result
            System.out.println("User with ID " + userId + " is of type: " + userType + "\n");

        } catch (IllegalArgumentException | UserNotFoundException e) {
            // Handle validation or business rule exceptions
            System.out.println(e.getMessage() + "\n");
        }
    }

    //-----------------------------------------------------------------------
    /**
     * Displays the admin type (SUPER or STANDARD) of a user based on their user ID.
     * <p>
     * This method prompts the administrator to enter an admin ID, validates the input,
     * fetches the corresponding admin type from the system, and displays it.
     * </p>
     *
     * Handles invalid input, user not found scenarios, and exceptions such as
     * {@code IllegalArgumentException} and {@code UserNotFoundException}.
     */
    public void viewAdminType() {
        System.out.println("\n--- View Admin Type ---");

        // Prompt for admin ID input
        System.out.print("Enter admin ID: ");
        String userIdStr = scanner.nextLine();

        // Validate the admin ID format
        if (!UserInputValidator.isValidUserId(userIdStr)) {
            System.out.println("Invalid user ID.\n");
            return;
        }

        try {
            // Parse user ID and retrieve admin type
            int userId = Integer.parseInt(userIdStr);
            AdminType adminType = userService.getAdminType(userId);

            // Display the result
            System.out.println("Admin with ID " + userId + " is of type: " + adminType + "\n");

        } catch (UserNotFoundException | IllegalArgumentException e) {
            // Handle exceptions related to invalid ID or user not found
            System.out.println(e.getMessage() + "\n");
        }
    }

    //-----------------------------------------------------------------------
    /**
     * Displays all books currently borrowed by a user identified by their user ID.
     * <p>
     * This method prompts the admin to input a user ID, validates the ID, retrieves
     * the list of borrowed books for that user via {@code bookService}, and prints them.
     * </p>
     *
     * Handles:
     * - Invalid user ID input
     * - Users who haven't borrowed any books
     * - Exceptions such as {@code UserNotFoundException} and {@code IllegalArgumentException}
     */
    public void viewBorrowedBooksByUser() {
        System.out.println("\n--- View Borrowed Books by a User ---");

        // Prompt the admin to enter the user ID
        System.out.print("Enter user ID: ");
        String userIdStr = scanner.nextLine();

        // Validate user ID format
        if (!UserInputValidator.isValidUserId(userIdStr)) {
            System.out.println("Invalid user ID.");
            return;
        }

        try {
            // Convert user ID to integer
            int userId = Integer.parseInt(userIdStr);

            // Retrieve list of borrowed books for the specified user
            List<Book> borrowedBooksByUser = bookService.getBorrowedBooksByUser(userId);

            // Display the books if any exist, otherwise show a friendly message
            if (!borrowedBooksByUser.isEmpty()) {
                System.out.println("\nBorrowed Books for User ID " + userId + ":");
                borrowedBooksByUser.forEach(System.out::println);
                System.out.println();
            } else {
                System.out.println("No books have been borrowed by the user.\n");
            }

        } catch (UserNotFoundException | IllegalArgumentException e) {
            // Display the exception message if the user does not exist or ID is invalid
            System.out.println(e.getMessage() + "\n");
        }
    }

    //-----------------------------------------------------------------------
    /**
     * Displays the complete borrowing history of a specific user based on their user ID.
     * <p>
     * This method prompts the admin to input a user ID, validates the input,
     * retrieves the user's borrowing history using the {@code transactionService},
     * and prints the history in a readable format.
     * </p>
     *
     * Handles:
     * - Invalid user ID format
     * - Users with no borrowing history
     * - {@code UserNotFoundException} and {@code IllegalArgumentException}
     */
    public void viewUserBorrowingHistory() {
        System.out.println("\n--- View User Borrowing History ---");

        // Prompt for the user ID
        System.out.print("Enter user ID: ");
        String userIdStr = scanner.nextLine();

        // Validate user ID input
        if (!UserInputValidator.isValidUserId(userIdStr)) {
            System.out.println("Invalid user ID.");
            return;
        }

        try {
            // Convert the input to an integer
            int userId = Integer.parseInt(userIdStr);

            // Fetch the borrowing history for the given user ID
            List<BorrowingHistory> borrowingHistoryList = transactionService.getBorrowingHistoryByUserId(userId);

            // Display history if it exists, otherwise notify that no records were found
            if (!borrowingHistoryList.isEmpty()) {
                System.out.println("\nBorrowing History for User ID " + userId + ":");
                borrowingHistoryList.forEach(System.out::println);
                System.out.println();
            } else {
                System.out.println("User has no borrowing history.\n");
            }
        } catch (UserNotFoundException | IllegalArgumentException e) {
            // Handle case where user is not found or ID is invalid
            System.out.println(e.getMessage() + "\n");
        }
    }

    //-----------------------------------------------------------------------
    /**
     * Displays the list of books currently borrowed by a specific member.
     * <p>
     * This method retrieves and prints all books that are currently borrowed by the
     * user associated with the provided {@code memberId}. If no books are borrowed,
     * an appropriate message is displayed. Exceptions are handled gracefully.
     * </p>
     *
     * @param memberId the ID of the member whose borrowed books are to be displayed
     */
    public void viewMemberBorrowedBooks(int memberId) {

        try {
            // Fetch the list of books currently borrowed by the member
            List<Book> borrowedBooks = bookService.getBorrowedBooksByUser(memberId);

            // If the list is empty, notify the user and return
            if (borrowedBooks.isEmpty()) {
                System.out.println("You have no books borrowed currently.\n");
                return;
            }

            // Print all borrowed books
            System.out.println("\nYou have currently borrowed the following books: ");
            borrowedBooks.forEach(System.out::println);
            System.out.println();

        } catch (UserNotFoundException | IllegalArgumentException e) {
            // Handle case where the user does not exist or an invalid ID is passed
            System.out.println(e.getMessage() + "\n");
        }
    }

    //-----------------------------------------------------------------------
    /**
     * Displays the borrowing history of a specific member.
     * <p>
     * This method fetches and prints all past borrowing records associated with the
     * provided {@code memberId}. If no borrowing history is found, the user is informed.
     * </p>
     *
     * @param memberId the ID of the member whose borrowing history is to be displayed
     */
    public void viewMemberBorrowingHistory(int memberId) {

        try {
            // Retrieve the borrowing history for the given member
            List<BorrowingHistory> borrowingHistoryList = transactionService.getBorrowingHistoryByUserId(memberId);

            // Inform the user if no history exists
            if (borrowingHistoryList.isEmpty()) {
                System.out.println("You have not borrowed any books yet.\n");
            }

            // Print all borrowing records
            System.out.println("\n--- Borrowing History ---");
            borrowingHistoryList.forEach(System.out::println);
            System.out.println();

        } catch (UserNotFoundException | IllegalArgumentException e) {
            // Handle exceptions related to user validity
            System.out.println(e.getMessage() + "\n");
        }
    }

    //-----------------------------------------------------------------------
    /**
     * Allows a member to update a specific field in their profile.
     * <p>
     * Prompts the member to choose a field to update (e.g., email, phone number, etc.),
     * validates the new input value, and then updates the field in the database.
     * If the update is successful, a confirmation message is displayed; otherwise,
     * an appropriate error message is shown.
     * </p>
     *
     * @param memberId the ID of the member who is updating their profile
     */
    public void updateMemberProfile(int memberId) {
        System.out.println("\n--- Update Profile ---");

        // Prompt the member to select a field to update
        String field = getMemberField();

        // Handle early return cases
        if (field.equalsIgnoreCase("invalid") ||
            field.equalsIgnoreCase("return")) {
            return;
        }

        // Get the new value for the selected field
        Object newValue = getNewMemberValue(field);

        // If invalid input is received, print message and return
        if ((newValue instanceof String) &&
                (((String) newValue).equalsIgnoreCase("invalid"))) {
            System.out.println(newValue);
            return;
        }

        // Attempt to update the selected field with the new value
        boolean fieldUpdated = userService.updateMemberField(memberId, field, newValue);

        // Format the field name for user-friendly display
        String replaced = field.replace("_" , " ");
        String result = field.substring(0, 1).toUpperCase() + replaced.substring(1);

        // Provide user feedback
        if (fieldUpdated) {
            System.out.println(result + " updated successfully.\n");
        } else {
            System.out.println(result + " could not be updated.\n");
        }
    }

    //-----------------------------------------------------------------------
    /**
     * Prompts the member to select a profile field they want to update.
     * <p>
     * Displays a numbered menu of updateable fields such as username, password,
     * name, contact info, etc. The user can also choose to return to the previous menu
     * or exit the program. The method returns the selected field in lowercase with
     * underscores to match the field naming in the database.
     * </p>
     *
     * @return the selected field name in lowercase with underscores,
     *         "return" if user chooses to go back,
     *         or "invalid" if input is not valid
     */
    private String getMemberField() {
        // Define the updatable fields
        String[] fields = {"Username", "Password", "Firstname", "Surname", "Date of Birth",
                "Gender", "Email", "Phone Number", "Address"};

        // Display field selection menu
        System.out.println("\nSelect the field to update: ");
        for (int i = 0; i < fields.length; i++) {
            System.out.println((i + 1) + ". " + fields[i]);
        }
        System.out.println((fields.length + 1) + ". Return to the previous menu");
        System.out.println((fields.length + 2) + ". Exit the program");


        try {
            // Get user choice
            System.out.print("Enter your choice: ");
            String selectedFieldStr = scanner.nextLine();
            int selectedField = Integer.parseInt(selectedFieldStr);

            // Validate input range
            if (selectedField < 1 || selectedField > fields.length + 2) {
                System.out.println("Invalid input, try again.\n");
                return "invalid";
            }

            // Option to return
            if (selectedField == fields.length + 1) {
                return "return";
            }

            // Option to exit
            if (selectedField == fields.length + 2) {
                exitProgram();
            }

            // Return normalized field name (e.g., "Phone Number" -> "phone_number")
            return fields[selectedField - 1].toLowerCase().replaceAll("\\s+", "_");

        } catch (NumberFormatException numberFormatException) {
            // Handle invalid number input
            System.out.println("Invalid input, enter a valid number.\n");
        }

        return "invalid";
    }

    //-----------------------------------------------------------------------
    /**
     * Prompts the member to input a new value for a specific profile field and validates it.
     * <p>
     * Based on the selected field (username, password, firstname, surname, etc.), this method asks
     * for user input, validates it using the appropriate validator method, and returns the valid value.
     * If the input is invalid, it returns the string "invalid" to indicate the error.
     * </p>
     *
     * @param field the member field to update (e.g., "username", "email", "address")
     * @return the validated new value as an Object, or the string "invalid" if validation fails
     */
    private Object getNewMemberValue(String field) {
        switch (field.toLowerCase()) {
            case "username" -> {
                System.out.print("\nEnter new username: ");
                String username = scanner.nextLine();

                // Validate the format of the username
                if (!MemberInputValidator.isValidUsername(username)) {
                    System.out.println("Invalid username format.");
                    return "invalid";
                }

                // Check if the username is already taken by another user
                try {
                    if (userService.getUserByUsername(username) != null) {
                        System.out.println("Username already exists for another user. " +
                                "Try another username.\n");
                        return "invalid";
                    }
                } catch (UserNotFoundException ignored) { }

                return username;
            }
            case "password" -> {
                System.out.print("\nEnter new password: ");
                String password = scanner.nextLine();

                // Validate password requirements
                if (!MemberInputValidator.isValidPassword(password)) {
                    System.out.println("Password must contain at least one digit, one lowercase, " +
                            "one uppercase, and one special character");
                    return "invalid";
                }

                return password;
            }
            case "firstname" -> {
                System.out.print("\nEnter firstname: ");
                String firstname = scanner.nextLine();
                if (!MemberInputValidator.isValidFirstName(firstname)) {
                    System.out.println("Invalid firstname.");
                    return "invalid";
                }

                return firstname;
            }
            case "surname" -> {
                System.out.print("\nEnter surname: ");
                String surname = scanner.nextLine();
                if (!MemberInputValidator.isValidSurname(surname)) {
                    System.out.println("Invalid surname.");
                    return "invalid";
                }

                return surname;
            }
            case "date_of_birth" -> {
                System.out.print("\nEnter new date of birth in (YYYY-MM-DD) format: ");
                String dateOfBirthStr = scanner.nextLine();
                if (!MemberInputValidator.isValidDateOfBirth(dateOfBirthStr)) {
                    System.out.println("Invalid date format. Please enter in " +
                            "YYYY-MM-DD format.");
                    return "invalid";
                }

                return LocalDate.parse(dateOfBirthStr);
            }
            case "gender" -> {
                System.out.print("\nEnter gender (male, female): ");
                String gender = scanner.nextLine();
                if (!MemberInputValidator.isValidGender(gender)) {
                    System.out.println("Invalid gender. Must be Male or Female.");
                    return "invalid";
                }

                return Gender.valueOf(gender.toUpperCase());
            }
            case "email" -> {
                System.out.print("\nEnter email address (example@123.com): ");
                String email = scanner.nextLine();
                if (!MemberInputValidator.isValidEmail(email)) {
                    System.out.println("Invalid email format. Email must be in \"example@123.com\" format.");
                    return "invalid";
                }

                return email;
            }
            case "phone_number" -> {
                System.out.print("\nEnter phone number: ");
                String phoneNumber = scanner.nextLine();
                if (!MemberInputValidator.isValidPhoneNumber(phoneNumber)) {
                    System.out.println("Invalid phone number. Phone number must be 10 to 15 digits with an option + in the beginning.");
                    return "invalid";
                }

                return phoneNumber;
            }
            case "address" -> {
                // Delegate address input to the address input method
                return takeAddressInput();
            }
            default -> System.out.println("Invalid field.");
        }


        return "";
    }

    //-----------------------------------------------------------------------
    /**
     * Collects address input from the user and validates each component.
     * <p>
     * Prompts the user to enter details for street, city, pincode, state, and country.
     * Each field is validated using the {@code AddressInputValidator}. If any validation fails,
     * the method prints an error message and returns {@code null}. If all fields are valid,
     * a new {@code Address} object is created and returned.
     * </p>
     *
     * @return a valid {@code Address} object, or {@code null} if any input is invalid
     */
    private Address takeAddressInput() {
        System.out.println("\nAddress Details: ");

        // Prompt for and validate street
        System.out.print("Enter street: ");
        String street = scanner.nextLine();
        if(!AddressInputValidator.isValidStreet(street)) {
            System.out.println("Invalid street. Please provide a valid street.");
            return null;
        }

        // Prompt for and validate city
        System.out.print("Enter city: ");
        String city = scanner.nextLine();
        if(!AddressInputValidator.isValidCity(city)) {
            System.out.println("Invalid city. Please provide a valid city.");
            return null;
        }

        // Prompt for and validate pincode
        System.out.print("Enter pincode: ");
        String pincode = scanner.nextLine();
        if(!AddressInputValidator.isValidPincode(pincode)) {
            System.out.println("Invalid pincode. Please provide a valid pincode.");
            return null;
        }

        // Prompt for and validate state
        System.out.print("Enter state: ");
        String state = scanner.nextLine();
        if(!AddressInputValidator.isValidState(state)) {
            System.out.println("Invalid state. Please provide a valid state.");
            return null;
        }

        // Prompt for and validate country
        System.out.print("Enter country: ");
        String country = scanner.nextLine();
        if(!AddressInputValidator.isValidCountry(country)){
            System.out.println("Invalid country.");
            return null;
        }

        // All fields are valid, construct and return Address object
        return new Address(
                street, city, pincode,
                state, country
        );
    }

    //-----------------------------------------------------------------------
    /**
     * Terminates the program gracefully.
     * <p>
     * This method is invoked when the user chooses to exit the application.
     * It prints a goodbye message to the console and then exits the program
     * using {@code System.exit(0)} to indicate a normal termination.
     * </p>
     */
    private void exitProgram() {
        // Display farewell message to the user
        System.out.println("Exiting the program. Goodbye!");

        // Exit the application with status code 0 (normal termination)
        exit(0);
    }

}
