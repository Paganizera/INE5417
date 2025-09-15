package ine5417.records;

import java.util.List;

/**
 * Encapsulates the complete results of a brute-force decryption attack.
 *
 * @param cipher The identifier of the cipher that was attacked.
 * @param result A list of {@link BruteForce} objects, each representing a
 * potential decryption with its corresponding score and language.
 */
public record BruteForceResult(String cipher, List<BruteForce> result) {
}
