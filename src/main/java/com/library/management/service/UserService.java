package com.library.management.service;

import com.library.management.model.*;

import java.util.List;

/**
 * Interface: UserService
 *
 * Description:
 * - Defines the contract for all user-related operations within the Library Management System.
 * - Includes functionalities for registering, retrieving, updating, and deleting users.
 * - Also provides role-specific (Admin/Member) management and type-related updates.
 *
 * Key Responsibilities:
 * - User registration and retrieval by ID or username
 * - Update user profile information including credentials and roles
 * - Delete users and verify user existence
 * - Separate management of members and administrators
 * - Handle role-specific updates for AdminType and Member attributes
 *
 * @author Fardaws Jawad
 */
public interface UserService {

    //-----------------------------------------------------------------------
    /**
     * Registers a new user (member or administrator) in the system.
     *
     * @param user User - The user object containing necessary details
     * @return boolean - true if registration is successful, false otherwise
     */
    boolean registerUser(User user);

    //-----------------------------------------------------------------------
    /**
     * Retrieves a user by their unique user ID.
     *
     * @param userId int - ID of the user
     * @return User - The user object if found, null otherwise
     */
    User getUserById(int userId);

    //-----------------------------------------------------------------------
    /**
     * Retrieves a user by their username.
     *
     * @param username String - The username to search
     * @return User - The user object if found, null otherwise
     */
    User getUserByUsername(String username);

    //-----------------------------------------------------------------------
    /**
     * Retrieves a list of all users in the system.
     *
     * @return List<User> - A list containing all registered users
     */
    List<User> getAllUsers();

    //-----------------------------------------------------------------------
    /**
     * Retrieves all users with administrator privileges.
     *
     * @return List<Administrator> - A list of all administrators
     */
    List<Administrator> getAllAdmins();

    //-----------------------------------------------------------------------
    /**
     * Retrieves all users with member role.
     *
     * @return List<Member> - A list of all members
     */
    List<Member> getAllMembers();

    //-----------------------------------------------------------------------
    /**
     * Updates the profile of an existing user.
     *
     * @param user User - Updated user object
     * @return boolean - true if update is successful, false otherwise
     */
    boolean updateUser(User user);

    //-----------------------------------------------------------------------
    /**
     * Updates the username of a specific user.
     *
     * @param userId int - ID of the user
     * @param newUsername String - New username
     * @return boolean - true if update is successful
     */
    boolean updateUsername(int userId, String newUsername);

    //-----------------------------------------------------------------------
    /**
     * Updates the password of a specific user.
     *
     * @param userId int - ID of the user
     * @param newPassword String - New password
     * @return boolean - true if update is successful
     */
    boolean updatePassword(int userId, String newPassword);

    //-----------------------------------------------------------------------
    /**
     * Updates the user type (e.g., MEMBER, ADMIN) of a user.
     *
     * @param userId int - ID of the user
     * @param userType User_Type - The new user type
     * @return boolean - true if the user type is updated
     */
    boolean updateUserType(int userId, User_Type userType);

    //-----------------------------------------------------------------------
    /**
     * Updates the administrative role of an admin user.
     *
     * @param userId int - ID of the user
     * @param adminType AdminType - New admin type (e.g., SUPER_ADMIN)
     * @return boolean - true if update is successful
     */
    boolean updateAdminType(int userId, AdminType adminType);

    //-----------------------------------------------------------------------
    /**
     * Dynamically updates a member-specific field.
     *
     * @param memberId int - ID of the member
     * @param field String - Name of the field to be updated
     * @param newValue Object - New value to be applied
     * @return boolean - true if update is successful
     */
    boolean updateMemberField(int memberId, String field, Object newValue);

    //-----------------------------------------------------------------------
    /**
     * Deletes a user from the system.
     *
     * @param userId int - ID of the user to be deleted
     * @return boolean - true if deletion is successful
     */
    boolean deleteUser(int userId);

    //-----------------------------------------------------------------------
    /**
     * Retrieves the user type of a specific user.
     *
     * @param userId int - ID of the user
     * @return User_Type - The type of the user (e.g., ADMIN, MEMBER)
     */
    User_Type getUserType(int userId);

    //-----------------------------------------------------------------------
    /**
     * Retrieves the administrative type of an admin user.
     *
     * @param userId int - ID of the admin
     * @return AdminType - The admin's type (e.g., SUPER_ADMIN, MODERATOR)
     */
    AdminType getAdminType(int userId);

    //-----------------------------------------------------------------------
    /**
     * Checks if a user exists in the system.
     *
     * @param userId int - ID of the user
     * @return boolean - true if the user exists, false otherwise
     */
    boolean userExists(int userId);

}
