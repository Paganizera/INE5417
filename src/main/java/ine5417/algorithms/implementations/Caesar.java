package ine5417.algorithms.implementations;

import ine5417.algorithms.Algorithm;
import ine5417.commom.Frequency;
import org.apache.commons.lang3.tuple.Triple;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


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
        List<Triple<String, byte[], Float>> result = new ArrayList<>();
        for (String lang : Frequency.availableLanguages) {
            Map<Byte, Float> langTable = Frequency.tables.get(lang);

            byte[] bestGuess = null;
            float bestScore = Float.NEGATIVE_INFINITY;

            for (int keyGuess = 0; keyGuess < 256; keyGuess++) {
                byte currentKey = (byte) keyGuess;

                byte[] potentialPlaintext = ciphertext.clone();
                execute_decrypt(potentialPlaintext, currentKey);

                float score = calculateScore(potentialPlaintext, langTable);

                if (score > bestScore) {
                    bestScore = score;
                    bestGuess = potentialPlaintext;
                }
            }

            result.add(Triple.of(lang, bestGuess, bestScore));
        }
        return result;
    }


    private float calculateScore(byte[] plaintext, Map<Byte, Float> frequencyTable) {
        float score = 0;
        for (byte b : plaintext) {
            score += frequencyTable.get(b);
        }
        return score;
    }
}
