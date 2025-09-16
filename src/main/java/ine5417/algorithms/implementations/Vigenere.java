package ine5417.algorithms.implementations;

import ine5417.algorithms.Algorithm;
import ine5417.commom.Frequency; // Importa a nova classe de frequência
import ine5417.records.BruteForce;

import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

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

        // 1. Extrai apenas as letras para encontrar o tamanho da chave.
        // A lógica do IC é uma boa heurística e não precisa ser multilíngue.
        StringBuilder lettersOnly = new StringBuilder();
        for (byte b : ciphertext) if (Character.isLetter((char) b)) lettersOnly.append(Character.toUpperCase((char) b));
        String analyzableText = lettersOnly.toString();

        int bestKeyLength = findKeyLength(analyzableText);

        // 2. Itera sobre cada idioma disponível na classe Frequency
        for (String lang : Frequency.availableLanguages) {
            // 3. Encontra a chave mais provável PARA ESTE IDIOMA
            StringBuilder guessedKey = new StringBuilder();
            for (int i = 0; i < bestKeyLength; i++) {
                StringBuilder column = new StringBuilder();
                for (int j = i; j < analyzableText.length(); j += bestKeyLength) {
                    column.append(analyzableText.charAt(j));
                }
                guessedKey.append(findBestKeyCharForColumn(column.toString(), lang));
            }

            // 4. Descriptografa e calcula o score
            String keyFound = guessedKey.toString();
            byte[] plaintextBytes = decipher(ciphertext, keyFound.getBytes(StandardCharsets.UTF_8));
            String resultText = new String(plaintextBytes, StandardCharsets.UTF_8);

            // O score é baseado na análise Qui-quadrado do texto final.
            // Menor Qui-quadrado = melhor. Invertemos para que maior seja melhor.
            double chiSquaredScore = calculateChiSquared(resultText, lang);
            float finalScore = (float) (1 / (chiSquaredScore + 1e-9)); // Adiciona epsilon para evitar divisão por zero

            allResults.add(new BruteForce(lang, resultText, keyFound, finalScore));
        }

        // 5. Ordena os resultados pelo score, do maior para o menor
        allResults.sort(Comparator.comparing(BruteForce::score).reversed());
        return allResults;
    }

    private int findKeyLength(String text) {
        int bestKeyLength = 1;
        double bestIc = 0.0;
        for (int keyLength = 2; keyLength <= MAX_KEY_LENGTH_TO_TEST; keyLength++) {
            double avgIc = 0.0;
            for (int i = 0; i < keyLength; i++) {
                StringBuilder column = new StringBuilder();
                for (int j = i; j < text.length(); j += keyLength) column.append(text.charAt(j));
                avgIc += calculateIC(column.toString());
            }
            avgIc /= keyLength;
            if (Math.abs(avgIc - 0.067) < Math.abs(bestIc - 0.067)) {
                bestIc = avgIc;
                bestKeyLength = keyLength;
            }
        }
        return bestKeyLength;
    }

    private double calculateIC(String text) {
        if (text.length() < 2) return 0.0;
        int[] frequencies = new int[ALPHABET_SIZE];
        for (char c : text.toCharArray()) frequencies[c - 'A']++;
        double sum = 0.0;
        for (int freq : frequencies) sum += freq * (freq - 1);
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
            // Passa o idioma para o cálculo
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
        if (expectedFrequencies == null) return Double.POSITIVE_INFINITY; // Idioma não encontrado

        // Conta a frequência dos bytes no texto de entrada
        Map<Byte, Long> observedCounts = text.chars()
                .mapToObj(c -> (byte) c)
                .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));

        double chiSquared = 0.0;
        int textLength = text.length();

        // Itera sobre os caracteres esperados no idioma
        for (Map.Entry<Byte, Float> entry : expectedFrequencies.entrySet()) {
            byte character = entry.getKey();
            float expectedPercent = entry.getValue();

            double expectedCount = (textLength * expectedPercent) / 100.0;
            long observedCount = observedCounts.getOrDefault(character, 0L);

            if (expectedCount > 0) { // Evita divisão por zero
                chiSquared += Math.pow(observedCount - expectedCount, 2) / expectedCount;
            }
        }
        return chiSquared;
    }
}