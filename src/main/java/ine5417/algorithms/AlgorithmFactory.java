package ine5417.algorithms;

import org.reflections.Reflections;

import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * This factory automatically discovers all classes that implement the Algorithm
 * interface by reading their public static IDENTIFIER field.
 */
public final class AlgorithmFactory {

    private static final Map<String, Class<? extends Algorithm>> ALGORITHMS = initializeAlgorithms();

    private AlgorithmFactory() {}

    private static Map<String, Class<? extends Algorithm>> initializeAlgorithms() {
        Reflections reflections = new Reflections("ine5417.algorithms.implementations");
        Set<Class<? extends Algorithm>> algorithmClasses = reflections.getSubTypesOf(Algorithm.class);

        return algorithmClasses.stream()
                .collect(Collectors.toMap(
                        AlgorithmFactory::getIdentifierFromClass, clazz -> clazz
                ));
    }


    private static String getIdentifierFromClass(Class<? extends Algorithm> algorithmClass) {
        try {
            return (String) algorithmClass.getField("IDENTIFIER").get(null);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new IllegalStateException(
                    "Algorithm class " + algorithmClass.getName() + " must have a public static final String IDENTIFIER field.", e);
        }
    }

    /**
     * Retrieves a new instance of an algorithm based on its identifier.
     */
    public static Optional<Algorithm> getAlgorithm(String identifier) {
        Class<? extends Algorithm> algorithmClass = ALGORITHMS.get(identifier.toLowerCase());
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