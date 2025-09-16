package ine5417.algorithms.implementations;

import ine5417.algorithms.Algorithm;
import ine5417.records.BruteForce;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.List;

public class Vigenere implements Algorithm {

    // Frequência das letras no idioma Inglês. Usado para o ataque de força bruta.
    // Fonte: Wikipedia
    private static final double[] ENGLISH_FREQUENCIES = {
            0.08167, 0.01492, 0.02782, 0.04253, 0.12702, 0.02228, 0.02015, // A-G
            0.06094, 0.06966, 0.00153, 0.00772, 0.04025, 0.02406, 0.06749, // H-N
            0.07507, 0.01929, 0.00095, 0.05987, 0.06327, 0.09056, 0.02758, // O-U
            0.00978, 0.02360, 0.00150, 0.01974, 0.00074  // V-Z
    };

    private static final int ALPHABET_SIZE = 26;
    private static final int MAX_KEY_LENGTH_TO_TEST = 20; // Limite para o teste de força bruta

    @Override
    public byte[] cipher(byte[] plaintext, byte[] key) {
        return process(plaintext, key, true);
    }

    @Override
    public byte[] decipher(byte[] toDecrypt, byte[] key) {
        return process(toDecrypt, key, false);
    }

    /**
     * Processa a (de)criptografia Vigenère.
     *
     * @param input   Os dados de entrada (texto claro ou cifrado).
     * @param key     A chave.
     * @param encrypt True para criptografar, false para descriptografar.
     * @return O resultado do processamento.
     */
    private byte[] process(byte[] input, byte[] key, boolean encrypt) {
        if (key == null || key.length == 0) {
            return input; // Retorna o texto original se a chave for inválida
        }

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
                // Mantém caracteres não alfabéticos (espaços, pontuação, etc.)
                output[i] = (byte) charIn;
            }
        }
        return output;
    }

    /**
     * Tenta quebrar a cifra por força bruta usando criptoanálise.
     *
     * @param ciphertext O texto cifrado a ser atacado.
     * @return Uma lista de possíveis soluções (chave, texto decifrado, pontuação).
     */
    @Override
    public List<BruteForce> bruteforce(byte[] ciphertext) {
        // 1. Extrair apenas as letras do texto cifrado para análise
        StringBuilder lettersOnly = new StringBuilder();
        for (byte b : ciphertext) {
            char c = (char) b;
            if (Character.isLetter(c)) {
                lettersOnly.append(Character.toUpperCase(c));
            }
        }
        String analyzableText = lettersOnly.toString();

        // 2. Adivinhar o comprimento da chave usando Índice de Coincidência
        int bestKeyLength = findKeyLength(analyzableText);

        // 3. Quebrar as Cifras de César para cada coluna do texto
        StringBuilder guessedKey = new StringBuilder();
        for (int i = 0; i < bestKeyLength; i++) {
            // Monta a sub-string para a coluna i
            StringBuilder column = new StringBuilder();
            for (int j = i; j < analyzableText.length(); j += bestKeyLength) {
                column.append(analyzableText.charAt(j));
            }

            // Encontra o melhor caractere da chave para esta coluna
            guessedKey.append(findBestKeyCharForColumn(column.toString()));
        }

        // 4. Descriptografar o texto com a chave encontrada
        String keyFound = guessedKey.toString();
        byte[] plaintextBytes = decipher(ciphertext, keyFound.getBytes(StandardCharsets.UTF_8));
        String resultText = new String(plaintextBytes, StandardCharsets.UTF_8);

        // 5. Criar o objeto BruteForce com todos os dados
        // A pontuação é inversamente proporcional ao tamanho da chave (chaves menores são mais "certas")
        float score = 1.0f / bestKeyLength;

        BruteForce result = new BruteForce("English", resultText, keyFound, score);

        return Collections.singletonList(result);
    }

    /**
     * Encontra o comprimento da chave mais provável.
     */
    private int findKeyLength(String text) {
        int bestKeyLength = 1;
        double bestIc = 0.0;

        for (int keyLength = 2; keyLength <= MAX_KEY_LENGTH_TO_TEST; keyLength++) {
            double avgIc = 0.0;
            // Calcula o IC médio para todas as colunas
            for (int i = 0; i < keyLength; i++) {
                StringBuilder column = new StringBuilder();
                for (int j = i; j < text.length(); j += keyLength) {
                    column.append(text.charAt(j));
                }
                avgIc += calculateIC(column.toString());
            }
            avgIc /= keyLength;

            // O IC de inglês é ~0.067. O comprimento da chave que mais se aproxima
            // deste valor é o mais provável.
            if (Math.abs(avgIc - 0.067) < Math.abs(bestIc - 0.067)) {
                bestIc = avgIc;
                bestKeyLength = keyLength;
            }
        }
        return bestKeyLength;
    }

    /**
     * Calcula o Índice de Coincidência (IC) de um texto.
     */
    private double calculateIC(String text) {
        if (text.length() < 2) { // prevents a zero division
            return 0.0;
        }
        int[] frequencies = new int[ALPHABET_SIZE];
        for (char c : text.toCharArray()) {
            frequencies[c - 'A']++;
        }

        double sum = 0.0;
        for (int freq : frequencies) {
            sum += freq * (freq - 1);
        }

        return sum / (text.length() * (text.length() - 1.0));
    }

    /**
     * Encontra o caractere da chave para uma coluna (Cifra de César) usando Qui-quadrado.
     */
    private char findBestKeyCharForColumn(String column) {
        double minChiSquared = Double.POSITIVE_INFINITY;
        char bestKeyChar = 'A';

        for (int shift = 0; shift < ALPHABET_SIZE; shift++) {
            StringBuilder decryptedColumn = new StringBuilder();
            for (char c : column.toCharArray()) {
                int decryptedCharIndex = (c - 'A' - shift + ALPHABET_SIZE) % ALPHABET_SIZE;
                decryptedColumn.append((char) ('A' + decryptedCharIndex));
            }

            double chiSquared = calculateChiSquared(decryptedColumn.toString());
            if (chiSquared < minChiSquared) {
                minChiSquared = chiSquared;
                bestKeyChar = (char) ('A' + shift);
            }
        }
        return bestKeyChar;
    }

    /**
     * Calcula o valor Qui-quadrado de um texto contra as frequências do inglês.
     */
    private double calculateChiSquared(String text) {
        if (text.isEmpty()) {
            return Double.POSITIVE_INFINITY; // Um texto vazio tem um score infinitamente ruim
        }
        int[] observedFrequencies = new int[ALPHABET_SIZE];
        for (char c : text.toCharArray()) {
            observedFrequencies[c - 'A']++;
        }

        double chiSquared = 0.0;
        for (int i = 0; i < ALPHABET_SIZE; i++) {
            double expected = text.length() * ENGLISH_FREQUENCIES[i];
            double observed = observedFrequencies[i];
            chiSquared += Math.pow(observed - expected, 2) / expected;
        }
        return chiSquared;
    }
}