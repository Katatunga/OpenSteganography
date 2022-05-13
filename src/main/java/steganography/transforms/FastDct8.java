package steganography.transforms;

/*
 * Fast discrete cosine transform algorithms (Java)
 *
 * Copyright (c) 2017 Project Nayuki. (MIT License)
 * https://www.nayuki.io/page/fast-discrete-cosine-transform-algorithms
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of
 * this software and associated documentation files (the "Software"), to deal in
 * the Software without restriction, including without limitation the rights to
 * use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of
 * the Software, and to permit persons to whom the Software is furnished to do so,
 * subject to the following conditions:
 * - The above copyright notice and this permission notice shall be included in
 *   all copies or substantial portions of the Software.
 * - The Software is provided "as is", without warranty of any kind, express or
 *   implied, including but not limited to the warranties of merchantability,
 *   fitness for a particular purpose and noninfringement. In no event shall the
 *   authors or copyright holders be liable for any claim, damages or other
 *   liability, whether in an action of contract, tort or otherwise, arising from,
 *   out of or in connection with the Software or the use or other dealings in the
 *   Software.
 */


public class FastDct8 implements Transform<double[][]> {

    /**
     * <p>Computes the scaled DCT type II on the provided 8x8 matrix and returns the result. It normalizes
     * the provided values to conform to the JPEG Standard.</p>
     * <p>The inverse of this function is {@link #reverse(double[][])}, except for rounding errors.</p>
     * @param values the matrix of doubles to transform
     * @throws NullPointerException if the matrix or any array in it is {@code null}
     */
    // This method was added to conveniently transform matrices from spatial domain into so-called "JPEG domain".
    public double[][] forward(double[][] values) {
        double[][] transposed = new double[8][8];
        double[][] result = new double[8][8];

        // restrict ([0,255]) and normalize (-128) all values
        for (int i = 0; i < result.length; i++) {
            for (int j = 0; j < result[i].length; j++) {
                result[i][j] = Math.max(0, Math.min(255, values[i][j])) - 128;
            }
        }

        // perform FDCT for each row and store result in a transposed matrix
        for (int row = 0; row < result.length; row++) {
            forward(result[row]);
            for (int col = 0; col < result[row].length; col++) {
                transposed[col][row] = result[row][col];
            }
        }

        // perform FDCT for each row of the transposed matrix
        // and store the transposed result in the result matrix
        for (int row = 0; row < transposed.length; row++) {
            forward(transposed[row]);
            for (int col = 0; col < transposed[row].length; col++) {
                result[col][row] = transposed[row][col];
            }
        }

        return result;
    }

    /**
     * Computes the scaled DCT type II on the specified length-8 array in place.
     * <p>The inverse of this function is {@link #reverse}, except for rounding errors.</p>
     * @param vector the vector of numbers to transform
     * @throws NullPointerException if the array is {@code null}
     */
    // This method is obtained unaltered from Nayuki Project. See license above.
    private void forward(double[] vector) {
        // Algorithm by Arai, Agui, Nakajima, 1988. For details, see:
        // https://web.stanford.edu/class/ee398a/handouts/lectures/07-TransformCoding.pdf#page=30
        final double v0, v1, v2, v3, v4, v5, v6, v7, v8, v9,
                v10, v11, v12, v13, v14, v15, v16, v17, v18, v19,
                v20, v21, v22, v23, v24, v25, v26, v27, v28;


        v0 = vector[0] + vector[7];
        v1 = vector[1] + vector[6];
        v2 = vector[2] + vector[5];
        v3 = vector[3] + vector[4];
        v4 = vector[3] - vector[4];
        v5 = vector[2] - vector[5];
        v6 = vector[1] - vector[6];
        v7 = vector[0] - vector[7];

        v8 = v0 + v3;
        v9 = v1 + v2;
        v10 = v1 - v2;
        v11 = v0 - v3;
        v12 = -v4 - v5;
        v13 = (v5 + v6) * A[3];
        v14 = v6 + v7;

        v15 = v8 + v9;
        v16 = v8 - v9;
        v17 = (v10 + v11) * A[1];
        v18 = (v12 + v14) * A[5];

        v19 = -v12 * A[2] - v18;
        v20 = v14 * A[4] - v18;

        v21 = v17 + v11;
        v22 = v11 - v17;
        v23 = v13 + v7;
        v24 = v7 - v13;

        v25 = v19 + v24;
        v26 = v23 + v20;
        v27 = v23 - v20;
        v28 = v24 - v19;

        vector[0] = S[0] * v15;
        vector[1] = S[1] * v26;
        vector[2] = S[2] * v21;
        vector[3] = S[3] * v28;
        vector[4] = S[4] * v16;
        vector[5] = S[5] * v25;
        vector[6] = S[6] * v22;
        vector[7] = S[7] * v27;
    }

