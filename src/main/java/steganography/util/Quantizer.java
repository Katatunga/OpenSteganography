package steganography.util;

/**
 * <p>This class offers static methods to so quantization operations in accordance with the JPEG Standard,
 * but by calculating the appropriate quantization tables with a base table and scale factors.</p>
 * <p>The quantization is intended to use for quality reduction in 8x8 pixel image elements, transformed
 * to the DCT domain.</p>
 * <p>The algorithm was taken from <a href="http://www.faqs.org/rfcs/rfc2035.html">RFC2035</a> and
 * adapted to the needs of this project. References to the RFC will be made throughout the code as follows:
 * <em>(see {@link Quantizer} [section x])</em></p>
 */
public final class Quantizer {

    /**
     * The base table for the luminance channel as provided in <em>(see {@link Quantizer} Appendix A)</em>
     */
    private static final int[] jpeg_luma_quantizer = {
        16, 11, 10, 16,  24,  40,  51,  61,
        12, 12, 14, 19,  26,  58,  60,  55,
        14, 13, 16, 24,  40,  57,  69,  56,
        14, 17, 22, 29,  51,  87,  80,  62,
        18, 22, 37, 56,  68, 109, 103,  77,
        24, 35, 55, 64,  81, 104, 113,  92,
        49, 64, 78, 87, 103, 121, 120, 101,
        72, 92, 95, 98, 112, 100, 103,  99
    };

    /**
     * <p>Returns a JPEG Quantization table corresponding to the provided Q factor.</p>
     * <p>This is the adapted algorithm from RFC2035 <em>(see {@link Quantizer} Appendix A)</em></p>
     * <p>The algorithm was modularized and calculation of chrominance tables was removed.</p>
     * @param qf Quality factor for calculating the quantization table
     */
    private static double[] makeTables(float qf)
    {
        int factor = getScaling(qf);

        double[] lum_q = new double[64];
        for (int i = 0; i < 64; i++) {
            lum_q[i] = quantizationValue(i, factor);
        }
        return lum_q;
    }

    /**
     * <p>Returns the scaling factor according to the provided quality factor {@code qf}.</p>
     * <p>The value is multiplied by 100 and then truncated to the interval [1,99].</p>
     * @param qf the desired quality factor to quantize a matrix with
     * @return a scaling factor to scale each element of the base table to produce a quantization table corresponding
     *      to {@code qf}
     */
    private static int getScaling(float qf) {
        int q = (int) (qf * 100);
        q = Math.max(1, Math.min(q, 100));

        return q < 50 ? 5000 / q : 200 - q * 2;
    }

    /**
     * Returns a single quantization value corresponding to the provided coordinates in a quantization table
     * represented as a 8x8 matrix.
     * @param x the x coordinate in a 8x8 matrix of the quantization value
     * @param y the y coordinate in a 8x8 matrix of the quantization value
     * @param qf the quality factor to use to calculate the quantization value
     * @return the quantization value at position (y,x) in a 8x8 matrix
     */
    public static int quantizationValue(int x, int y, float qf) {
        return quantizationValue(y * 8 + x, qf);
    }

    /**
     * Returns a single quantization value corresponding to the provided index in a quantization table represented
     * as a 1D-Array.
     * @param index the index of the quantization value in a 1D-Array
     * @param qf the quality factor to use to calculate the quantization value
     * @return the quantization value at index {@code index} in a 1D-Array
     */
    public static int quantizationValue(int index, float qf) {
        return quantizationValue(index, getScaling(qf));
    }

    /**
     * Returns a single quantization value corresponding to the provided index in a quantization table represented
     * as a 1D-Array.
     * @param index the index of the quantization value in a 1D-Array
     * @param q the q value as returned by {@link #getScaling(float)} to use to calculate the quantization value
     * @return the quantization value at index {@code index} in a 1D-Array
     */
    private static int quantizationValue(int index, int q) {
        int lq = ( jpeg_luma_quantizer[index] * q + 50) / 100;
        return Math.min(255, Math.max(1, lq));
    }

    /**
     * <p>Quantizes (divides and rounds) the provided values with a quantization matrix created according to the provided
     * quality factor{@code qf}.</p>
     * <p>Utilizes the {@link #jpeg_luma_quantizer base table} and the algorithm provided in
     * <em>{@link Quantizer} Appendix A</em></p>
     * <p>Parameter {@code values} should be of size 8x8 only. Any more will be ignored, any less will result in an
     * {@link IndexOutOfBoundsException}</p>
     * @param values matrix of values (intended for image elements in DCT domain) to quantize
     * @param qf quality factor to quantize {@code values} according to
     * @throws IndexOutOfBoundsException if {@code value.length < 8 || value[i].length < 8}
     * @return quantized {@code values}
     */
    public static double[][] quantize(double[][] values, float qf) {
        return quantize(values, makeTables(qf));
    }

