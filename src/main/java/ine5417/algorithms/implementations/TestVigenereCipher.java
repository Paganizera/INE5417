package ine5417.algorithms.implementations;

import ine5417.algorithms.Algorithm;
import ine5417.algorithms.implementations.Vigenere;
import ine5417.records.BruteForce;

import java.nio.charset.StandardCharsets;
import java.util.List;

class TestVigenereCipher {

    public static void main(String[] args) {
        System.out.println("--- Iniciando Testes da Classe VigenereCipher ---");

        // Instancia o algoritmo a ser testado
        Algorithm vigenere = new Vigenere();

        // --- Dados de Teste ---
        // Usamos um texto com maiúsculas, minúsculas, pontuação e espaços
        // para garantir que o algoritmo lida corretamente com todos os casos.
        final String KEY = "TESTANDO";
        final String PLAINTEXT = "Este e um texto de teste sem nenhuma pontuacao como esperado...";

        // Este é o resultado esperado da cifragem do PLAINTEXT com a KEY acima.
        final String EXPECTED_CIPHERTEXT = "X" + "WLX E HP HXBLH DR WSLXW LEZ QSGLMFA CRBMYSVAB FCFS WLPRUOWS...".toLowerCase();

        System.out.println("\n-------------------------------------------------");

        // --- 1. Teste de Cifragem (Encryption) ---
        System.out.println("1. Teste de Cifragem...");
        System.out.println("   Texto Original: \"" + PLAINTEXT + "\"");
        System.out.println("   Chave         : \"" + KEY + "\"");

        byte[] cipheredBytes = vigenere.cipher(PLAINTEXT.getBytes(StandardCharsets.UTF_8), KEY.getBytes(StandardCharsets.UTF_8));
        String actualCiphertext = new String(cipheredBytes, StandardCharsets.UTF_8);

        System.out.println("   Resultado Esperado: \"" + EXPECTED_CIPHERTEXT + "\"");
        System.out.println("   Resultado Obtido  : \"" + actualCiphertext + "\"");

        if (EXPECTED_CIPHERTEXT.equals(actualCiphertext)) {
            System.out.println("   >>> RESULTADO: PASSOU ✅");
        } else {
            System.out.println("   >>> RESULTADO: FALHOU ❌");
        }

        System.out.println("-------------------------------------------------");

        // --- 2. Teste de Decifragem (Decryption) ---
        System.out.println("2. Teste de Decifragem...");
        System.out.println("   Texto Cifrado: \"" + EXPECTED_CIPHERTEXT + "\"");
        System.out.println("   Chave        : \"" + KEY + "\"");

        byte[] decipheredBytes = vigenere.decipher(EXPECTED_CIPHERTEXT.getBytes(StandardCharsets.UTF_8), KEY.getBytes(StandardCharsets.UTF_8));
        String actualPlaintext = new String(decipheredBytes, StandardCharsets.UTF_8);

        System.out.println("   Resultado Esperado: \"" + PLAINTEXT + "\"");
        System.out.println("   Resultado Obtido  : \"" + actualPlaintext + "\"");

        if (PLAINTEXT.equals(actualPlaintext)) {
            System.out.println("   >>> RESULTADO: PASSOU ✅");
        } else {
            System.out.println("   >>> RESULTADO: FALHOU ❌");
        }

        System.out.println("-------------------------------------------------");

        // --- 3. Teste de Força Bruta (Brute-force) ---
        System.out.println("3. Teste de Força Bruta (Criptoanálise)...");
        System.out.println("   Analisando o texto cifrado: \"" + EXPECTED_CIPHERTEXT + "\"");

        List<BruteForce> results = vigenere.bruteforce(EXPECTED_CIPHERTEXT.getBytes(StandardCharsets.UTF_8));

        // Verificações
        boolean testPassed = false;
        if (results != null && !results.isEmpty()) {
            BruteForce topResult = results.get(0);
            System.out.println("   Melhor resultado encontrado:");
            System.out.println("   - Chave    : \"" + topResult.key() + "\" (Esperado: \"" + KEY + "\")");
            System.out.println("   - Texto    : \"" + topResult.result() + "\"");
            System.out.println("   - Pontuação    : \"" + topResult.score() + "\"");

            // Verifica se a chave encontrada (ignorando case) e o texto são os corretos.
            if (topResult.key().equalsIgnoreCase(KEY) && topResult.result().equals(PLAINTEXT)) {
                testPassed = true;
            }
        } else {
            System.out.println("   Nenhum resultado foi encontrado pela força bruta.");
        }

        if (testPassed) {
            System.out.println("   >>> RESULTADO: PASSOU ✅");
        } else {
            System.out.println("   >>> RESULTADO: FALHOU ❌");
        }

        System.out.println("-------------------------------------------------");
        System.out.println("\n--- Testes Concluídos ---");
    }
}