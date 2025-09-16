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
        byte treatedKey = treatKey(key[0]);
        return execute_encrypt(toCipher, treatedKey);
    }

    @Override
    public byte[] decipher(byte[] encrypted, byte[] key) {
        byte treatedKey = treatKey(key[0]);
        return execute_decrypt(encrypted, treatedKey);
    }

    private byte treatKey(byte key) {
        int treatedKey = ((key % 26) + 26) % 26;
        return (byte) treatedKey;
    }

    private byte[] execute_encrypt(byte[] plaintext, byte key) {
        byte[] encrypted = new byte[plaintext.length];
        for (int i = 0; i < plaintext.length; i++) {
            char letter = (char) plaintext[i];
            if (letter >= 'a' && letter <= 'z') {
                char shifted = (char) ('a' + (letter - 'a' + key) % 26);
                encrypted[i] = (byte) shifted;
            } else if (letter >= 'A' && letter <= 'Z') {
                char shifted = (char) ('A' + (letter - 'A' + key) % 26);
                encrypted[i] = (byte) shifted;
            } else {
                encrypted[i] = plaintext[i];
            }
        }
        return encrypted;
    }

    private byte[] execute_decrypt(byte[] encrypted, byte key) {
        byte[] plaintext = new byte[encrypted.length];
        for (int i = 0; i < encrypted.length; i++) {
            char character = (char) encrypted[i];
            if (character >= 'a' && character <= 'z') {
                int temp = (character - 'a' - key + 26) % 26;
                plaintext[i] = (byte) ('a' + temp);
            } else if (character >= 'A' && character <= 'Z') {
                int temp = (character - 'A' - key + 26) % 26;
                plaintext[i] = (byte) ('A' + temp);
            } else {
                plaintext[i] = encrypted[i];
            }
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
