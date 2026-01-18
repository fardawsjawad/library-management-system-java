package com.library.management.service;

import com.library.management.model.Address;

/**
 * Interface: AddressService
 *
 * Description:
 * - Defines service-level operations for managing user addresses in the Library Management System.
 * - Provides methods to add, retrieve, update, and delete addresses based on address ID or user ID.
 * - This interface ensures abstraction of address-related business logic from controllers and DAO layers.
 *
 * Key Responsibilities:
 * - Add a new address linked to a user
 * - Fetch address using address ID or user ID
 * - Update an address for a given user
 * - Delete an address using address ID or user ID
 *
 * @author Fardaws Jawad
 */
public interface AddressService {

    //-----------------------------------------------------------------------
    /**
     * Adds a new address and associates it with the specified user.
     *
     * @param address Address - The address to be added
     * @param userId int - The ID of the user to associate the address with
     * @return boolean - true if the address is added successfully, false otherwise
     */
    boolean addAddress(Address address, int userId);

    //-----------------------------------------------------------------------
    /**
     * Retrieves an address by its unique address ID.
     *
     * @param addressId int - The ID of the address to be fetched
     * @return Address - The address object if found, otherwise null
     */
    Address getAddressById(int addressId);

    //-----------------------------------------------------------------------
    /**
     * Retrieves the address associated with a specific user ID.
     *
     * @param userId int - The ID of the user whose address is to be fetched
     * @return Address - The address object linked to the user, otherwise null
     */
    Address getAddressByUserId(int userId);

    //-----------------------------------------------------------------------
    /**
     * Updates the address of the specified user.
     *
     * @param address Address - The updated address object
     * @param userId int - The ID of the user whose address is to be updated
     * @return boolean - true if the update is successful, false otherwise
     */
    boolean updateAddress(Address address, int userId);

    //-----------------------------------------------------------------------

    /**
     * Deletes an address using its address ID.
     *
     * @param addressId int - The ID of the address to be deleted
     * @return boolean - true if the address is deleted successfully, false otherwise
     */
    boolean deleteAddress(int addressId);

    //-----------------------------------------------------------------------
    /**
     * Deletes the address associated with the given user ID.
     *
     * @param userId int - The ID of the user whose address should be deleted
     * @return boolean - true if the deletion is successful, false otherwise
     */
    boolean deleteAddressByUserId(int userId);

}