    /**
     * <p>Quantizes (divides and rounds) the provided values with the provided quantization matrix {@code qT}.</p>
     * <p>Parameter {@code values} should be of size 8x8 only. Any more will be ignored, any less will result in an
     * {@link IndexOutOfBoundsException}</p>
     * @param values matrix of values (intended for image elements in DCT domain) to quantize
     * @param qT quantization matrix to quantize {@code values} with
     * @throws IndexOutOfBoundsException if {@code value.length < 8 || value[i].length < 8}
     * @return quantized {@code values}
     */
    public static double[][] quantize(double[][] values, double[] qT) {
        double[][] quantized = new double[8][8];
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                quantized[i][j] = Math.round(values[i][j] / qT[i*8 + j]);
            }
        }
        return quantized;
    }

    /**
     * <p>Dequantizes (multiplies) the provided values according to the provided quality factor{@code qf}.</p>
     * <p>Parameter {@code values} should be of size 8x8 only. Any more will be ignored, any less will result in an
     * {@link IndexOutOfBoundsException}</p>
     * @param values matrix of values (intended for image elements in DCT domain) to quantize
     * @param qf quality factor to multiply {@code values} according to
     * @throws IndexOutOfBoundsException if {@code value.length < 8 || value[i].length < 8}
     * @return dequantized {@code values}
     */
    public static double[][] dequantize(double[][] values, float qf) {
        return dequantize(values, makeTables(qf));
    }

    /**
     * <p>Dequantizes (multiplies) the provided values with the provided quantization matrix {@code qT}.</p>
     * <p>Parameter {@code values} should be of size 8x8 only. Any more will be ignored, any less will result in an
     * {@link IndexOutOfBoundsException}</p>
     * @param values matrix of values (intended for image elements in DCT domain) to quantize
     * @param qT quantization matrix to multiply {@code values} with
     * @throws IndexOutOfBoundsException if {@code value.length < 8 || value[i].length < 8}
     * @return dequantized {@code values}
     */
    public static double[][] dequantize(double[][] values, double[] qT) {
        double[][] dequantized = new double[8][8];
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                dequantized[i][j] = values[i][j] * qT[i*8 + j];
            }
        }
        return dequantized;
    }

    /**
     * <p>Prequantizes ({@link #quantize(double[][], float) quantizes} and
     * {@link #dequantize(double[][], float) dequantizes}) the provided values  according to the provided quality
     * factor {@code qf}. This procedure reduces the quality of image elements in DCT domain in accordance to
     * {@link Quantizer}</p>
     * <p>Parameter {@code values} should be of size 8x8 only. Any more will be ignored, any less will result in an
     * {@link IndexOutOfBoundsException}</p>
     * @param values matrix of values (intended for image elements in DCT domain) to prequantize
     * @param qf quality factor to prequantize {@code values} according to
     * @throws IndexOutOfBoundsException if {@code value.length < 8 || value[i].length < 8}
     * @return prequantized {@code values}
     */
    public static double[][] prequantize(double[][] values, float qf) {
        return prequantize(values, makeTables(qf));
    }

    /**
     * <p>Prequantizes ({@link #quantize(double[][], float) quantizes} and
     * {@link #dequantize(double[][], float) dequantizes}) the provided values with the provided quantization
     * matrix {@code qT}. This procedure reduces the quality of image elements in DCT domain in accordance to
     * {@link Quantizer}</p>
     * <p>Parameter {@code values} should be of size 8x8 only. Any more will be ignored, any less will result in an
     * {@link IndexOutOfBoundsException}</p>
     * @param values matrix of values (intended for image elements in DCT domain) to prequantize
     * @param qT quantization matrix to prequantize {@code values} with
     * @throws IndexOutOfBoundsException if {@code value.length < 8 || value[i].length < 8}
     * @return prequantized {@code values}
     */
    public static double[][] prequantize(double[][] values, double[] qT) {
        double[][] prequantized = new double[8][8];
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                double val = Math.round(values[i][j] / qT[i*8 + j]) * qT[i*8 + j];
                prequantized[i][j] = val;
            }
        }
        return prequantized;
    }
}
