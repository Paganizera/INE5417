package ine5417.algorithms.implementations;

import ine5417.commom.Frequency;
import ine5417.records.BruteForce;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;

@Component
public class RepeatingXOR extends SingleKeyXOR {
    public static final String IDENTIFIER = "repeatingxor";

    public static String getIdentifier() {
        return RepeatingXOR.IDENTIFIER;
    }

    // ... (cipher and decipher methods are unchanged) ...
    @Override
    public byte[] cipher(byte[] plaintext, byte[] key) { return execute(plaintext, key); }
    @Override
    public byte[] decipher(byte[] encrypted, byte[] key) { return execute(encrypted, key); }


    @Override
    public List<BruteForce> bruteforce(byte[] ciphertext) {
        List<BruteForce> results = new ArrayList<>();
        for (String lang : Frequency.availableLanguages) {
            Map<Byte, Float> langTable = new HashMap<>(Frequency.tables.get(lang)); // Use a copy
            if (ciphertext.length < 30) {
                langTable.put((byte) ' ', 7f);
            }

            BruteForce bestResultForLang = new BruteForce("", "", "", -1f);
            List<Integer> keyLengths = findKeyLengths(ciphertext, 8);

            for (int keyLength : keyLengths) {
                if (keyLength == 0) continue;

                byte[] key = new byte[keyLength];
                List<byte[]> transposedBlocks = transposeCiphertext(ciphertext, keyLength);

                for (int i = 0; i < keyLength; i++) {
                    key[i] = findBestSingleByteKey(transposedBlocks.get(i), langTable).getKey();
                }

                byte[] plaintext = execute(ciphertext, key);
                float score = calculateScore(plaintext, langTable);

                if (score > bestResultForLang.score()) {
                    bestResultForLang = new BruteForce(
                            lang,
                            new String(plaintext, StandardCharsets.UTF_8),
                            new String(key, StandardCharsets.UTF_8),
                            score
                    );
                }
            }
            results.add(bestResultForLang);
        }
        results.sort((a, b) -> b.score().compareTo(a.score()));
        return results;
    }

    private int hammingDistance(byte[] a, byte[] b) {
        int distance = 0;
        for (int i = 0; i < a.length; i++) {
            distance += Integer.bitCount(a[i] ^ b[i]);
        }
        return distance;
    }


    private List<Integer> findKeyLengths(byte[] ciphertext, int count) {
        Map<Integer, Float> distances = new HashMap<>();
        for (int keyLength = 2; keyLength <= Math.min(30,ciphertext.length / 5); keyLength++) {
            byte[] chunk1 = Arrays.copyOfRange(ciphertext, 0, keyLength);
            byte[] chunk2 = Arrays.copyOfRange(ciphertext, keyLength, 2 * keyLength);
            byte[] chunk3 = Arrays.copyOfRange(ciphertext, 2 * keyLength, 3 * keyLength);
            byte[] chunk4 = Arrays.copyOfRange(ciphertext, 3 * keyLength, 4 * keyLength);
            byte[] chunk5 = Arrays.copyOfRange(ciphertext, 4 * keyLength, 5 * keyLength);

            // Calculate normalized distance for all 10 pairs
            float dist1 = (float) hammingDistance(chunk1, chunk2) / keyLength;
            float dist2 = (float) hammingDistance(chunk1, chunk3) / keyLength;
            float dist3 = (float) hammingDistance(chunk1, chunk4) / keyLength;
            float dist4 = (float) hammingDistance(chunk1, chunk5) / keyLength;
            float dist5 = (float) hammingDistance(chunk2, chunk3) / keyLength;
            float dist6 = (float) hammingDistance(chunk2, chunk4) / keyLength;
            float dist7 = (float) hammingDistance(chunk2, chunk5) / keyLength;
            float dist8 = (float) hammingDistance(chunk3, chunk4) / keyLength;
            float dist9 = (float) hammingDistance(chunk3, chunk5) / keyLength;
            float dist10 = (float) hammingDistance(chunk4, chunk5) / keyLength;

            // Average the 10 distances
            float averageDistance = (dist1 + dist2 + dist3 + dist4 + dist5 + dist6 + dist7 + dist8 + dist9 + dist10) / 10.0f;
            distances.put(keyLength, averageDistance);
        }

        return distances.entrySet().stream()
                .sorted(Map.Entry.comparingByValue())
                .limit(count)
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
    }

    private List<byte[]> transposeCiphertext(byte[] ciphertext, int keyLength) {
        List<byte[]> blocks = new ArrayList<>();
        for (int i = 0; i < keyLength; i++) {
            byte[] block = new byte[(ciphertext.length + keyLength - 1 - i) / keyLength];
            for (int j = 0; j * keyLength + i < ciphertext.length; j++) {
                block[j] = ciphertext[j * keyLength + i];
            }
            blocks.add(block);
        }
        return blocks;
    }

    private byte[] execute(byte[] input, byte[] key) {
        if (key == null || key.length == 0) return input;
        byte[] output = new byte[input.length];
        for (int i = 0; i < input.length; i++) {
            output[i] = (byte) (input[i] ^ key[i % key.length]);
        }
        return output;
    }
}