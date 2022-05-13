package steganography.image.innerStructure.encoders.plain;

import steganography.exceptions.encoder.EmbedderInputException;
import steganography.image.innerStructure.embedders.Embedder;
import steganography.image.innerStructure.encoders.GeneralEncoder;
import steganography.image.innerStructure.overlays.abstracts.BuffImgOverlay;

import java.util.BitSet;

/**
 * An Encoder that just encodes the payload, bit by bit, into the cover image.
 * @see #PlainEncoder
 * @param <T> Representation of pixel values dictated by return value of
 *           the used {@link BuffImgOverlay}
 */
public class PlainEncoder<T> extends GeneralEncoder<T> {

    /**
     * <p>Creates an Encoder that just encodes the payload, bit by bit, into the cover in the sequence the
     * provided {@code overlay} presents.</p>
     * <p>This Encoder embeds in every Cover Object, regardless of its state, warranting higher compression
     * resistance (see {@link #embed}).</p>
     * <p>This Encoder offers a sequential mode (see {@link #PlainEncoder(Embedder, BuffImgOverlay, boolean)
     * PlainEncoder}).</p>
     * <p>This encoder is compatible with: {@link PlainFlipEncoder}</p>
     * @param embedder {@link Embedder} to be used to embed the bits of payload into the Cover Elements
     * @param overlay {@link BuffImgOverlay} to get the Cover Elements from
     */
    public PlainEncoder(Embedder<T> embedder, BuffImgOverlay<T> overlay) {
        super(embedder, overlay);
    }

    /**
     * <p>Creates an Encoder that just encodes the payload, bit by bit, into the cover in the sequence the
     * provided {@code overlay} presents.</p>
     * <p>This Encoder embeds in every Cover Object, regardless of its state, warranting higher compression
     * resistance (see {@link #embed}).</p>
     * <p>This Encoder offers a sequential mode, allowing subsequent calls to {@link #encode} or {@link #decode}
     * without overwriting / rereading Cover Elements from the beginning. Sequential mode is used if the parameter
     * {@code sequential} is set to true.</p>
     * <p>This encoder is compatible with: {@link PlainFlipEncoder}</p>
     * @param embedder {@link Embedder} to be used to embed the bits of payload into the Cover Elements
     * @param overlay {@link BuffImgOverlay} to get the Cover Elements from
     * @param sequential set to true, if this Encoder should use the sequential mode, allowing (meaningful) subsequent
     *          calls to {@link #encode} or {@link #decode}.
     */
    public PlainEncoder(Embedder<T> embedder, BuffImgOverlay<T> overlay, boolean sequential) {
        super(embedder, overlay, sequential);
    }

    /**
     * <p>Encodes the payload "as-is", sequentially into the Cover Elements returned by the overlay.</p>
     * <p>Encoding happens by flipping only when necessary, Cover Elements already representing the
     * correct Bits are not changed in any way.</p>
     */
    @Override
    protected void encodeAlgorithm(byte[] payload) throws EmbedderInputException {
        int bitLen = payload.length * 8;
        BitSet payloadBits = BitSet.valueOf(payload);

        for (int i = 0; i < bitLen; i++) {
            T cvrElem = this.overlay.get(i + this.sequencePosition);
            this.overlay.set(
                    this.embed(payloadBits.get(i), cvrElem),
                    i + this.sequencePosition);
        }

        updateSequencePosition(this.sequencePosition + bitLen);
    }

    /**
     * <p>Embeds one Bit in the provided Cover Element ({@code cvrELem}). The embedded Bit is one, if {@code embedOne}
     * is set to true, zero otherwise.</p>
     * <p>This procedure affects the Cover Element regardless of its current Bit representation and is therefore less
     * detection resistant, but it allows the {@link Embedder embedders} to enforce a minimum distance in the
     * representation of Cover elements to prevent Bit flips enabling higher compression resistance.</p>
     * @param embedOne Bit to be embedded into the Cover Elements (true == 1; false == 0)
     * @param cvrObj Cover Element to embed the Bit into
     * @throws EmbedderInputException if {@code cvrObj} doesn't match the input the Embedder requires.
     * @return the embedded cover element of class T
     */
    protected T embed(boolean embedOne, T cvrObj) throws EmbedderInputException {
        return this.embedder.embed(cvrObj, embedOne);
    }

    /**
     * <p>Decodes the Cover Elements sequentially as provided by the overlay and returns
     * the resulting payload of length {@code bLength}.</p>
     */
    @Override
    protected byte[] decodeAlgorithm(int bLength) throws EmbedderInputException {
        BitSet payload = new BitSet();
        int bitLen = bLength * 8;
        for (int i = 0; i < bitLen; i++) {
            T cvrObj = this.overlay.get(i + this.sequencePosition);
            payload.set(i, this.embedder.representsOne(cvrObj));
        }
        updateSequencePosition(this.sequencePosition + bitLen);
        return payload.toByteArray();
    }

    @Override
    public int available() {
        return super.available() - this.sequencePosition;
    }
}
