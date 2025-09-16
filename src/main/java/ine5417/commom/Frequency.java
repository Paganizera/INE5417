package ine5417.commom;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.Map.entry;

public final class Frequency {
        public static List<String> availableLanguages = new ArrayList<>();
        /**
         * A map where the key is a language code and the value is the corresponding character frequency table
         */
        public static Map<String,Map<Byte,Float>> tables = new HashMap<>();

        static {
            // Defines the character frequency for the English language.
            Map<Character, Float> englishFrequency = Map.ofEntries(
                    entry('e', 12.49f), entry('t', 9.28f), entry('a', 8.04f), entry('o', 7.64f),
                    entry('i', 7.57f), entry('n', 7.23f), entry('s', 6.51f), entry('r', 6.28f),
                    entry('h', 5.05f), entry('l', 4.07f), entry('d', 3.82f), entry('c', 3.34f),
                    entry('u', 2.73f), entry('m', 2.51f), entry('f', 2.40f), entry('p', 2.14f),
                    entry('g', 1.87f), entry('w', 1.68f), entry('y', 1.66f), entry('b', 1.48f),
                    entry('v', 1.05f), entry('k', 0.54f), entry('x', 0.23f), entry('j', 0.16f),
                    entry('q', 0.12f), entry('z', 0.09f), entry(' ', 5.0f)
            );

            // Defines the character frequency for the Portuguese language.
            Map<Character, Float> portugueseFrequency = Map.ofEntries(
                    entry('a', 14.63f), entry('e', 12.57f), entry('o', 10.73f), entry('s', 7.81f),
                    entry('r', 6.53f), entry('i', 6.18f), entry('n', 5.05f), entry('d', 4.99f),
                    entry('m', 4.74f), entry('u', 4.63f), entry('t', 4.34f), entry('c', 3.88f),
                    entry('l', 2.78f), entry('p', 2.52f), entry('q', 1.20f), entry('v', 1.58f),
                    entry('g', 1.30f), entry('h', 1.28f), entry('b', 1.04f), entry('f', 1.02f),
                    entry('z', 0.47f), entry('j', 0.40f), entry('x', 0.21f), entry('k', 0.02f),
                    entry('w', 0.01f), entry('y', 0.01f), entry(' ', 15.0f)
            );

            // Populates the public tables and list of available languages.
            tables.put("en", Frequency.convert(englishFrequency));
            tables.put("pt", Frequency.convert(portugueseFrequency));

            availableLanguages.add("en");
            availableLanguages.add("pt");
        }

        /**
         * Private constructor to prevent instantiation of this utility class.
         */
        private Frequency() {}

        /**
         * Private helper method to convert a frequency map from Character keys to Byte keys.
         * <p>
         * Note: This conversion only maps the direct byte value of the provided characters.
         *
         * @param map The source map with Character keys.
         * @return A new map with Byte keys.
         */
        private static Map<Byte, Float> convert(Map<Character, Float> map){
            return map.entrySet().stream()
                    .flatMap(e -> {
                        char c = e.getKey();
                        float freq = e.getValue();
                        if (Character.isLetter(c)) {
                            return Stream.of(
                                    Map.entry((byte) Character.toLowerCase(c), freq),
                                    Map.entry((byte) Character.toUpperCase(c), freq)
                            );
                        }
                        return Stream.of(Map.entry((byte) c, freq));
                    })
                    .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
        }
}