package ine5417.algorithms.implementations;

import ine5417.algorithms.Algorithm;
import ine5417.commom.Frequency;
import ine5417.records.BruteForce;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@SuppressWarnings("unused")
public class A1Z26 implements Algorithm {
    public static final String IDENTIFIER = "a1z26";

    public static String getIdentifier() { return A1Z26.IDENTIFIER; }

    @Override
    public byte[] cipher(byte[] plaintext, byte[] key) {
        String text = new String(plaintext, StandardCharsets.UTF_8);
        StringBuilder ciphertext = new StringBuilder();

        for (char c : text.toCharArray()) {
            if (c >= 'a' && c <= 'z') {
                ciphertext.append(c - 'a' + 1).append(" ");
            } else if (c >= 'A' && c <= 'Z') {
                ciphertext.append(c - 'A' + 1).append(" ");
            } else if (c == ' ') {
                ciphertext.append(" ");
            }
        }


        return ciphertext.toString().trim().getBytes(StandardCharsets.UTF_8);
    }

    @Override
    public byte[] decipher(byte[] toDecrypt, byte[] key) {
        String text = new String(toDecrypt, StandardCharsets.UTF_8);
        StringBuilder plaintext = new StringBuilder();

        // Divide por espaço simples.
        // O parâmetro -1 garante que tokens vazios não sejam descartados.
        String[] tokens = text.split(" ", -1);

        for (String token : tokens) {
            if (token.isEmpty()) {
                // Se encontrarmos um token vazio, significa que havia dois espaços seguidos
                // (o separador + o espaço do texto). Então, restauramos o espaço.
                plaintext.append(" ");
                continue;
            }

            try {
                int num = Integer.parseInt(token);
                if (num >= 1 && num <= 26) {
                    plaintext.append((char) ('a' + num - 1));
                }
            } catch (NumberFormatException e) {
                // Ignora tokens que não são números válidos
            }
        }

        return plaintext.toString().getBytes(StandardCharsets.UTF_8);
    }

    @Override
    public List<BruteForce> bruteforce(byte[] ciphertext) {
        List<BruteForce> finalResults = new ArrayList<>();

        byte[] potentialPlaintext = decipher(ciphertext, null);
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

    private float calculateScore(byte[] plaintext, Map<Byte, Float> frequencyTable) {
        float score = 0;
        for (byte b : plaintext) {
            score += frequencyTable.getOrDefault(b, 0.0f);
        }
        return score;
    }
}