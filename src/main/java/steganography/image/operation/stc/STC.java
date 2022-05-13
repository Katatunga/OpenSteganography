package steganography.image.operation.stc;

import steganography.util.ArrayUtils;

import java.util.Arrays;
import java.util.BitSet;
import java.util.Random;

public class STC {
    /**
     * More literal implementation of the viterbi algorithm than what is used now. For educational purposes
     * @param x cover elements
     * @param message message
     * @param H_hat submatrix
     * @param h constraint height
     * @return optimal y
     */
    private static int[] viterbi_stc(int[] x, int[] message, int[] H_hat, int h) { // H_hat in integer representation
        int msgLen = message.length;
        int cvrLen = x.length;

        if (msgLen > cvrLen / 2)
            throw new IllegalArgumentException(String.format("Message (%d) is longer than Cover (%d) / 2", msgLen, cvrLen));

        // forward part of the Viterbi algorithm
        int w = H_hat.length;

        int stateAmount = (int) Math.pow(2, h);
        int halfStateAmount = stateAmount / 2;

        double[] rho = new double[cvrLen]; // represents distortion values -> method call later
        Arrays.fill(rho, 1); // constant distortion for testing

        int[][] path = new int[msgLen * w][stateAmount];

        double[] wght = new double[stateAmount];
        Arrays.fill(wght, Double.POSITIVE_INFINITY);
        wght[0] = 0;

        int indx = 0;
        int indm;
        for (indm = 0; indm < msgLen; indm++) {
            for (int hh_col : H_hat) {
                double[] newwght = new double[wght.length];
                for (int k = 0; k < stateAmount; k++) {
                    double w0 = wght[k] + x[indx] * rho[indx];
                    double w1 = wght[k ^ hh_col] + (1 - x[indx]) * rho[indx];
                    path[indx][k] = w1 < w0 ? 1 : 0;
                    newwght[k] = Math.min(w0, w1);
                }
                indx++;
                wght = newwght;
            }

            // prune states
            for (int j = 0; j < halfStateAmount; j++) {
                wght[j] = wght[2 * j + message[indm]];
            }

            for (int j = halfStateAmount; j < stateAmount; j++) {
                wght[j] = Double.POSITIVE_INFINITY;
            }
        }

        int[] y = new int[cvrLen]; // stc-encoded message to encode in stego-object
        int minInd = ArrayUtils.minInd(wght);

        // backward part of the Viterbi algorithm
        int state = minInd; // this part was wrong in pseudocode: // int state = 0
        indx--;
        for (--indm; indm >= 0; indm--) {
            state = 2 * state + message[indm]; // This part was wrong in pseudocode: executed after for-loop
            for (int j = w-1; j >= 0; j--) {
                y[indx] = path[indx][state];
                state = state ^ (y[indx] * H_hat[j]);
                indx--;
            }
        }
        return y;
    }
}
