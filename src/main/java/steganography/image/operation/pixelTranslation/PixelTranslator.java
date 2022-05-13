package steganography.image.operation.pixelTranslation;

/**
 * <p>Implementing classes should transfer the representation of pixels from the {@code int[]} representation as
 * provided by {@link java.awt.image.BufferedImage#getRGB(int, int, int, int, int[], int, int) BufferedImage.getRGB()}
 * to a {@code double[][]} (2D-Array) representation.</p>
 * <p>The methods offer possibilities to simply transfer ot to modify values between transfers.</p>
 */
public interface PixelTranslator {

    /**
     * @return the translators main values as a 2D-Array representation of doubles.
     */
    double[][] getValues();

    /**
     * Sets the provided values as the translators values.
     * @param values values to be set as chunks values
     */
    void setValues(double[][] values);

    /**
     * @return the width used to construct the 2D-Array.
     */
    int getWidth();

    /**
     * @return the values as a {@code int[]} representation as provided by
     * {@link java.awt.image.BufferedImage#getRGB(int, int, int, int, int[], int, int) BufferedImage.getRGB}.
     */
    int[] asARGB();

    /**
     * Returns the value at position [y][x] of the 2D-Array
     * @param x - column of the chunks position
     * @param y - row of the chunks position
     * @return the double-value located at position [y][x]
     */
    double get(int x, int y);

    /**
     * Sets the provided value at the position [y][x] of the 2D-Array
     * @param x - column of the 2D-Array
     * @param y - row of the 2D-Array
     * @param value - value to set at position [y][x]
     */
    void set(int x, int y, double value);
}
