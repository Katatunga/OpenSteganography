package steganography.transforms;

import jwave.Transform;
import jwave.transforms.FastWaveletTransform;
import jwave.transforms.wavelets.daubechies.Daubechies20;
import jwave.transforms.wavelets.daubechies.Daubechies8;
import jwave.transforms.wavelets.haar.Haar1;

public class Wavelet implements steganography.transforms.Transform<double[][]> {

    /**
     * The {@link Transform} to use
     */
    private final Transform transform;

    /**
     * <p>Creates an Object to perform Wavelet Transforms with.</p>
     * <p>This Class uses {@link jwave} <a href=https://github.com/graetz23/JWave>(Github)</a>.</p>
     * @param transform {@link Transform} to use
     */
    public Wavelet(Transform transform) {
        this.transform = transform;
    }

    /**
     * <p>Creates an Object to perform Wavelet Transforms with.</p>
     * <p>This Class uses {@link jwave} <a href=https://github.com/graetz23/JWave>(Github)</a>.</p>
     * <p>The default {@link Transform} is {@link FastWaveletTransform}({@link Daubechies8})</p>
     */
    public Wavelet() {
        this.transform = new Transform(new FastWaveletTransform(new Daubechies8()));
    }

    /**
     * Perform the forward transform into Wavelet domain by one level in each dimension.
     * @return the transformed values
     */
    @Override
    public double[][] forward(double[][] spValues) {
        return this.transform.forward(spValues, 1, 1);
    }

    /**
     * Perform the reverse transform from Wavelet domain by one level in each dimension to spatial domain.
     * @return the transformed values one level lower in Wavelet domain or in spatial domain
     */
    @Override
    public double[][] reverse(double[][] trValues) {
        return this.transform.reverse(trValues, 1, 1);
    }

    /**
     * <p>This method performs one level wavelet transform according to {@link #transform} and then sums the absolute
     * values of all subbands excluding LL (approximation). The result can be used to judge the amount of details
     * (as in structure or noise) in the provided spatial values.</p>
     * @param spValues spatial values to sum the details of
     * @return the sum of all values in the one level wavelet subbands excluding LL (approximation)
     */
    public double detailSum(double[][] spValues) {
        double[][] wvValues = forward(spValues);
        double sum = 0;
        int yLength = wvValues.length;
        int xLength = wvValues[0].length;
        for (int i = 0; i < yLength; i++) {
            for (int j = 0; j < xLength; j++) {
                if (i < yLength / 2 && j < xLength / 2) continue;

                sum += Math.abs(wvValues[i][j]);
            }
        }
        return sum;
    }
}
