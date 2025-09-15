package ine5417.algorithms;

import ine5417.algorithms.implementations.RepeatingXOR;
import ine5417.algorithms.implementations.SingleKeyXOR;

import java.util.Map;
import java.util.Optional;

/**
 * Class used for maintenance of algorithms implementations and obtainability
 */
public final class AlgorithmFactory {

    private static final Map<String, Class<? extends Algorithm>> ALGORITHMS = Map.of(
            SingleKeyXOR.getIdentifier(), SingleKeyXOR.class,
            RepeatingXOR.getIdentifier(), RepeatingXOR.class
    );

    /**
     * Private constructor to prevent instantiation of this utility class.
     */
    private AlgorithmFactory() {
    }

    /**
     * Retrieves a new instance of an algorithm based on its unique string identifier.
     * This factory method uses reflection to create the object.
     *
     * @param identifier The unique string key for the desired algorithm (e.g., "singleKeyXOR").
     * @return An {@link Optional} containing the created {@code Algorithm} instance if the
     * identifier is valid and instantiation is successful; otherwise, an empty {@code Optional}.
     */
    public static Optional<Algorithm> getAlgorithm(String identifier) {
        Class<? extends Algorithm> algorithmClass = ALGORITHMS.get(identifier);

        if (algorithmClass != null) {
            try {
                // Create a new instance of the algorithm class
                return Optional.of(algorithmClass.getDeclaredConstructor().newInstance());
            } catch (Exception e) {
                // If instantiation fails for any reason, return empty
                return Optional.empty();
            }
        }
        return Optional.empty();
    }
}