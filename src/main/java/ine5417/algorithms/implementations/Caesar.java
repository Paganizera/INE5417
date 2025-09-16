package ine5417.algorithms.implementations;

import ine5417.algorithms.Algorithm;
import ine5417.commom.Frequency;
import ine5417.records.BruteForce;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@SuppressWarnings("unused")
public class Caesar implements Algorithm {
    public static final String IDENTIFIER = "caesar";

    public static String getIdentifier() { return Caesar.IDENTIFIER; }

    @Override
    public byte[] cipher(byte[] toCipher, byte[] key) {
        byte shiftKey = (byte) (key[0] % 26);
        return execute(toCipher, shiftKey);
    }

    @Override
    public byte[] decipher(byte[] encrypted, byte[] key) {
        byte shiftKey = (byte) (key[0] % 26);
        byte inverseKey = (byte) ((26 - shiftKey) % 26);
        return execute(encrypted, inverseKey);
    }

    private int shiftCharacter(int c, byte shiftAmount) {
        if (c >= 'a' && c <= 'z') {
            return 'a' + (c - 'a' + shiftAmount) % 26;
        }
        if (c >= 'A' && c <= 'Z') {
            return 'A' + (c - 'A' + shiftAmount) % 26;
        }
        return c;
    }

    private byte[] execute(byte[] data, byte shiftAmount) {
        String resultString = new String(data, StandardCharsets.UTF_8)
                .chars()
                .map(c -> shiftCharacter(c, shiftAmount))
                .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
                .toString();

        return resultString.getBytes(StandardCharsets.UTF_8);
    }

    @Override
    public List<BruteForce> bruteforce(byte[] ciphertext) {
        List<BruteForce> finalResults = new ArrayList<>();
        for (String lang : Frequency.availableLanguages) {
            Map<Byte, Float> langTable = Frequency.tables.get(lang);
            List<BruteForce> resultsForLang = new ArrayList<>();

            for (int keyGuess = 0; keyGuess < 26; keyGuess++) {
                byte currentKey = (byte) keyGuess;
                byte inverseKey = (byte) ((26 - currentKey) % 26);
                byte[] potentialPlaintext = execute(ciphertext, inverseKey);
                float score = calculateScore(potentialPlaintext, langTable);

                resultsForLang.add(new BruteForce(
                        lang,
                        new String(potentialPlaintext, StandardCharsets.UTF_8),
                        String.valueOf(currentKey),
                        score
                ));
            }

            resultsForLang.sort((a, b) -> b.score().compareTo(a.score()));
            int limit = Math.min(3, resultsForLang.size());
            finalResults.addAll(resultsForLang.subList(0, limit));
        }
        finalResults.sort((a, b) -> b.score().compareTo(a.score()));
        return finalResults;
    }

    private float calculateScore(byte[] plaintext, Map<Byte, Float> frequencyTable) {
        float score = 0;
        for (byte b : plaintext) {
            score += frequencyTable.getOrDefault(b, 0.0f);
        }
        return score;
    }
}