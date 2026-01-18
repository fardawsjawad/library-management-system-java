package com.library.management.validator;

import com.library.management.model.AdminType;


/**
 * Validator class for administrator-specific input fields.
 * <p>
 * This class extends {@link UserInputValidator} to inherit general user input validation methods,
 * and adds validation logic specific to administrator roles such as checking valid admin types.
 * </p>
 */
public class AdministratorInputValidator extends UserInputValidator{

    /**
     * Validates the administrator type string.
     * <p>
     * This method checks whether the provided string corresponds to a valid {@link AdminType},
     * specifically either {@code STANDARD} or {@code SUPER}, ignoring case.
     * </p>
     *
     * @param adminTypeStr the admin type as a string
     * @return {@code true} if the string matches a valid admin type; {@code false} otherwise
     */
    public static boolean isValidAdminType(String adminTypeStr) {
        if (adminTypeStr == null || adminTypeStr.trim().isEmpty()) return false;

        try {
            AdminType adminType = AdminType.valueOf(adminTypeStr.trim().toUpperCase());
            return adminType.equals(AdminType.STANDARD) || adminType.equals(AdminType.SUPER);
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

}
