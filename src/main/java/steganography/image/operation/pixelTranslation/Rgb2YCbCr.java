package steganography.image.operation.pixelTranslation;

public class Rgb2YCbCr implements PixelTranslator {

    /**
     * ARGB values of pixels, as provided by {@link java.awt.image.BufferedImage#getRGB BufferedImage.getRGB()}.
     * Used as input to calculate the Y, Cb and Cr values and as output for the reverse calculation.
     */
    private int[] argbValues;

    /**
     * Y channel of the YCbCr color space, represented as double in the interval [0, 255]
     */
    private final double[] y;

    /**
     * Cb channel of the YCbCr color space, represented as double in the interval [0, 255]
     */
    private final double[] cb;

    /**
     * Cr channel of the YCbCr color space, represented as double in the interval [0, 255]
     */
    private final double[] cr;

    private final int width;

    private final int height;

    private final Channel mainChannel;

    /**
     * Channels that can be chosen as main output from this class.
     */
    public enum Channel {
        Y,
        CB,
        CR
    }

    /**
     * <p>Creates a new ColorTranslator that converts between RGB and YCbCr color spaces.</p>
     * <p>This implementation reduces the colors to one (selectable) channel (parameter {@code channel}).
     * This is a lossy reduction, so to convert back to the original colors, the same instance of this translator
     * has to be used.</p>
     * <p>To transfer the dimensions from a 1D-Array to a 2D-Array the provided {@code width} is used. This
     * translator only accepts {@code argbValues.length} and {@code width} combinations that lead to symmetric
     * 2D-Arrays.</p>
     * @param argbValues values of pixels (ARGB-Integers) as provided by {@link java.awt.image.BufferedImage#getRGB
     *                  BufferedImage.getRGB()}
     * @param width width of the resulting 2D-Array
     * @param channel {@link Channel} to use as main value, Y If omitted
     */
    public Rgb2YCbCr(int[] argbValues, int width, Channel channel) {
        this.width = width;

        Double sHeight = argbValues.length / (double) width;
        if (sHeight != sHeight.intValue())
            throw new IllegalArgumentException(
                    "Combination of argbValues.length and width would result in an asymmetrical 2D-Array.");
        this.height = sHeight.intValue();

        this.argbValues = argbValues;
        this.y    = new double[argbValues.length];
        this.cb   = new double[argbValues.length];
        this.cr   = new double[argbValues.length];
        this.mainChannel = channel;
        calcYCbCr();
    }

    /**
     * <p>Creates a new ColorTranslator that converts between RGB and YCbCr color spaces.</p>
     * <p>This implementation reduces the colors to the Y channel. This is a lossy reduction, so to convert back to
     * the original colors, the same instance of this translator has to be used.</p>
     * <p>To transfer the dimensions from a 1D-Array to a 2D-Array the provided {@code width} is used. This
     * translator only accepts {@code argbValues.length} and {@code width} combinations that lead to symmetric
     * 2D-Arrays.</p>
     * <p> To use other channels, see {@link #Rgb2YCbCr(int[], int, Channel)}.</p>
     * @param argbValues values of pixels (ARGB-Integers) as provided by {@link java.awt.image.BufferedImage#getRGB
     *                  BufferedImage.getRGB()}
     * @param width width of the resulting 2D-Array
     */
    public Rgb2YCbCr(int[] argbValues, int width) {
        this(argbValues, width, Channel.Y);
    }

    /**
     * @return the main channel to be returned by {@link #getValues()} and modified by {@link #setValues}.
     */
    protected double[] getMain() {
        switch (this.mainChannel) {
            case Y:  return this.y;
            case CB: return this.cb;
            case CR: return this.cr;
        }
        return this.y;
    }

    @Override
    public double[][] getValues() {
        double[][] dValues = new double[width][width];
        for (int i = 0; i < width; i++) {
            System.arraycopy(getMain(), i * width, dValues[i], 0, width);
        }
        return dValues;
    }

    @Override
    public void setValues(double[][] values) {
        for (int i = 0; i < values.length; i++) {
            System.arraycopy(values[i], 0, getMain(), i * width, width);
        }
    }

    @Override
    public int getWidth() {
        return this.width;
    }

    @Override
    public int[] asARGB() {
        calcARGB();
        return this.argbValues;
    }

    @Override
    public double get(int x, int y) {
        return this.getMain()[(y * width) + x];
    }

    @Override
    public void set(int x, int y, double value) {
        this.getMain()[(y * width) + x] = value;
    }

    /**
     * <p>Calculates the Y, Cb and Cr values, using {@link #argbValues} as input and {@link #y}, {@link #cb} and
     * {@link #cr} as output.</p>
     * <p>Calculation according to (book) [Bilddatenkompression // ISBN: 978-3-8348-0472-3 // p. 183-184]</p>
     */
    private void calcYCbCr() {
        for (int i = 0; i < y.length; i++) {
            double r = ((argbValues[i] >> 16) & 255);
            double g = ((argbValues[i] >> 8)  & 255);
            double b = ( argbValues[i]        & 255);

             y[i] = Math.min(Math.max(0, (  0.2990 * r + 0.5870 * g + 0.1140 * b))      , 255);
            cb[i] = Math.min(Math.max(0, (- 0.1687 * r - 0.3313 * g + 0.5000 * b) + 128), 255);
            cr[i] = Math.min(Math.max(0, (  0.5000 * r - 0.4187 * g - 0.0813 * b) + 128), 255);
        }
    }

    /**
     * <p>Calculates the ARGB values, using {@link #y}, {@link #cb} and {@link #cr} as input and {@link #argbValues}
     * as output. This method conserves the alpha value in the original {@code argbValue}
     * (given to {@link #Rgb2YCbCr(int[], int) the contructor}), provided it was not changed in the meantime.</p>
     * <p>Calculation according to (book) [Bilddatenkompression // ISBN: 978-3-8348-0472-3 // p. 183-184]</p>
     */
    private void calcARGB() {
        int[] newRgbValues = new int[argbValues.length];
        for (int i = 0; i < y.length; i++) {
            double yVal  =  y[i];
            double cbVal = cb[i] - 128;
            double crVal = cr[i] - 128;

            int[] rgb = new int[3];

            rgb[0] = (int) Math.min(Math.max(0, Math.round(yVal +                  1.4020 * crVal)), 255);
            rgb[1] = (int) Math.min(Math.max(0, Math.round(yVal - 0.3441 * cbVal - 0.7141 * crVal)), 255);
            rgb[2] = (int) Math.min(Math.max(0, Math.round(yVal + 1.7720 * cbVal                 )), 255);

            // Conserve alpha value
            int alpha = argbValues[i] & (0xff << 24);
            newRgbValues[i] = alpha | (rgb[0]  << 16) | (rgb[1] << 8) | rgb[2];
        }
        argbValues = newRgbValues;
    }
}
