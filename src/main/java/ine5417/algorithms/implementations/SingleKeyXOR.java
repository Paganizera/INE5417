package ine5417.algorithms.implementations;

import ine5417.algorithms.Algorithm;

public class SingleKeyXOR implements Algorithm {
    public static final String IDENTIFIER = "singleKeyXOR";

    public static String getIdentifier() {
        return SingleKeyXOR.IDENTIFIER;
    }

    @Override
    public String Cipher(String plaintext, String key) {
        return "";
    }

    @Override
    public String Decipher(String  ciphertext, String key) {
        return "";
    }

    @Override
    public String Bruteforce(String plaintext) {
        return "";
    }

    @Override
    public boolean inConstraints(String encrypted) {
        return false;
    }

}
