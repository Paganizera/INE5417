package ine5417.algorithms.implementations;

import org.apache.commons.lang3.tuple.Triple;

import java.util.List;

public class RepeatingXOR extends SingleKeyXOR {
    public static final String IDENTIFIER = "repeatingxor";

    public static String getIdentifier() {
        return RepeatingXOR.IDENTIFIER;
    }

    @Override
    public byte[] cipher(byte[] plaintext, byte[] key) {
        return execute(plaintext, key);
    }

    @Override
    public byte[] decipher(byte[] encrypted, byte[] key) {
        return execute(encrypted, key);
    }

    @Override
    public List<Triple<String, byte[], Float>> bruteforce(byte[] ciphertext) {
        //TODO: implement the logic behind this
        return null;
    }


    private byte[] execute(byte[] plaintext, byte[] key) {
        for (int i = 0; i < plaintext.length; i++) {
            plaintext[i] ^= key[i % key.length];
        }
        return plaintext;
    }
}






