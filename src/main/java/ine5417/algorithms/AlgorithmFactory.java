package ine5417.algorithms;

import ine5417.algorithms.Algorithm;
import ine5417.algorithms.implementations.RepeatingXOR;
import ine5417.algorithms.implementations.SingleKeyXOR;


import java.util.Map;
import java.util.Optional;

public final class AlgorithmFactory {

    private static final Map<String, Class<? extends Algorithm>> ALGORITHMS = Map.of(
            SingleKeyXOR.getIdentifier(), SingleKeyXOR.class,
            RepeatingXOR.getIdentifier(), RepeatingXOR.class
    );

    private AlgorithmFactory() {}

    public static Optional<Algorithm> getAlgorithm(String identifier) {
        Class<? extends Algorithm> algorithmClass = ALGORITHMS.get(identifier);

        if (algorithmClass != null) {
            try {
                return Optional.of(algorithmClass.getDeclaredConstructor().newInstance());
            } catch (Exception e) {
                return Optional.empty();
            }
        }
        return Optional.empty();
    }
}