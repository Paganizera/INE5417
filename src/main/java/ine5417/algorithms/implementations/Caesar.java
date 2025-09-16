package ine5417.algorithms.implementations;
import ine5417.algorithms.Algorithm;
import org.apache.commons.lang3.tuple.Triple;
import java.util.List;


public class Caesar implements Algorithm {
    public static final String IDENTIFIER = "caesar";

    public static String getIdentifier() {
        return Caesar.IDENTIFIER;
    }

    @Override
    public byte[] cipher(byte[] toCipher, byte[] key) {
        return execute_encrypt(toCipher, key[0]);
    }

    @Override
    public byte[] decipher(byte[] encrypted, byte[] key) {
        return execute_decrypt(encrypted, key[0]);
    }

    private byte[] execute_encrypt(byte[] plaintext, byte key) {
        byte[] encrypted = new byte[plaintext.length];
        for (int i = 0; i < plaintext.length; i++) {
            encrypted[i] = (byte) (text[i] + key);
        }
        return encrypted;
    }

    private byte[] execute_decrypt(byte[] encrypted, byte key) {
        byte[] plaintext = new byte[encrypted.length];
        for (int i = 0; i < encrypted.length; i++) {
            plaintext[i] = (byte) (encrypted[i] - key);
        }
        return plaintext;
    }


    @Override
    public List<Triple<String, byte[], Float>> bruteforce(byte[] ciphertext) {
        //TODO: implement
        return null;
    }
}
