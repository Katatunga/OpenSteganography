package steganography.image.innerStructure.distortion;

import steganography.image.operation.pixelTranslation.PixelTranslator;
import steganography.image.operation.pixelTranslation.TranslatorSupplier;
import steganography.transforms.Transform;

/**
 * Implementation of J-Uniward. See paper:
 * <em>Universal distortion function for steganography in an arbitrary domain</em>
 * (DOI: 10.1186/1687-417X-2014-1)
 */
public class JUniward implements DistortionFunction<int[]> {
    private final Transform<double[][]> wvt;
    protected final TranslatorSupplier<PixelTranslator> translatorSupplier;

    public JUniward(TranslatorSupplier<PixelTranslator> translatorSupplier, Transform<double[][]> waveletTransform) {
        this.translatorSupplier = translatorSupplier;
        this.wvt = waveletTransform;
    }

    @Override
    public Double calculateDistortion(int[] original, int[] embedded) {
        return jUniward(
                this.translatorSupplier.get(original, 8).getValues(),
                this.translatorSupplier.get(embedded, 8).getValues()
        );
    }

    // Universal distortion function for steganography in an arbitrary domain
    // p. 3 - 3.2 Distortion function (non-side-informed embedding)
    protected double jUniward(double[][] or_spatial, double[][] ch_spatial) {
        double[][] or_wavelet = this.wvt.forward(or_spatial);
        double[][] ch_wavelet = this.wvt.forward(ch_spatial);

        final int n1 = ch_wavelet.length;
        final int n2 = ch_wavelet[0].length;

        double delta = 0.015625; // 2^{−6} see P. 8-9

        double sum = 0;

        for (int u = 0; u < n1; u++) {
            for (int v = 0; v < n2; v++) {
                if (u < n1 / 2 && v < n2 / 2) continue;
                sum += Math.abs(or_wavelet[u][v] - ch_wavelet[u][v]) / (delta + Math.abs(or_wavelet[u][v]));
            }
        }
        return sum;
    }
}
