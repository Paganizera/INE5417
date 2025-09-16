package ine5417.algorithms.implementations;

import ine5417.algorithms.Algorithm;
import ine5417.commom.Frequency;
import ine5417.records.BruteForce;
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
    public List<BruteForce> bruteforce(byte[] ciphertext) {
        List<BruteForce> result = new ArrayList<>();
        for (String lang : Frequency.availableLanguages) {
            Map<Byte, Float> langTable = Frequency.tables.get(lang);
            if (ciphertext.length < 30) {
                langTable.put((byte) ' ', 7f);
            }

            byte[] bestGuess = new byte[0];
            byte key = 0;
            float bestScore = Float.NEGATIVE_INFINITY;

            for (int keyGuess = 0; keyGuess < 256; keyGuess++) {
                byte currentKey = (byte) keyGuess;

                byte[] potentialPlaintext = ciphertext.clone();
                execute(potentialPlaintext, new byte[]{currentKey});

                float score = calculateScore(potentialPlaintext, langTable);

                if (score > bestScore) {
                    bestScore = score;
                    bestGuess = potentialPlaintext;
                    key = currentKey;
                }
            }

            result.add(new BruteForce(lang, new String(bestGuess), String.valueOf(key), bestScore));
        }
        return result;
    }


    protected float calculateScore(byte[] plaintext, Map<Byte, Float> frequencyTable) {
        float score = 0;
        for (byte b : plaintext) {
            // If byte 'b' is not in the map, use 0.0f as its score instead of getting null.
            score += frequencyTable.getOrDefault(b, 0.0f);
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
