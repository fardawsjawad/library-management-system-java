package com.library.management.model;

/**
 * Enum representing the type of administrator in the Library Management System.
 * <p>
 * This distinction is typically used to control access levels within the system.
 *
 * <ul>
 *   <li>{@code SUPER} - Has full administrative privileges including user management and system settings.</li>
 *   <li>{@code STANDARD} - Has limited administrative rights, such as managing books and viewing users.</li>
 * </ul>
 *
 * @author Fardaws Jawad
 *
 * @see com.library.management.model.User
 */
public enum AdminType {

    /** Full access administrator with elevated privileges. */
    SUPER,

    /** Limited access administrator with standard permissions. */
    STANDARD

}
