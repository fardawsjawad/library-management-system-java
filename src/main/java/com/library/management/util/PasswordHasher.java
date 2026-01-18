package com.library.management.util;

import org.mindrot.jbcrypt.BCrypt;

/**
 * Utility class for securely hashing and verifying user passwords
 * using the BCrypt algorithm.
 * <p>
 * This class is used to ensure passwords are stored in a secure,
 * non-reversible format in the Library Management System database.
 * BCrypt is a strong hashing algorithm that incorporates a salt
 * and is adaptive, meaning it can be configured to remain slow
 * to resist brute-force attacks even as computing power increases.
 * </p>
 *
 * <p><b>Dependencies:</b> Requires the <code>jBCrypt</code> library.
 * Make sure it is included in your build configuration (e.g., Maven or Gradle).</p>
 *
 * <p>Example usage:</p>
 * <pre>{@code
 * String hashed = PasswordHasher.hashPassword("mySecret123");
 * boolean match = PasswordHasher.checkPassword("mySecret123", hashed);
 * }</pre>
 *
 * @author Fardaws Jawad
 */
public class PasswordHasher {

    /**
     * Hashes a plain text password using BCrypt with a salt.
     *
     * @param plainPassword the user's plain text password
     * @return a securely hashed password string
     */
    public static String hashPassword(String plainPassword) {
        return BCrypt.hashpw(plainPassword, BCrypt.gensalt(12));
    }


    /**
     * Verifies whether a given plain text password matches a previously hashed password.
     *
     * @param plainPassword  the user's input password to verify
     * @param hashedPassword the stored hashed password to compare against
     * @return {@code true} if the password matches; {@code false} otherwise
     */
    public static boolean checkPassword(String plainPassword, String hashedPassword) {
        return BCrypt.checkpw(plainPassword, hashedPassword);
    }

}
