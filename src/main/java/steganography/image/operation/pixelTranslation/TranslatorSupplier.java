package steganography.image.operation.pixelTranslation;

/**
 * Functional interface to supply consuming classes with PixelTranslators. Can be used as
 * a sort of short factory pattern.
 * @param <T> descendants of PixelTranslator to provide consuming classes with
 */
@FunctionalInterface
public interface TranslatorSupplier<T extends PixelTranslator> {

    /**
     * Returns a PixelTranslator according to the respective implementation. If not stated otherwise
     * by the implementing class, this method returns a new PixelTranslator.
     * @param argbValues the ARGB values as provided by {@link java.awt.image.BufferedImage BufferedImage}
     * @param width the width of the 2D-Array the PixelTranslator should provide as {@code values}
     * @return a new PixelTranslator
     */
    T get(int[] argbValues, int width);
}
