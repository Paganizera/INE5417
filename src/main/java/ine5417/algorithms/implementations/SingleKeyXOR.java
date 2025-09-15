package ine5417.algorithms.implementations;

import ine5417.algorithms.Algorithm;
import ine5417.commom.Frequency;
import org.apache.commons.lang3.tuple.Triple;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class SingleKeyXOR implements Algorithm {
    public static final String IDENTIFIER = "singlekeyxor";

    public static String getIdentifier() {
        return SingleKeyXOR.IDENTIFIER;
    }

    @Override
    public byte[] cipher(byte[] toCipher, byte[] key) {
        return execute(toCipher, key);
    }

    @Override
    public byte[] decipher(byte[] encrypted, byte[] key) {
        return execute(encrypted, key);
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
                execute(potentialPlaintext, new byte[]{currentKey});

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

    private byte[] execute(byte[] message, byte[] key) {
        // The operation is done over key[0] in the constraints of a single key xor algorithm
        for (int i = 0; i < message.length; i++) {
            message[i] ^= key[0];
        }
        return message;
    }
}
