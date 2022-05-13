package steganography.image.innerStructure.embedders.dct;

import steganography.image.innerStructure.embedders.BlockEmbedder;
import steganography.image.operation.pixelTranslation.PixelTranslator;
import steganography.image.operation.pixelTranslation.TranslatorSupplier;
import steganography.transforms.Transform;

public abstract class DctEmbedder extends BlockEmbedder {

    /**
     * Y-coordinate of reference coefficient in Cover Element (DCT domain)
     */
    protected final int refY;
    /**
     * X-coordinate of reference coefficient in Cover Element (DCT domain)
     */
    protected final int refX;

    protected final TranslatorSupplier<PixelTranslator> translatorSupplier;
    protected final Transform<double[][]> dctTransform;
    protected final Float qf;

    /**
     * <p>Creates the abstract parent class for embedders that use the DCT transformed values of Cover Elements to
     * embed.</p>
     * @param translatorSupplier supplies a {@link PixelTranslator} that translates the Cover Elements from their
     *                           default values (possibly int) to doubles of the interval [0,255] and back
     * @param dctTransform transforms matrices of doubles to and from DCT domain
     * @param qf desired (JPEG) quality factor for which the embedder should prepare the decoding. If possible,
     *           embedders adjust the way the cover elements are modified to ensure compression resistance up to qf.
     */
    protected DctEmbedder(TranslatorSupplier<PixelTranslator> translatorSupplier, Transform<double[][]> dctTransform,
                          Float qf) {
        this.translatorSupplier = translatorSupplier;
        this.dctTransform = dctTransform;
        this.qf = qf;
        this.refY = 0;
        this.refX = 4;
    }

    /**
     * <p>Creates the abstract parent class for embedders that use the DCT transformed values of Cover Elements to
     * embed.</p>
     * @param translatorSupplier supplies a {@link PixelTranslator} that translates the Cover Elements from their
     *                           default values (possibly int) to doubles of the interval [0,255] and back
     * @param dctTransform transforms matrices of doubles to and from DCT domain
     * @param qf desired (JPEG) quality factor for which the embedder should prepare the decoding. If possible,
     *           embedders adjust the way the cover elements are modified to ensure compression resistance up to qf.
     * @param refX x coordinate 0 (inclusive) - 8 (exclusive) of dct coefficient to change
     * @param refY y coordinate 0 (inclusive) - 8 (exclusive) of dct coefficient to change
     */
    protected DctEmbedder(TranslatorSupplier<PixelTranslator> translatorSupplier, Transform<double[][]> dctTransform,
                          Float qf, int refX, int refY) {
        this.translatorSupplier = translatorSupplier;
        this.dctTransform = dctTransform;
        this.qf = qf;

        if (Math.min(refX, refY) < 0 || Math.max(refX, refY) > 7)
            throw new IllegalArgumentException(
                    "Arguments refX and refY must be between 0 (inclusive) and 8 (exclusive)");
        this.refX = refX;
        this.refY = refY;
    }
}
