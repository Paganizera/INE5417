package ine5417.algorithms;

import ine5417.records.BruteForce;
import org.apache.commons.lang3.tuple.Triple;

import java.util.List;

/*
 * This interface ensures that different algorithms can be used interchangeably
 * throughout the application by standardizing the methods for encryption, decryption,
 * and brute-force attacks.
 */
public interface Algorithm {

    /**
     * Encrypts a byte array using a specified key.
     *
     * @param plaintext The raw data (plaintext) to be encrypted.
     * @param key       The key to use for encryption.
     * @return The encrypted data as a byte array.
     */
    byte[] cipher(byte[] plaintext, byte[] key);

    /**
     * Decrypts a byte array using a specified key.
     *
     * @param toDecrypt The encrypted data  to be decrypted.
     * @param key       The key required for decryption.
     * @return The original decrypted data as a byte array (plaintext).
     */
    byte[] decipher(byte[] toDecrypt, byte[] key);

    /**
     * Attempts to decrypt a ciphertext without a key by iterating through possibilities.
     *
     * @param ciphertext The encrypted data to be attacked.
     * @return A list of {@link BruteForce} objects, where each represents a potential solution
     */
    List<BruteForce> bruteforce(byte[] ciphertext);
}