package ine5417.controllers;

import ine5417.algorithms.Algorithm;
import ine5417.algorithms.AlgorithmFactory;
import ine5417.records.BruteForce;
import ine5417.records.BruteForceResult;
import ine5417.records.Ciphered;
import ine5417.records.Deciphered;
import org.apache.coyote.BadRequestException;
import org.springframework.stereotype.Service;

import java.util.Base64;
import java.util.List;
import java.util.Optional;

@Service
public class CipherController {

    /**
     * Encrypts a plaintext message using a specified cipher and key.
     *
     * @param plaintext The message to be encrypted.
     * @param cipher    The identifier of the cipher to be used (e.g., "singleKeyXOR").
     * @param key       The key for the encryption.
     * @return A {@link Ciphered} object containing the encrypted message and operation parameters.
     * @throws BadRequestException If the specified cipher is not found or invalid.
     */
    public Ciphered encrypt(String plaintext, String cipher, String key) throws BadRequestException {
        Algorithm algorithm = getAlgorithm(cipher);
        byte[] message = algorithm.cipher(plaintext.getBytes(), key.getBytes());
        String encodedMessage = Base64.getEncoder().encodeToString(message);
        return new Ciphered(encodedMessage, cipher, key);
    }

    /**
     * Decrypts a ciphertext message using a specified cipher and key.
     *
     * @param encrypted The message to be decrypted.
     * @param cipher    The identifier of the cipher to be used.
     * @param key       The key required for decryption.
     * @return A {@link Deciphered} object containing the original plaintext and operation parameters.
     * @throws BadRequestException If the specified cipher is not found or invalid.
     */
    public Deciphered decrypt(String encrypted, String cipher, String key) throws BadRequestException {
        Algorithm algorithm = getAlgorithm(cipher);
        byte[] encryptedBytes = Base64.getDecoder().decode(encrypted);
        byte[] message = algorithm.decipher(encryptedBytes, key.getBytes());

        return new Deciphered(new String(message), cipher, key);
    }

    /**
     * Attempts to break an encrypted message without a key by trying all possibilities.
     *
     * @param encrypted The ciphertext to attack.
     * @param cipher    The identifier of the cipher suspected to have been used.
     * @return A {@link BruteForceResult} containing a list of potential decryptions and their scores.
     * @throws BadRequestException If the specified cipher is not found or invalid.
     */
    public BruteForceResult bruteforce(String encrypted, String cipher) throws BadRequestException {
        Algorithm algorithm = getAlgorithm(cipher);
        List<BruteForce> result = algorithm.bruteforce(Base64.getDecoder().decode(encrypted));

        return new BruteForceResult(cipher, result);
    }

    /**
     * Retrieves an algorithm implementation from the factory based on its identifier.
     *
     * @param cipher The string identifier for the desired algorithm.
     * @return An instance of the requested {@link Algorithm}.
     * @throws BadRequestException If no algorithm with the given identifier is found.
     */
    public Algorithm getAlgorithm(String cipher) throws BadRequestException {
        Optional<Algorithm> algorithm = AlgorithmFactory.getAlgorithm(cipher.toLowerCase());

        if (algorithm.isEmpty()) {
            throw new BadRequestException("Invalid cipher");
        }
        return algorithm.get();
    }
}