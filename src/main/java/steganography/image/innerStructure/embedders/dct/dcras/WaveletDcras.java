package steganography.image.innerStructure.embedders.dct.dcras;

import steganography.transforms.Transform;
import steganography.transforms.Wavelet;
import steganography.image.operation.pixelTranslation.PixelTranslator;
import steganography.image.operation.pixelTranslation.TranslatorSupplier;
import steganography.util.Quantizer;

/**
 * Somewhat usable class, please experiment
 */
public class WaveletDcras extends DcrasEmbedder {

    private final int qf;
    protected final Wavelet wvt;

    public WaveletDcras(TranslatorSupplier<PixelTranslator> translatorSupplier,
                        Transform<double[][]> dctTransform, Wavelet waveletTransform,
                        Float qf) {

        super(translatorSupplier, dctTransform, qf);
        this.qf = Float.valueOf(qf * 100).intValue();
        this.wvt = waveletTransform;
    }

    @Override
    protected int pickEmbeddingChunk(PixelTranslator[] chunks) {
        double[] energies = new double[chunks.length];
        double max = -Double.MAX_VALUE;
        int maxInd = -1;

        // transform chunks and save the defining values of each block, while noting the maximum
        for (int i = 0; i < chunks.length; i++) {
            // reduce quality to get relatable data
            double[][] sChunk = Quantizer.prequantize(chunks[i].getValues(), this.qf);
            // not inverting brings higher stability for whatever reason
            // sChunk = FastDct8.inverseTransform2D(sChunk);

            energies[i] = this.wvt.detailSum(sChunk);

            if (energies[i] > max) {
                max = energies[i];
                maxInd = i;
            }
        }

        return maxInd;
    }
}
