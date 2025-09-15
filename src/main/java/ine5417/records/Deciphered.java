package ine5417.records;

/**
 * Represents the result of a decryption operation.
 *
 * @param decryptedMessage The resulting plaintext message after decryption.
 * @param cipher           The identifier of the cipher used for the decryption.
 * @param key              The key used to decrypt the message.
 */
public record Deciphered(String decryptedMessage, String cipher, String key) {
}