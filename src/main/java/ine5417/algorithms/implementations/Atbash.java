package ine5417.algorithms.implementations;

import ine5417.algorithms.Algorithm;
import ine5417.commom.Frequency;
import ine5417.records.BruteForce;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@SuppressWarnings("unused")
public class Atbash implements Algorithm {
    public static final String IDENTIFIER = "atbash";

    public static String getIdentifier() { return Atbash.IDENTIFIER; }

    /**
     * 'a' becomes 'z', 'b' becomes 'y', etc.
     * 'A' becomes 'Z', 'B' becomes 'Y', etc.
     * Non-alphabetic characters are unchanged.
     */
    private int substituteCharacter(int c) {
        if (c >= 'a' && c <= 'z') {
            return 'a' + ('z' - c);
        }
        if (c >= 'A' && c <= 'Z') {
            return 'A' + ('Z' - c);
        }
        return c;
    }

    /**
     * Executes the Atbash transformation on the data.
     */
    private byte[] execute(byte[] data) {
        String resultString = new String(data, StandardCharsets.UTF_8)
                .chars()
                .map(this::substituteCharacter)
                .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
                .toString();

        return resultString.getBytes(StandardCharsets.UTF_8);
    }

    @Override
    public byte[] cipher(byte[] plaintext, byte[] key) {
        return execute(plaintext);
    }

    @Override
    public byte[] decipher(byte[] toDecrypt, byte[] key) {
        return execute(toDecrypt);
    }

    @Override
    public List<BruteForce> bruteforce(byte[] ciphertext) {
        List<BruteForce> finalResults = new ArrayList<>();

        byte[] potentialPlaintext = execute(ciphertext);
        String plaintextString = new String(potentialPlaintext, StandardCharsets.UTF_8);

        for (String lang : Frequency.availableLanguages) {
            Map<Byte, Float> langTable = Frequency.tables.get(lang);

            float score = calculateScore(potentialPlaintext, langTable);

            finalResults.add(new BruteForce(
                    lang,
                    plaintextString,
                    "N/A",
                    score
            ));
        }

        finalResults.sort((a, b) -> b.score().compareTo(a.score()));
        return finalResults;
    }

    /**
     * Calculates a score for a given plaintext based on a language's
     * letter frequency table.
     */
    private float calculateScore(byte[] plaintext, Map<Byte, Float> frequencyTable) {
        float score = 0;
        for (byte b : plaintext) {
            score += frequencyTable.getOrDefault(b, 0.0f);
        }
        return score;
    }
}