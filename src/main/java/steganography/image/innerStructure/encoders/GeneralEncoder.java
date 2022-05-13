package steganography.image.innerStructure.encoders;

import steganography.exceptions.DamagedMessageException;
import steganography.exceptions.encoder.EmbedderInputException;
import steganography.exceptions.encoder.UnsupportedOverlayException;
import steganography.image.innerStructure.embedders.Embedder;
import steganography.image.exceptions.ImageCapacityException;
import steganography.image.innerStructure.overlays.abstracts.BuffImgOverlay;

/**
 * <p>Abstract Class to inherit the general approach to EnDecoders from.</p>
 * <p>Inheriting classes should in most cases only need to overwrite the abstract
 * methods and possibly add some class members to embed and read payloads hidden in images.</p>
 * <p>GeneralEnDecoders offer a sequential mode, allowing subsequent calls to {@link #encode} or {@link #decode}
 * without overwriting / rereading Cover Elements from the beginning.</p>
 * @param <T> A class capable of representing one or multiple pixels
 */
public abstract class GeneralEncoder<T> implements Encoder {

    protected final Embedder<T> embedder;
    protected final BuffImgOverlay<T> overlay;

    /**
     * Stores the current position in the sequence, if sequential mode is used.
     */
    protected int sequencePosition = 0;

    /**
     * True if sequential mode is active
     */
    protected final boolean sequentialMode;

    /**
     * <p>Creates the super class for inheriting EnDecoders and provides the main coarse algorithms to inherited
     * methods.</p>
     * <p>GeneralEnDecoders offer a sequential mode (see {@link #GeneralEncoder(Embedder, BuffImgOverlay, boolean)}).</p>
     * @param embedder {@link Embedder} to be used to embed the bits of payload into the Cover Elements
     * @param overlay {@link BuffImgOverlay} to get the Cover Elements from
     */
    public GeneralEncoder(Embedder<T> embedder, BuffImgOverlay<T> overlay) {
        this.embedder = embedder;
        this.overlay = overlay;
        this.sequentialMode = false;
    }

    /**
     * <p>Creates the super class for inheriting EnDecoders and provides the main coarse algorithms to inherited
     * methods.</p>
     * <p>GeneralEnDecoders offer a sequential mode, allowing subsequent calls to {@link #encode} or {@link #decode}
     * without overwriting / rereading Cover Elements from the beginning. Sequential mode is used if the parameter
     * {@code sequential} is set to true.</p>
     * @param embedder {@link Embedder} to be used to embed the bits of payload into the Cover Elements
     * @param overlay {@link BuffImgOverlay} to get the Cover Elements from
     * @param sequential set to true, if sequential mode should be used.
     */
    public GeneralEncoder(Embedder<T> embedder, BuffImgOverlay<T> overlay, boolean sequential) {
        this.embedder = embedder;
        this.overlay = overlay;
        this.sequentialMode = sequential;
    }

    /**
     * Updates the sequence position if sequential mode is active.
     * @param newPos position to update to
     */
    protected void updateSequencePosition(int newPos) {
        if (this.sequentialMode)
            this.sequencePosition = newPos;
    }

    /**
     * <p>Encodes (embeds) the provided {@code payload} into the {@link java.awt.image.BufferedImage BufferedImage}
     * represented by the instances overlay.</p>
     * <p>This method uses a default seed to encode the payload and is therefore the same as:
     * {@link #encode(byte[], long) encode(payload, DEFAULT_SEED)}</p>
     * <p>Generally, GeneralEnDecoders can only {@link #decode(int) decode} their own messages. Other cases should be
     * explicitly stated in the JavaDoc of the respective decoding classes.</p>
     * @param payload payload to encode in the overlays image
     * @throws ImageCapacityException if the payload is bigger than the images capacity
     * @throws UnsupportedOverlayException if the overlays return value is unsuitable for the embedder
     */
    public void encode(byte[] payload) throws ImageCapacityException, UnsupportedOverlayException {
        encode(payload, DEFAULT_SEED);
    }

    /**
     * <p>Encodes (embeds) the provided payload into the {@link java.awt.image.BufferedImage BufferedImage} represented
     * by the instances overlay, using the provided {@code seed} to randomize embedding.</p>
     * <p>To {@link #decode(int, long) decode(bLength, seed)} a message encoded by this method, the same seed has to
     * be used.</p>
     * <p>Generally, GeneralEnDecoders can only {@link #decode(int, long) decode} their own messages. Other cases should be
     * explicitly stated in the JavaDoc of the respective decoding classes.</p>
     * @param payload payload to encode in the overlays image
     * @throws ImageCapacityException if the payload is bigger than the images capacity
     * @throws UnsupportedOverlayException if the overlays return value is unsuitable for the embedder
     */
    public void encode(byte[] payload, long seed) throws ImageCapacityException, UnsupportedOverlayException {
        int maxUnits = this.available();

        int bitLen = payload.length * 8;
        if (bitLen > maxUnits)
            throw new ImageCapacityException(
                    String.format("Payload (%d Bit) is longer than the%s capacity (%d Bit)",
                            bitLen, (this.sequentialMode ? " remaining" : ""), maxUnits));

        try {
            encodeAlgorithm(payload);
        } catch (EmbedderInputException e) {
            UnsupportedOverlayException ne = new UnsupportedOverlayException(
                    "Overlay return value does not match Embedder Input value");
            ne.addSuppressed(e);
            throw ne;
        }
    }

