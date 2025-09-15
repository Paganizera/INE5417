package ine5417.records;
/**
 * Represents a single potential result from a brute-force decryption attempt.
 *
 * @param language The language profile used to score this result (e.g., "en", "pt").
 * @param result   The potential decrypted plaintext message.
 * @param score    The calculated score. Higher scores are better.
 */
public record BruteForce(String language, String result, Float score) {
}