    /**
     * <p>Computes the scaled DCT type III on the provided 8x8 matrix and returns the result. It denormalizes
     * the provided values to conform to the JPEG Standard.</p>
     * <p>The inverse of this function is {@link #forward(double[][])}, except for rounding errors.</p>
     * @param values the matrix of doubles to transform
     * @throws NullPointerException if the matrix or any array in it is {@code null}
     */
    // This method was added to conveniently inverse the transform
    // of matrices from so-called "JPEG domain" into spatial domain.
    public double[][] reverse(double[][] values) {
        double[][] transposed = new double[8][8];
        double[][] result = new double[8][8];

        for (int i = 0; i < result.length; i++) {
            System.arraycopy(values[i], 0, result[i], 0, 8);
        }

        // perform IDCT for each row and store result in a transposed matrix
        for (int row = 0; row < result.length; row++) {
            reverse(result[row]);
            for (int col = 0; col < result[row].length; col++) {
                transposed[col][row] = result[row][col];
            }
        }

        // perform IDCT for each row of the transposed matrix
        // and store the transposed result in a new matrix
        for (int row = 0; row < transposed.length; row++) {
            reverse(transposed[row]);
            for (int col = 0; col < transposed[row].length; col++) {
                result[col][row] = transposed[row][col];
            }
        }

        // denormalize and round all values, restrict them to [0,255]
        for (int i = 0; i < result.length; i++) {
            for (int j = 0; j < result[i].length; j++) {
                result[i][j] = Math.round(result[i][j] + 128);
                result[i][j] = Math.max(0, Math.min(255, result[i][j]));
            }
        }

        return result;
    }


    /**
     * Computes the scaled DCT type III on the specified length-8 array in place.
     * <p>The inverse of this function is {@link #forward}, except for rounding errors.</p>
     * @param vector the vector of numbers to transform
     * @throws NullPointerException if the array is {@code null}
     */
    // This method is obtained unaltered from Nayuki Project. See license above.
    private void reverse(double[] vector) {
        // A straightforward inverse of the forward algorithm
        final double v0, v1, v2, v3, v4, v5, v6, v7, v8, v9,
                v10, v11, v12, v13, v14, v15, v16, v17, v18, v19,
                v20, v21, v22, v23, v24, v25, v26, v27, v28;

        v15 = vector[0] / S[0];
        v26 = vector[1] / S[1];
        v21 = vector[2] / S[2];
        v28 = vector[3] / S[3];
        v16 = vector[4] / S[4];
        v25 = vector[5] / S[5];
        v22 = vector[6] / S[6];
        v27 = vector[7] / S[7];

        v19 = (v25 - v28) / 2;
        v20 = (v26 - v27) / 2;
        v23 = (v26 + v27) / 2;
        v24 = (v25 + v28) / 2;

        v7  = (v23 + v24) / 2;
        v11 = (v21 + v22) / 2;
        v13 = (v23 - v24) / 2;
        v17 = (v21 - v22) / 2;

        v8 = (v15 + v16) / 2;
        v9 = (v15 - v16) / 2;

        v18 = (v19 - v20) * A[5];  // Different from original
        v12 = (v19 * A[4] - v18) / (A[2] * A[5] - A[2] * A[4] - A[4] * A[5]);
        v14 = (v18 - v20 * A[2]) / (A[2] * A[5] - A[2] * A[4] - A[4] * A[5]);

        v6 = v14 - v7;
        v5 = v13 / A[3] - v6;
        v4 = -v5 - v12;
        v10 = v17 / A[1] - v11;

        v0 = (v8 + v11) / 2;
        v1 = (v9 + v10) / 2;
        v2 = (v9 - v10) / 2;
        v3 = (v8 - v11) / 2;

        vector[0] = (v0 + v7) / 2;
        vector[1] = (v1 + v6) / 2;
        vector[2] = (v2 + v5) / 2;
        vector[3] = (v3 + v4) / 2;
        vector[4] = (v3 - v4) / 2;
        vector[5] = (v2 - v5) / 2;
        vector[6] = (v1 - v6) / 2;
        vector[7] = (v0 - v7) / 2;
    }


    /*---- Tables of constants ----*/

    private static double[] S = new double[8];
    private static double[] A = new double[6];

    static {
        double[] C = new double[8];
        for (int i = 0; i < C.length; i++) {
            C[i] = Math.cos(Math.PI / 16 * i);
            S[i] = 1 / (4 * C[i]);
        }
        S[0] = 1 / (2 * Math.sqrt(2));
        // A[0] is unused
        A[1] = C[4];
        A[2] = C[2] - C[6];
        A[3] = C[4];
        A[4] = C[6] + C[2];
        A[5] = C[6];
    }

}