    /**
     * The detailed encoding Algorithm, implemented by the inheriting Encoder.
     * @param payload The payload to encode
     * @throws EmbedderInputException if the output of the Overlay doesn't match the Input the Embedder requires.
     */
    protected abstract void encodeAlgorithm(byte[] payload) throws EmbedderInputException;

    /**
     * <p>Decodes an encoded payload from the {@link java.awt.image.BufferedImage BufferedImage} represented
     * by the instances overlay, using the default seed. This method decodes payloads embedded by
     * {@link #encode(byte[])} or {@link #encode(byte[], long) encode(payload, DEFAULT_SEED)} respectively</p>
     * <p>There will be a result from this method in most cases, even if another seed is used or the image
     * contains no payload.</p>
     * <p>Generally, GeneralEnDecoders can only decode messages, {@link #encode(byte[], long)} encoded} by their class.
     * Other cases should be explicitly stated in the JavaDoc of the respective decoding classes.</p>
     * @param bLength length in bytes of the payload encoded in the overlays image
     * @throws DamagedMessageException if the payload could not be read due to errors or bit flips. This Exception
     *         may not necessarily be thrown by GeneralEnDecoders, as they may return the (possibly faulty) payload
     *         without error checking.
     * @throws UnsupportedOverlayException if the overlays return value is unsuitable for the embedder
     */
    public byte[] decode(int bLength) throws DamagedMessageException, UnsupportedOverlayException,
            ImageCapacityException {
        return decode(bLength, DEFAULT_SEED);
    }

    /**
     * <p>Decodes an encoded payload from the {@link java.awt.image.BufferedImage BufferedImage} represented
     * by the instances overlay, using the provided {@code seed}, which was used to randomize
     * {@link #encode(byte[], long)} encoding}.</p>
     * <p>There will be a result from this method in most cases, even if a wrong seed is used or the image
     * contains no payload. But for the correct and embedded payload to be decoded, the same seed must be used
     * as was for encoding, similar to a password.</p>
     * <p>Generally, GeneralEnDecoders can only decode messages, {@link #encode(byte[], long)} encoded} by their class.
     * Other cases should be explicitly stated in the JavaDoc of the respective decoding classes.</p>
     * @param bLength length in bytes of the payload encoded in the overlays image
     * @throws DamagedMessageException if the payload could not be read due to errors or bit flips. This Exception
     *         may not necessarily be thrown by GeneralEnDecoders, as they may return the (possibly faulty) payload
     *         without error checking.
     * @throws UnsupportedOverlayException if the overlays return value is unsuitable for the embedder
     */
    public byte[] decode(int bLength, long seed) throws DamagedMessageException, UnsupportedOverlayException,
            ImageCapacityException {
        int maxUnits = this.available();

        int bitLen = bLength * 8;
        if (bitLen > maxUnits)
            throw new ImageCapacityException(
                    String.format("Payload (%d Bit) is supposedly longer than the%s capacity (%d Bit)",
                            bitLen, (this.sequentialMode ? " remaining" : ""), maxUnits));

        try {
            return toMinLength(decodeAlgorithm(bLength), bLength);
        } catch (EmbedderInputException e) {
            UnsupportedOverlayException ne = new UnsupportedOverlayException(
                    "Overlay return value does not match Embedder Input value");
            ne.addSuppressed(e);
            throw ne;
        }
    }

    /**
     * The detailed decoding Algorithm, implemented by the inheriting Encoder.
     * @param bLength The supposed length of the payload in bytes
     * @return the decoded payload
     * @throws EmbedderInputException if the output of the Overlay doesn't match the Input the Embedder requires.
     */
    protected abstract byte[] decodeAlgorithm(int bLength) throws EmbedderInputException;

    private byte[] toMinLength(byte[] output, int minLength) {
        // if last bytes are zero bytes (not returned by BitSet)
        if (output.length < minLength) {
            byte[] intermediate = new byte[minLength];
            System.arraycopy(output, 0, intermediate, 0, output.length);
            output = intermediate;
        }
        return output;
    }

    @Override
    public int available() {
        return this.overlay.available();
    }
}
