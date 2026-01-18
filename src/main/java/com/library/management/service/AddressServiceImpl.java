package com.library.management.service;

import com.library.management.dao.AddressDAO;
import com.library.management.model.Address;

/**
 * Class: AddressServiceImpl
 *
 * Description:
 * - Implements the AddressService interface and provides concrete logic for managing addresses in the Library Management System.
 * - Interacts with the AddressDAO for database operations.
 * - Performs basic input validation before delegating to the DAO layer.
 *
 * Dependencies:
 * - AddressDAO: Handles all address-related database interactions.
 *
 * Key Responsibilities:
 * - Add, retrieve, update, and delete addresses based on address ID or user ID.
 * - Ensure valid input is passed before accessing the DAO layer.
 *
 * @author Fardaws Jawad
 */
public class AddressServiceImpl implements AddressService {


    private final AddressDAO addressDAO;


    /**
     * Constructor initializes the AddressDAO instance.
     */
    public AddressServiceImpl() {
        this.addressDAO = new AddressDAO();
    }

    //-----------------------------------------------------------------------
    /**
     * Adds a new address and links it to the specified user.
     *
     * @param address Address - The address object to be added
     * @param userId int - ID of the user to whom the address is associated
     * @return boolean - true if addition is successful, false otherwise
     * @throws IllegalArgumentException - if address is null or userId is invalid (<= 0)
     */
    @Override
    public boolean addAddress(Address address, int userId) {
        if(address == null || userId <= 0) throw new IllegalArgumentException("Valid address and user ID required.");

        return addressDAO.addAddress(address, userId);
    }

    //-----------------------------------------------------------------------
    /**
     * Retrieves an address using its unique address ID.
     *
     * @param addressId int - The ID of the address to be retrieved
     * @return Address - The corresponding address object, or null if not found
     * @throws IllegalArgumentException - if addressId is invalid (<= 0)
     */
    @Override
    public Address getAddressById(int addressId) {
        if(addressId <= 0) throw new IllegalArgumentException("Invalid address ID.");

        return addressDAO.getAddressById(addressId);
    }

    //-----------------------------------------------------------------------
    /**
     * Retrieves the address associated with a specific user ID.
     *
     * @param userId int - The ID of the user
     * @return Address - The corresponding address object, or null if not found
     * @throws IllegalArgumentException - if userId is invalid (<= 0)
     */
    @Override
    public Address getAddressByUserId(int userId) {
        if(userId <= 0) throw new IllegalArgumentException("Invalid user ID.");

        return addressDAO.getAddressByUserId(userId);
    }

    //-----------------------------------------------------------------------
    /**
     * Updates the address of the specified user.
     *
     * @param address Address - The updated address object
     * @param userId int - The ID of the user whose address is being updated
     * @return boolean - true if the update was successful, false otherwise
     * @throws IllegalArgumentException - if address is null or userId is invalid (<= 0)
     */
    @Override
    public boolean updateAddress(Address address, int userId) {
        if(address == null || userId <= 0) throw new IllegalArgumentException("Valid address and user ID required.");

        return addressDAO.updateAddress(address, userId);
    }

    //-----------------------------------------------------------------------
    /**
     * Deletes an address using its address ID.
     *
     * @param addressId int - The ID of the address to be deleted
     * @return boolean - true if deletion is successful, false otherwise
     * @throws IllegalArgumentException - if addressId is invalid (<= 0)
     */
    @Override
    public boolean deleteAddress(int addressId) {
        if(addressId <= 0) throw new IllegalArgumentException("Invalid address ID.");

        return addressDAO.deleteAddress(addressId);
    }

    //-----------------------------------------------------------------------
    /**
     * Deletes the address associated with a given user ID.
     *
     * @param userId int - The ID of the user whose address is to be deleted
     * @return boolean - true if deletion is successful, false otherwise
     * @throws IllegalArgumentException - if userId is invalid (<= 0)
     */
    @Override
    public boolean deleteAddressByUserId(int userId) {
        if(userId <= 0) throw new IllegalArgumentException("Invalid user ID.");

        return addressDAO.deleteAddressByUserId(userId);
    }
}
