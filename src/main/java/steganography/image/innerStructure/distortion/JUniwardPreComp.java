package steganography.image.innerStructure.distortion;

import steganography.transforms.FastDct8;
import steganography.image.operation.pixelTranslation.PixelTranslator;
import steganography.image.operation.pixelTranslation.TranslatorSupplier;
import steganography.transforms.Transform;
import steganography.util.Quantizer;

/**
 * Not actually used but could be inspirational while trying to combat "wet pixels" (see row 33 & 34)
 */
public class JUniwardPreComp extends JUniward {

    private final float qf;

    public JUniwardPreComp(TranslatorSupplier<PixelTranslator> translatorSupplier,
                           Transform<double[][]> waveletTransform, float qf) {

        super(translatorSupplier, waveletTransform);
        this.qf = qf;
    }

    @Override
    public Double calculateDistortion(int[] original, int[] embedded) {
        double[][] dOriginal = this.translatorSupplier.get(original, 8).getValues();
        double[][] dEmbedded = this.translatorSupplier.get(embedded, 8).getValues();

        double[][] dctOriginal = new FastDct8().forward(dOriginal);
        double[][] dctEmbedded = new FastDct8().forward(dEmbedded);

        double diff = diff(Quantizer.prequantize(dctOriginal, qf), Quantizer.prequantize(dctEmbedded, qf));

        // find a good relational value here and this could improve compression resistance immensely
        return diff == 0 ? 1000 : jUniward(dOriginal, dEmbedded);
    }

    private double diff(double[][] arr, double[][] brr) {
        double diff = 0;
        for (int i = 0; i < arr.length; i++) {
            for (int j = 0; j < arr[0].length; j++) {
                diff += Math.abs(arr[i][j] - brr[i][j]);
            }
        }
        return diff;
    }
}
