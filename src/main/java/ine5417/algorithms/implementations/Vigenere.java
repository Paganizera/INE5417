package ine5417.algorithms.implementations;

import ine5417.algorithms.Algorithm;
import ine5417.commom.Frequency;
import ine5417.records.BruteForce;

import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@SuppressWarnings("unused")
public class Vigenere implements Algorithm {
    public static final String IDENTIFIER = "vigenere";
    private static final int ALPHABET_SIZE = 26;
    private static final int MAX_KEY_LENGTH_TO_TEST = 20;

    @Override
    public byte[] cipher(byte[] plaintext, byte[] key) {
        return process(plaintext, key, true);
    }

    @Override
    public byte[] decipher(byte[] toDecrypt, byte[] key) {
        return process(toDecrypt, key, false);
    }

    private byte[] process(byte[] input, byte[] key, boolean encrypt) {
        if (key == null || key.length == 0) return input;
        byte[] output = new byte[input.length];
        int keyIndex = 0;
        for (int i = 0; i < input.length; i++) {
            char charIn = (char) input[i];
            char charKey = (char) key[keyIndex % key.length];
            if (charIn >= 'a' && charIn <= 'z') {
                int shift = Character.toUpperCase(charKey) - 'A';
                int offset = encrypt ? (charIn - 'a' + shift) : (charIn - 'a' - shift + ALPHABET_SIZE);
                output[i] = (byte) ('a' + (offset % ALPHABET_SIZE));
                keyIndex++;
            } else if (charIn >= 'A' && charIn <= 'Z') {
                int shift = Character.toUpperCase(charKey) - 'A';
                int offset = encrypt ? (charIn - 'A' + shift) : (charIn - 'A' - shift + ALPHABET_SIZE);
                output[i] = (byte) ('A' + (offset % ALPHABET_SIZE));
                keyIndex++;
            } else {
                output[i] = (byte) charIn;
            }
        }
        return output;
    }

    @Override
    public List<BruteForce> bruteforce(byte[] ciphertext) {
        List<BruteForce> allResults = new ArrayList<>();

        StringBuilder lettersOnly = new StringBuilder();
        for (byte b : ciphertext) {
            char c = (char) (b & 0xFF); // Treat byte as unsigned
            if (c >= 'a' && c <= 'z') {
                lettersOnly.append(Character.toUpperCase(c));
            } else if (c >= 'A' && c <= 'Z') {
                lettersOnly.append(c);
            }
        }
        String analyzableText = lettersOnly.toString();

        if (analyzableText.length() < 2) {
            return Collections.emptyList(); // Not enough data to analyze
        }

        List<Integer> topKeyLengths = findTopKeyLengths(analyzableText);

        for (String lang : Frequency.availableLanguages) {
            List<BruteForce> langResults = new ArrayList<>();

            for (int keyLength : topKeyLengths) {
                StringBuilder guessedKey = new StringBuilder();
                for (int i = 0; i < keyLength; i++) {
                    StringBuilder column = new StringBuilder();
                    for (int j = i; j < analyzableText.length(); j += keyLength) {
                        column.append(analyzableText.charAt(j));
                    }
                    if (!column.isEmpty()) {
                        guessedKey.append(findBestKeyCharForColumn(column.toString(), lang));
                    }
                }

                String keyFound = guessedKey.toString();
                if (keyFound.isEmpty()) continue;

                byte[] plaintextBytes = decipher(ciphertext, keyFound.getBytes(StandardCharsets.UTF_8));
                String resultText = new String(plaintextBytes, StandardCharsets.UTF_8);

                double chiSquaredScore = calculateChiSquared(resultText, lang);
                float finalScore = (float) (1 / (chiSquaredScore + 1e-9));

                langResults.add(new BruteForce(lang, resultText, keyFound, finalScore));
            }

            langResults.sort(Comparator.comparing(BruteForce::score).reversed());
            allResults.addAll(langResults.stream().limit(3).toList());
        }

        allResults.sort(Comparator.comparing(BruteForce::score).reversed());
        return allResults;
    }

