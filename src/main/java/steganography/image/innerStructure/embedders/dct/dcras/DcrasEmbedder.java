package steganography.image.innerStructure.embedders.dct.dcras;

import steganography.exceptions.encoder.EmbedderInputException;
import steganography.image.innerStructure.embedders.dct.DctEmbedder;
import steganography.image.operation.pixelTranslation.PixelTranslator;
import steganography.image.operation.pixelTranslation.TranslatorSupplier;
import steganography.transforms.Transform;
import steganography.util.ArrayUtils;
import steganography.util.Quantizer;

/**
 * <p>This Class is an Embedder that uses an adapted version of "DCT coefficient relationship based adaptive
 * steganography" (DCRAS) to embed Bits into provided Cover Elements.</p>
 * <p>DCRAS is explained in this <a href="http://ieeexplore.ieee.org/document/7299952/">paper</a>. References to the
 * paper in the following code or in the inheriting classes are made as follows:
 * <em>(see {@link DcrasEmbedder} p. [x])</em></p>
 */
public class DcrasEmbedder extends DctEmbedder {

    /**
     * <p>The amount of blocks to be used, while one being a reference block.</p>
     * <p>Should not be changed unless you know what you are doing.</p>
     */
    private static final int BLOCK_AMOUNT = 4;

    /**
     * The (relative) distance to keep from mean in relation to a quantization value
     */
    private static final float REL_DIST = .8f;

    /**
     * <p>Creates an Embedder that uses an adapted version of "DCT coefficient relationship based adaptive steganography"
     * (DCRAS - <em>see {@link DcrasEmbedder})</em>) to embed Bits into cover elements.</p>
     * <p>This is a (functional) base class. The main concern of inheriting classes is the selection of the embedding
     * block. In DCRAS, four adjacent 8x8 JPEG (or DCT) blocks serve to embed one Bit. While three blocks deliver the
     * mean of their specific values, only the value of one block is modified. So this selection is important.</p>
     * @param translatorSupplier supplies a {@link PixelTranslator} that translates the Cover Elements from their
     *                           default values (possibly int) to doubles of the interval [0,255] and back
     * @param dctTransform transforms matrices of doubles to and from DCT domain
     * @param qf desired (JPEG) quality factor for which the embedder should prepare the decoding.
     *           This embedder adjusts the amount by which it modifies the cover elements to try
     *           to ensure compression resistance up to qf.
     */
    public DcrasEmbedder(TranslatorSupplier<PixelTranslator> translatorSupplier, Transform<double[][]> dctTransform,
                         Float qf) {
        super(translatorSupplier, dctTransform, qf);
    }

    /**
     * <p>Creates an Embedder that uses an adapted version of "DCT coefficient relationship based adaptive steganography"
     * (DCRAS - <em>see {@link DcrasEmbedder})</em>) to embed Bits into cover elements.</p>
     * <p>This is a (functional) base class. The main concern of inheriting classes is the selection of the embedding
     * block. In DCRAS, four adjacent 8x8 JPEG (or DCT) blocks serve to embed one Bit. While three blocks deliver the
     * mean of their specific values, only the value of one block is modified. So this selection is important.</p>
     * @param translatorSupplier supplies a {@link PixelTranslator} that translates the Cover Elements from their
     *                           default values (possibly int) to doubles of the interval [0,255] and back
     * @param dctTransform transforms matrices of doubles to and from DCT domain
     * @param qf desired (JPEG) quality factor for which the embedder should prepare the decoding.
     *           This embedder adjusts the amount by which it modifies the cover elements to try
     *           to ensure compression resistance up to qf.
     * @param refX x coordinate 0 (inclusive) - 8 (exclusive) of dct coefficient to change
     * @param refY y coordinate 0 (inclusive) - 8 (exclusive) of dct coefficient to change
     */
    public DcrasEmbedder(TranslatorSupplier<PixelTranslator> translatorSupplier, Transform<double[][]> dctTransform,
                         Float qf, int refX, int refY) {
        super(translatorSupplier, dctTransform, qf, refX, refY);
    }

    /**
     * Uses some logic or rules to return the index of the block to use for embedding. Although a static integer in
     * this implementation, this method is the main focus point of most inheriting Embedders.
     * @param blocks the blocks to decide the best candidate for embedding from
     * @return the index of the best candidate fo embedding, according to this Embedders rules
     */
    protected int pickEmbeddingChunk(PixelTranslator[] blocks) {
        return 0;
    }

