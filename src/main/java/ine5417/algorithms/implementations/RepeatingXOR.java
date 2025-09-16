package ine5417.algorithms.implementations;

import org.apache.commons.lang3.tuple.Triple;

import ine5417.commom.Frequency;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

@Component
public class RepeatingXOR extends SingleKeyXOR {
    public static final String IDENTIFIER = "repeatingxor";

    public static String getIdentifier() {
        return RepeatingXOR.IDENTIFIER;
    }

    @Override
    public byte[] cipher(byte[] plaintext, byte[] key) {
        return execute(plaintext, key);
    }

    @Override
    public byte[] decipher(byte[] encrypted, byte[] key) {
        return execute(encrypted, key);
    }

    @Override
    public List<Triple<String, byte[], Float>> bruteforce(byte[] ciphertext) {
        List<Triple<String, byte[], Float>> results = new ArrayList<>();

        // Iterate through all available language frequency profiles from your Frequency class
        for (String lang : Frequency.availableLanguages) {
            // Get the corresponding frequency table from your Frequency class
            Map<Byte, Float> langTable = Frequency.tables.get(lang);

            CrackResult bestResultForLang = null;

            // Step 1: Find the most likely key lengths (we'll test the top 3)
            List<Integer> keyLengths = findKeyLengths(ciphertext, 3);

            for (int keyLength : keyLengths) {
                if (keyLength == 0) continue;

                byte[] key = new byte[keyLength];

                // Step 2: Transpose the ciphertext into blocks
                List<byte[]> transposedBlocks = transposeCiphertext(ciphertext, keyLength);

                // Step 3: Solve each block as a single-byte XOR cipher
                for (int i = 0; i < keyLength; i++) {
                    key[i] = solveSingleByteXor(transposedBlocks.get(i), langTable).key;
                }

                // Decrypt the full ciphertext with the discovered key
                byte[] plaintext = execute(ciphertext, key);
                float score = scorePlaintext(plaintext, langTable);

                // Check if this key is the best one we've found for this language so far
                if (bestResultForLang == null || score > bestResultForLang.score) {
                    bestResultForLang = new CrackResult(key, plaintext, score);
                }
            }

            if (bestResultForLang != null) {
                results.add(Triple.of(lang, bestResultForLang.key, bestResultForLang.score));
            }
        }

        // Sort results from best to worst score
        results.sort((a, b) -> b.getRight().compareTo(a.getRight()));
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
        int maxKeyLength = Math.min(40, ciphertext.length / 2);

        for (int keyLength = 2; keyLength <= maxKeyLength; keyLength++) {
            int chunksToTest = Math.min(4, ciphertext.length / (keyLength * 2));
            if (chunksToTest == 0) continue;

            float totalNormalizedDistance = 0;
            for(int i = 0; i < chunksToTest; i++) {
                byte[] chunk1 = Arrays.copyOfRange(ciphertext, i * keyLength, (i + 1) * keyLength);
                byte[] chunk2 = Arrays.copyOfRange(ciphertext, (i + 1) * keyLength, (i + 2) * keyLength);
                totalNormalizedDistance += (float) hammingDistance(chunk1, chunk2) / keyLength;
            }
            distances.put(keyLength, totalNormalizedDistance / chunksToTest);
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

    private SingleByteXorResult solveSingleByteXor(byte[] block, Map<Byte, Float> langTable) {
        SingleByteXorResult bestResult = null;
        for (int key = 0; key < 256; key++) {
            byte[] plaintext = new byte[block.length];
            for (int i = 0; i < block.length; i++) {
                plaintext[i] = (byte) (block[i] ^ key);
            }
            float score = scorePlaintext(plaintext, langTable);
            if (bestResult == null || score > bestResult.score) {
                bestResult = new SingleByteXorResult((byte) key, score);
            }
        }
        return bestResult != null ? bestResult : new SingleByteXorResult((byte) 0, 0f); // Fallback
    }

    private float scorePlaintext(byte[] text, Map<Byte, Float> langTable) {
        float score = 0;
        for (byte b : text) {
            score += langTable.getOrDefault(b, 0f);
        }
        return score;
    }

    /**
     * CORRECTED VERSION of the execute method.
     * It now creates a new byte array for the output instead of modifying
     * the input array, preventing the original ciphertext from being corrupted.
     *
     * @param input The byte array to be processed (plaintext or ciphertext).
     * @param key The XOR key.
     * @return A new byte array containing the result.
     */
    private byte[] execute(byte[] input, byte[] key) {
        byte[] output = new byte[input.length];
        for (int i = 0; i < input.length; i++) {
            output[i] = (byte) (input[i] ^ key[i % key.length]);
        }
        return output;
    }

    // --- Private Helper Classes for Cracking Results ---

    private record SingleByteXorResult(byte key, float score) {}

    private record CrackResult(byte[] key, byte[] plaintext, float score) {}
}