    /**
     * Finds the most likely key lengths by calculating the Index of Coincidence (IC).
     * Returns a list of the top 'count' key lengths.
     */
    private List<Integer> findTopKeyLengths(String text) {
        // Use Map.Entry as a Pair to store <KeyLength, IC_Score>
        List<Map.Entry<Integer, Double>> candidates = new ArrayList<>();
        final double targetIc = 0.067;

        // Calculate IC for key length 1 as a baseline.
        candidates.add(new AbstractMap.SimpleEntry<>(1, calculateIC(text)));

        // Test other key lengths.
        for (int keyLength = 2; keyLength <= MAX_KEY_LENGTH_TO_TEST; keyLength++) {
            double avgIc = 0.0;
            int validColumns = 0;
            for (int i = 0; i < keyLength; i++) {
                StringBuilder column = new StringBuilder();
                for (int j = i; j < text.length(); j += keyLength) {
                    column.append(text.charAt(j));
                }
                if (column.length() > 1) {
                    avgIc += calculateIC(column.toString());
                    validColumns++;
                }
            }
            if (validColumns > 0) {
                candidates.add(new AbstractMap.SimpleEntry<>(keyLength, avgIc / validColumns));
            }
        }

        // Sort candidates by how close their IC is to the target IC.
        candidates.sort(Comparator.comparingDouble(entry -> Math.abs(entry.getValue() - targetIc)));

        // Return the top 'count' key lengths
        return candidates.stream()
                .map(Map.Entry::getKey)
                .limit(3)
                .collect(Collectors.toList());
    }

    private double calculateIC(String text) {
        if (text.length() < 2) return 0.0;
        int[] frequencies = new int[ALPHABET_SIZE];
        for (char c : text.toCharArray()) {
            frequencies[c - 'A']++;
        }
        double sum = 0.0;
        for (int freq : frequencies) {
            sum += freq * (freq - 1.0);
        }
        return sum / (text.length() * (text.length() - 1.0));
    }

    private char findBestKeyCharForColumn(String column, String language) {
        double minChiSquared = Double.POSITIVE_INFINITY;
        char bestKeyChar = 'A';
        for (int shift = 0; shift < ALPHABET_SIZE; shift++) {
            StringBuilder decryptedColumn = new StringBuilder();
            for (char c : column.toCharArray()) {
                int decryptedCharIndex = (c - 'A' - shift + ALPHABET_SIZE) % ALPHABET_SIZE;
                decryptedColumn.append((char) ('A' + decryptedCharIndex));
            }
            double chiSquared = calculateChiSquared(decryptedColumn.toString(), language);
            if (chiSquared < minChiSquared) {
                minChiSquared = chiSquared;
                bestKeyChar = (char) ('A' + shift);
            }
        }
        return bestKeyChar;
    }

    private double calculateChiSquared(String text, String language) {
        if (text.isEmpty()) return Double.POSITIVE_INFINITY;

        Map<Byte, Float> expectedFrequencies = Frequency.tables.get(language);
        if (expectedFrequencies == null) return Double.POSITIVE_INFINITY;

        Map<Byte, Long> observedCounts = text.chars()
                .mapToObj(c -> (byte) c)
                .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));

        double chiSquared = 0.0;
        int letterCount = 0;

        // Count only the letters for calculating expected counts
        for (char c : text.toCharArray()) {
            if (Character.isLetter(c)) {
                letterCount++;
            }
        }
        if (letterCount == 0) return Double.POSITIVE_INFINITY;

        for (Map.Entry<Byte, Float> entry : expectedFrequencies.entrySet()) {
            byte character = entry.getKey();
            float expectedPercent = entry.getValue();

            // Only compare letter frequencies
            if (Character.isLetter((char) (character & 0xFF))) {
                double expectedCount = (letterCount * expectedPercent) / 100.0;
                long observedCount = observedCounts.getOrDefault(character, 0L)
                        + observedCounts.getOrDefault((byte) Character.toLowerCase(character), 0L);

                if (expectedCount > 0) {
                    chiSquared += Math.pow(observedCount - expectedCount, 2) / expectedCount;
                }
            }
        }
        return chiSquared;
    }
}