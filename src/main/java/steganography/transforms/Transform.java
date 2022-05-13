package steganography.transforms;

/**
 * Implementing Classes transform provided inputs from some domain into another.
 * Input and output classes are the same.
 * @param <T> Input and output class
 */
public interface Transform<T> {

    /**
     * Perform the forward transform into this Transforms domain.
     * @param values values to be transformed
     * @return transformed values
     */
    T forward(T values);

    /**
     * Perform the reverse transform into the domain the input of {@link #forward} is in.
     * @param values transformed values to reverse the Transform of
     * @return values in the {@link #forward forwards} input domain
     */
    T reverse(T values);
}
