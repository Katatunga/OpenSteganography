package steganography.image.innerStructure.embedders.dct.dmas;

import steganography.exceptions.encoder.EmbedderInputException;
import steganography.image.innerStructure.embedders.dct.DctEmbedder;
import steganography.image.operation.pixelTranslation.PixelTranslator;
import steganography.image.operation.pixelTranslation.TranslatorSupplier;
import steganography.transforms.Transform;
import steganography.util.Quantizer;

/**
 * <p>This Class is an Embedder that uses an adapted version of "Dither modulation based adaptive steganography" (DMAS)
 * to embed Bits into provided Cover Elements.</p>
 * <p>DMAS is explained in this <a href="https://doi.org/10.1007/s11042-017-4506-3">paper</a>. References to the
 * paper in the following code or in the inheriting classes are made as follows:
 * <em>(see {@link DmasEmbedder} p. [x])</em></p>
 */
public class DmasEmbedder extends DctEmbedder {
    /**
     * <p>Creates an Embedder that uses an adapted version of "Dither modulation based adaptive steganography"
     * (DMAS - <em>see {@link DmasEmbedder})</em>) to embed Bits into cover elements.</p>
     * @param translatorSupplier supplies a {@link PixelTranslator} that translates the Cover Elements from their
     *                           default values (possibly int) to doubles of the interval [0,255] and back
     * @param dctTransform transforms matrices of doubles to and from DCT domain
     * @param qf desired (JPEG) quality factor for which the embedder should prepare the decoding.
     *           This embedder adjusts the amount by which it modifies the cover elements to try
     *           to ensure compression resistance up to qf.
     */
    public DmasEmbedder(TranslatorSupplier<PixelTranslator> translatorSupplier, Transform<double[][]> dctTransform,
                           Float qf) {
        super(translatorSupplier, dctTransform, qf);
    }

    /**
     * <p>Creates an Embedder that uses an adapted version of "Dither modulation based adaptive steganography"
     * (DMAS - <em>see {@link DmasEmbedder})</em>) to embed Bits into cover elements.</p>
     * @param translatorSupplier supplies a {@link PixelTranslator} that translates the Cover Elements from their
     *                           default values (possibly int) to doubles of the interval [0,255] and back
     * @param dctTransform transforms matrices of doubles to and from DCT domain
     * @param qf desired (JPEG) quality factor for which the embedder should prepare the decoding.
     *           This embedder adjusts the amount by which it modifies the cover elements to try
     *           to ensure compression resistance up to qf.
     * @param refX x coordinate 0 (inclusive) - 8 (exclusive) of dct coefficient to change
     * @param refY y coordinate 0 (inclusive) - 8 (exclusive) of dct coefficient to change
     */
    public DmasEmbedder(TranslatorSupplier<PixelTranslator> translatorSupplier, Transform<double[][]> dctTransform,
                           Float qf, int refX, int refY) {
        super(translatorSupplier, dctTransform, qf, refX, refY);
    }

    @Override
    public int[] embed(int[] cvrElem, boolean one) throws EmbedderInputException {
        acceptLength(cvrElem, 64);
        PixelTranslator chunk = this.translatorSupplier.get(cvrElem, 8);
        return encodeChunk(chunk, one).asARGB();
    }

    @Override
    public int[] flip(int[] cvrElem) throws EmbedderInputException {
        acceptLength(cvrElem, 64);
        PixelTranslator chunk = this.translatorSupplier.get(cvrElem, 8);
        return flipChunk(chunk).asARGB();
    }

    protected PixelTranslator encodeChunk(PixelTranslator chunk, boolean isOne) {
        double[][] dct = this.dctTransform.forward(chunk.getValues());
        dct = toNearest(isOne, dct);
        chunk.setValues(this.dctTransform.reverse(dct));
        return chunk;
    }

    protected PixelTranslator flipChunk(PixelTranslator chunk) {
        double[][] dct = this.dctTransform.forward(chunk.getValues());
        dct = toNearest(!isOne(dct), dct);
        chunk.setValues(this.dctTransform.reverse(dct));
        return chunk;
    }

    @Override
    public boolean representsOne(int[] cvrElem) throws EmbedderInputException {
        acceptLength(cvrElem, 64);
        PixelTranslator chunk = this.translatorSupplier.get(cvrElem, 8);
        return isOne(this.dctTransform.forward(chunk.getValues()));
    }

    private double getDelta() {
        return Math.max(Quantizer.quantizationValue(refX, refY, this.qf), 5);
    }

    protected boolean isOne(double[][] values) {
        return isOne(values[refY][refX]);
    }

    /**
     *
     * @param value value representing a 1 or 0
     * @return true, if the value represents a 1
     */
    private boolean isOne(double value) {
        double delta = getDelta();
        return (int) ((Math.abs(value) + (delta / 2)) / delta) % 2 == 0;
    }

    /**
     * @param embedOne true == embedding a 1
     * @param values block to change the reference of
     * @return {@code values} with reference changed to nearest intervalSet representing {@code embedOne}.
     */
    protected double[][] toNearest(boolean embedOne, double[][] values) {
        values[refY][refX] = toNearest(embedOne, values[refY][refX]);
        return values;
    }

    /**
     * Calculates nearest middle of intervalSet representing {@code embedOne}.
     * @param embedOne true == embedding a 1
     * @param value value to change
     * @return nearest middle of intervalSet representing {@code embedOne}.
     */
    private double toNearest(boolean embedOne, double value) {
        double delta = getDelta();
        double nearest = Math.round(value / delta) * delta;
        if (embedOne != isOne(nearest))
            nearest += value > nearest ? delta : -delta;
        return nearest;
    }
}
