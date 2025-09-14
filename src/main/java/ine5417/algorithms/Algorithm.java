package ine5417.algorithms;

/**
 * Interface used for standard implementation of algorithms
 */
public interface Algorithm {
    String Cipher(String plaintext, String key);
    String Decipher(String toDecrypt, String key);
    String Bruteforce(String plaintext);
    boolean inConstraints(String encrypted);
}
