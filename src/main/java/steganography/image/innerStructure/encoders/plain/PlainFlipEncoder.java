package steganography.image.innerStructure.encoders.plain;

import steganography.exceptions.encoder.EmbedderInputException;
import steganography.image.innerStructure.embedders.Embedder;
import steganography.image.innerStructure.overlays.abstracts.BuffImgOverlay;

public class PlainFlipEncoder<T> extends PlainEncoder<T> {

    /**
     * <p>Creates an Encoder that encodes the payload, bit by bit, into the cover in the sequence the
     * provided {@code overlay} presents, but changing (flipping) only necessary Cover Elements, warranting higher
     * detection resistance (see {@link #embed}).</p>
     * <p>This Encoder offers a sequential mode (see {@link #PlainFlipEncoder(Embedder, BuffImgOverlay, boolean)
     * PlainFlipEncoder}.</p>
     * <p>This encoder is compatible with: {@link PlainEncoder}</p>
     * @param embedder {@link Embedder} to be used to embed the bits of payload into the Cover Elements
     * @param overlay {@link BuffImgOverlay} to get the Cover Elements from
     */
    public PlainFlipEncoder(Embedder<T> embedder, BuffImgOverlay<T> overlay) {
        super(embedder, overlay);
    }

    /**
     * <p>Creates an Encoder that encodes the payload, bit by bit, into the cover in the sequence the
     * provided {@code overlay} presents, but changing (flipping) only necessary Cover Elements, warranting higher
     * detection resistance (see {@link #embed}).</p>
     * <p>This Encoder offers a sequential mode, allowing subsequent calls to {@link #encode} or {@link #decode}
     * without overwriting / rereading Cover Elements from the beginning. Sequential mode is used if the parameter
     * {@code sequential} is set to true or omitted.</p>
     * <p>This encoder is compatible with: {@link PlainEncoder}</p>
     * @param embedder {@link Embedder} to be used to embed the bits of payload into the Cover Elements
     * @param overlay {@link BuffImgOverlay} to get the Cover Elements from
     * @param sequential set to true, if this Encoder should use the sequential mode, allowing (meaningful) subsequent
     *          calls to {@link #encode} or {@link #decode}.
     */
    public PlainFlipEncoder(Embedder<T> embedder, BuffImgOverlay<T> overlay, boolean sequential) {
        super(embedder, overlay, sequential);
    }

    /**
     * <p>Flips the Embedders Bit representation in the Cover Element ({@code cvrELem}) to match {@code embedOne}.
     * If the Cover Element already represents {@code embedOne}, this is a non-operation, returning the provided
     * {@code cvrElem}.</p>
     * <p>This procedure affects only the necessary Cover Element and is therefore prioritizing detection resistance
     * above compression resistance, as unchanged Cover Elements could remain in border regions between Bit
     * representations. This approach is only suitable for lossless compression of the cover.</p>
     * @param embedOne Bit to be embedded into the Cover Elements (true == 1; false == 0)
     * @param cvrObj Cover Element to embed the Bit into
     * @throws EmbedderInputException if {@code cvrObj} doesn't match the input the Embedder requires.
     */
    @Override
    protected T embed(boolean embedOne, T cvrObj) throws EmbedderInputException {
        if (this.embedder.representsOne(cvrObj) != embedOne)
            return this.embedder.flip(cvrObj);
        return cvrObj;
    }
}
