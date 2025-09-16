package ine5417.algorithms.implementations;

import ine5417.algorithms.Algorithm;
import ine5417.commom.Frequency;
import ine5417.records.BruteForce;
import org.apache.commons.lang3.tuple.Pair; // You might need to add this import

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class SingleKeyXOR implements Algorithm {
    public static final String IDENTIFIER = "singlekeyxor";

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

            Pair<Byte, Float> bestResult = findBestSingleByteKey(ciphertext, langTable);
            byte bestKey = bestResult.getKey();
            float bestScore = bestResult.getValue();

            byte[] bestGuess = execute(ciphertext, new byte[]{bestKey});

            result.add(new BruteForce(
                    lang,
                    new String(bestGuess, StandardCharsets.UTF_8),
                    String.valueOf((char)bestKey),
                    bestScore
            ));
        }
        return result;
    }

    public Pair<Byte, Float> findBestSingleByteKey(byte[] ciphertext, Map<Byte, Float> langTable) {
        byte bestKey = 0;
        float bestScore = Float.NEGATIVE_INFINITY;

        for (int keyGuess = 0; keyGuess < 256; keyGuess++) {
            byte currentKey = (byte) keyGuess;
            byte[] potentialPlaintext = execute(ciphertext, new byte[]{currentKey});
            float score = calculateScore(potentialPlaintext, langTable);

            if (score > bestScore) {
                bestScore = score;
                bestKey = currentKey;
            }
        }
        return Pair.of(bestKey, bestScore);
    }


    protected float calculateScore(byte[] plaintext, Map<Byte, Float> frequencyTable) {
        float score = 0;
        for (byte b : plaintext) {
            score += frequencyTable.getOrDefault(b, -5.0f);
        }
        return score;
    }

    private byte[] execute(byte[] message, byte[] key) {
        byte[] output = new byte[message.length];
        for (int i = 0; i < message.length; i++) {
            output[i] = (byte) (message[i] ^ key[0]);
        }
        return output;
    }
}