    ////////////////////////////////////////////////////////////////////////////////////////
    //                                    ENCODE
    ////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public int[] embed(int[] cvrElem, boolean one) throws EmbedderInputException {
        acceptLength(cvrElem, 256);
        EmbeddingData ed = next(cvrElem);
        setCoefficient(ed, calcEmbedValue(ed.reference, ed.values, one));
        return setChunks(ed.getChunks());
    }

    @Override
    public int[] flip(int[] cvrElem) throws EmbedderInputException {
        acceptLength(cvrElem, 256);
        EmbeddingData ed = next(cvrElem);
        boolean one = ed.reference > mean(ed.values);

        setCoefficient(ed, calcEmbedValue(ed.reference, ed.values, !one));
        return setChunks(ed.getChunks());
    }

    protected void setCoefficient(EmbeddingData ed, double value) {
        ed.getEmbChunk().set(refX, refY, value);
    }

    ////////////////////////////////////////////////////////////////////////////////////////
    //                                  DECODE
    ////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public boolean representsOne(int[] cvrElem) throws EmbedderInputException {
        acceptLength(cvrElem, 256);
        EmbeddingData ed = next(cvrElem);
        return ed.reference > mean(ed.values);
    }

    ////////////////////////////////////////////////////////////////////////////////////////
    //                                PREPARE BLOCKS
    ////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Evaluates the provided Cover Element by splitting and preparing it, then stores the information necessary
     * for embedding into a {@link EmbeddingData} for further use.
     * @param cvrElem Cover Element, input of this Embedder
     * @return {@link EmbeddingData} containing all necessary information for further processes
     * @throws EmbedderInputException if the provided Cover Element is unsuitable for this Embedder
     */
    protected EmbeddingData next(int[] cvrElem) throws EmbedderInputException {
        PixelTranslator[] dctChunks = getChunks(cvrElem);

        double[] values = new double[BLOCK_AMOUNT-1];
        int indEmbChunk = pickEmbeddingChunk(dctChunks);
        double reference = 0;

        for (int i = 0; i < dctChunks.length; i++) {
            double val = getRefValue(dctChunks[i]);
            if (i == indEmbChunk) reference = val;
            else values[i > indEmbChunk ? i-1 : i] = val;
        }
        return new EmbeddingData(reference, values, dctChunks, indEmbChunk);
    }

    protected double getRefValue(PixelTranslator block) {
        return block.get(refX, refY);
    }

    /**
     * <p>Splits and prepares the provided Cover Element to use by the algorithm of this Embedder.</p>
     * @param cvrElem Cover Element, input of this Embedder
     * @return separate value arrays, represented as {@link PixelTranslator}, called {@code blocks}. The values
     *      of the blocks will be transformed into dct domain.
     * @throws EmbedderInputException if the provided Cover Element is unsuitable for this Embedder
     */
    protected PixelTranslator[] getChunks(int[] cvrElem) throws EmbedderInputException {
        acceptLength(cvrElem, 256);

        int[][] cValues = splitToChunkValues(cvrElem);

        PixelTranslator[] blocks = new PixelTranslator[cValues.length];
        for (int i = 0; i < cValues.length; i++) {
            // convert argb to usable color space [0, 255]
            blocks[i] = this.translatorSupplier.get(cValues[i], 8);
            // transform each block in the dct frequency domain
            blocks[i].setValues(dctTransform.forward(blocks[i].getValues()));
        }
        return blocks;
    }

    /**
     * <p>Splits one big block (256 values - input of this Embedder) into 4 smaller blocks (64 values each).</p>
     * <p>It works in exactly the opposite way as {@link #combineToQuadChunk}.</p>
     */
    private int[][] splitToChunkValues(int[] quadChunk) {
        int[][] cValues = new int[4][64];
        for (int i = 0; i < quadChunk.length; i += 16) {
            int j = i < 128 ? 0 : 2;
            System.arraycopy(quadChunk, i, cValues[j], i / 2 % 64, 8);
            System.arraycopy(quadChunk, i + 8, cValues[j+1], i / 2 % 64, 8);
        }
        return cValues;
    }

    /**
     * Reverses the dct transform and combines the four blocks back to one big block,
     * returning it as a 1D-Array
     * @param blocks the four separate blocks containing the dct values of this Embedders input
     * @return the output of this Embedder
     */
    protected int[] setChunks(PixelTranslator[] blocks) {
        int[][] cValues = new int[4][64];

        for (int i = 0; i < cValues.length; i++) {
            // transform each block back to spatial domain
            blocks[i].setValues(dctTransform.reverse(blocks[i].getValues()));
            // convert rgb to y for each block
            cValues[i] = blocks[i].asARGB();
        }
        return combineToQuadChunk(cValues);
    }

    /**
     * <p>Combines the provided int[] to one big array in the following form:</p>
     * <p>input[4][]{one[64], two[64], three[64], four[64]} -></p>
     * <p>output[8] {
     *     <p>one[8], two[8],</p>
     *     <p>one[8], two[8],</p>
     *     <p>...</p>
     *     <p>three[8], four[8],</p>
     *     <p>three[8], four[8],</p>
     *     <p>...</p>
     * }</p>
     */
    private int[] combineToQuadChunk(int[][] cValues) {
        int[] quadChunk = new int[256];
        for (int i = 0; i < quadChunk.length; i += 16) {
            int j = i < 128 ? 0 : 2;
            System.arraycopy(cValues[j], i / 2 % 64, quadChunk, i, 8);
            System.arraycopy(cValues[j+1], i / 2 % 64, quadChunk, i + 8, 8);
        }
        return quadChunk;
    }

    ////////////////////////////////////////////////////////////////////////////////////////
    //                             CALCULATE CHANGE VALUE
    ////////////////////////////////////////////////////////////////////////////////////////

    /**
     * <p>Calculates the value used instead of {@code reference} to embed a Bit (as indicated by {@code isOne}).</p>
     * <p>The return value is calculated as a certain distance in relation to the {@link #mean} of {@code values}.
     * The distance can be positive (embedding a one) or negative (embedding a zero).</p>
     * <p>The distance itself is calculated in relation to the value in the quantization table corresponding to
     * the position of {@code reference} and {@code values} (quantization value - see
     * {@link Quantizer#quantizationValue(int, int, float)})</p>
     * <p>This method implements the main adaption from DCRAS <em>(see {@link DcrasEmbedder} p. 463 / 3)</em>.
     * Instead of repeatedly compressing the Cover Element, a relational value is found that should be stable
     * up to the intended quality factor provided to {@link #DcrasEmbedder}.</p>
     * @param reference the reference value to replace by the result of this method
     * @param values the relational values to calculate a distance (positive or negative) from
     * @param isOne true if a one is to be embedded, false otherwise
     * @return a value to replace {@code reference} with, resulting in embedding
     */
    private double calcEmbedValue(double reference, double[] values, boolean isOne) {
        double qValue = Quantizer.quantizationValue(refX, refY, qf);

        // calculate prequantized Values
        double[] preqValues = new double[values.length];
        for (int i = 0; i < preqValues.length; i++) {
            preqValues[i] = Math.round(values[i] / qValue) * qValue;
        }

        // return the minimum distance to the mean, relative to the quantization value
        double mean = mean(preqValues);
        double minD = Math.max(REL_DIST * qValue, 5);
        return isOne ? Math.max(reference, mean + minD) : Math.min(reference, mean - minD);
    }

    /**
     * Returns the mean of the provided values.
     */
    private double mean(double[] values) {
        double sum = 0;
        for (double value : values) sum += value;
        return sum / (double) values.length;
    }

    ////////////////////////////////////////////////////////////////////////////////////////
    //                             POJO TO PASS RETURN VALUES
    ////////////////////////////////////////////////////////////////////////////////////////

    /**
     * This is a POJO to store information helpful to the embedding process, mainly to share easily between methods.
     */
    protected static class EmbeddingData {
        /**
         * The reference value that will be modified to embed
         */
        private final double reference;
        /**
         * The values whose mean is referred to in order to embed
         */
        private final double[] values;
        /**
         * The input values (processed Cover Element) represented as {@link PixelTranslator PixelTranslators}
         */
        private final PixelTranslator[] blocks;
        /**
         * The index of {@link #blocks} referring to the block selected for modifying
         */
        private final int indEmbChunk;

        protected EmbeddingData(double reference, double[] values, PixelTranslator[] blocks, int indEmbChunk) {
            this.reference = reference;
            this.values = values;
            this.blocks = blocks;
            this.indEmbChunk = indEmbChunk;
        }

        public double getReference() {
            return reference;
        }

        public double[] getValues() {
            return values;
        }

        public PixelTranslator[] getChunks() {
            return blocks;
        }

        public PixelTranslator getEmbChunk() {
            return this.blocks[indEmbChunk];
        }
    }

}
