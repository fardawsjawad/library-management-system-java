package com.library.management.validator;

/**
 * Utility class that provides validation methods for address-related inputs.
 * <p>
 * This class is used to validate various address fields such as address ID,
 * street, city, pincode, state, and country to ensure data integrity before
 * processing or persisting them into the database.
 * </p>
 *
 * <p>
 * All methods in this class are static and can be used without instantiating the class.
 * </p>
 */
public class AddressInputValidator {

    //-----------------------------------------------------------------------
    /**
     * Validates the address ID string to ensure it represents a positive integer.
     *
     * @param addressIdStr the address ID as a string
     * @return {@code true} if the string is a valid positive integer; {@code false} otherwise
     */
    public static boolean isValidAddressId(String addressIdStr) {
        if (addressIdStr == null || addressIdStr.trim().isEmpty()) return false;

        try {
            int addressId = Integer.parseInt(addressIdStr);
            return addressId > 0;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    //-----------------------------------------------------------------------
    /**
     * Validates that the street name is not null or empty.
     *
     * @param street the street name
     * @return {@code true} if the street is non-null and non-empty; {@code false} otherwise
     */
    public static boolean isValidStreet(String street) {
        return street != null && !street.trim().isEmpty();
    }

    //-----------------------------------------------------------------------
    /**
     * Validates that the city name is not null or empty.
     *
     * @param city the city name
     * @return {@code true} if the city is non-null and non-empty; {@code false} otherwise
     */
    public static boolean isValidCity(String city) {
        return city != null && !city.trim().isEmpty();
    }

    //-----------------------------------------------------------------------
    /**
     * Validates the pincode format.
     * <p>
     * A valid pincode may include letters (case-insensitive), numbers, spaces, and hyphens,
     * and must be between 3 and 10 characters long.
     * </p>
     *
     * @param pincode the postal code
     * @return {@code true} if the pincode matches the expected pattern; {@code false} otherwise
     */
    public static boolean isValidPincode(String pincode) {
        if (pincode == null || pincode.trim().isEmpty()) return false;

        return pincode.trim().matches("^[A-Za-z0-9\\s\\-]{3,10}$");
    }

    //-----------------------------------------------------------------------
    /**
     * Validates that the state name is not null or empty.
     *
     * @param state the state name
     * @return {@code true} if the state is non-null and non-empty; {@code false} otherwise
     */
    public static boolean isValidState(String state) {
        return state != null && !state.trim().isEmpty();
    }

    //-----------------------------------------------------------------------
    /**
     * Validates that the country name is not null or empty.
     *
     * @param country the country name
     * @return {@code true} if the country is non-null and non-empty; {@code false} otherwise
     */
    public static boolean isValidCountry(String country) {
        return country != null && !country.trim().isEmpty();
    }

}
