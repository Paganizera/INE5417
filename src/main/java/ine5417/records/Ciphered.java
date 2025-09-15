package ine5417.records;

/**
 * Represents the result of an encryption operation.
 *
 * @param encryptedMessage The resulting ciphertext (encrypted message).
 * @param cipher           The identifier of the cipher used for encryption.
 * @param key              The key used for encryption.
 */
public record Ciphered(String encryptedMessage, String cipher, String key) {
}
