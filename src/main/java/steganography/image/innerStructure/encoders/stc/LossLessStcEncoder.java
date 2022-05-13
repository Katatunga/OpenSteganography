package steganography.image.innerStructure.encoders.stc;

import steganography.exceptions.encoder.EmbedderInputException;
import steganography.image.innerStructure.distortion.DistortionFunction;
import steganography.image.innerStructure.embedders.Embedder;
import steganography.image.innerStructure.overlays.abstracts.BuffImgOverlay;

import java.util.BitSet;

public class LossLessStcEncoder<T> extends StcEncoder<T> {

    /**
     * <p>Creates an Encoder and Decoder that coordinates embedding using STCs (see <em>{@link StcEncoder reference})</em>
     * to minimize embedding distortion.</p>
     * <p>This StcEncoder embeds only the necessary changes by flipping the differences between {@code cover}
     * (Bits that are represented by the {@code Cover Elements}) and {@code stego} (result of viterbi), causing the
     * minimum possible distortion.</p>
     * <p>This approach, while fairly robust against detection, is highly susceptible to compression and is only
     * suitable for lossless formats.</p>
     * <p>This Encoder offers a sequential mode (see {@link #LossLessStcEncoder(Embedder, BuffImgOverlay,
     * DistortionFunction, boolean) LossLessStcEncoder}).</p>
     * <p>This Encoder is compatible with: {@link StcEncoder}</p>
     *
     * @param embedder   Embedder to flip or embed Bits in Cover Elements
     * @param overlay    Overlay returning Pixels as if part of a sequence
     * @param distortion DistortionFunction to calculate the distortion impact of embedding
     */
    public LossLessStcEncoder(Embedder<T> embedder, BuffImgOverlay<T> overlay, DistortionFunction<T> distortion) {
        super(embedder, overlay, distortion);
    }

    /**
     * <p>Creates an Encoder and Decoder that coordinates embedding using STCs (see <em>{@link StcEncoder reference})</em>
     * to minimize embedding distortion.</p>
     * <p>This StcEncoder embeds only the necessary changes by flipping the differences between {@code cover}
     * (Bits that are represented by the {@code Cover Elements}) and {@code stego} (result of viterbi), causing the
     * minimum possible distortion.</p>
     * <p>This approach, while fairly robust against detection, is highly susceptible to compression and is only
     * suitable for lossless formats.</p>
     * <p>This Encoder offers a sequential mode (see {@link #LossLessStcEncoder(Embedder, BuffImgOverlay,
     * DistortionFunction, boolean) LossLessStcEncoder}).</p>
     * <p>This Encoder is compatible with: {@link StcEncoder}</p>
     *
     * @param embedder   Embedder to flip or embed Bits in Cover Elements
     * @param overlay    Overlay returning Pixels as if part of a sequence
     * @param distortion DistortionFunction to calculate the distortion impact of embedding
     * @param seed long to be set as initial seed to randomize encoding
     */
    public LossLessStcEncoder(Embedder<T> embedder, BuffImgOverlay<T> overlay, DistortionFunction<T> distortion, long seed) {
        super(embedder, overlay, distortion, seed);
    }

    /**
     * <p>Creates an Encoder and Decoder that coordinates embedding using STCs (see <em>{@link StcEncoder reference})</em>
     * to minimize embedding distortion.</p>
     * <p>This StcEncoder embeds only the necessary changes by flipping the differences between {@code cover}
     * (Bits that are represented by the {@code Cover Elements}) and {@code stego} (result of viterbi), causing the
     * minimum possible distortion.</p>
     * <p>This approach, while fairly robust against detection, is highly susceptible to compression and is only
     * suitable for lossless formats.</p>
     * <p>This Encoder offers a sequential mode, allowing subsequent calls to {@link #encode} or {@link #decode}
     * without overwriting / rereading Cover Elements from the beginning. Sequential mode is used if the parameter
     * {@code sequential} is set to true.</p>
     * <p>This Encoder is compatible with: {@link StcEncoder}</p>
     *
     * @param embedder   Embedder to flip or embed Bits in Cover Elements
     * @param overlay    Overlay returning Pixels as if part of a sequence
     * @param distortion DistortionFunction to calculate the distortion impact of embedding
     * @param sequential true, if this Encoder should use sequential mode
     */
    public LossLessStcEncoder(Embedder<T> embedder, BuffImgOverlay<T> overlay,
                              DistortionFunction<T> distortion, boolean sequential) {
        super(embedder, overlay, distortion, sequential);
    }

    /**
     * <p>Creates an Encoder and Decoder that coordinates embedding using STCs (see <em>{@link StcEncoder reference})</em>
     * to minimize embedding distortion.</p>
     * <p>This StcEncoder embeds only the necessary changes by flipping the differences between {@code cover}
     * (Bits that are represented by the {@code Cover Elements}) and {@code stego} (result of viterbi), causing the
     * minimum possible distortion.</p>
     * <p>This approach, while fairly robust against detection, is highly susceptible to compression and is only
     * suitable for lossless formats.</p>
     * <p>This Encoder offers a sequential mode, allowing subsequent calls to {@link #encode} or {@link #decode}
     * without overwriting / rereading Cover Elements from the beginning. Sequential mode is used if the parameter
     * {@code sequential} is set to true.</p>
     * <p>This Encoder is compatible with: {@link StcEncoder}</p>
     *
     * @param embedder   Embedder to flip or embed Bits in Cover Elements
     * @param overlay    Overlay returning Pixels as if part of a sequence
     * @param distortion DistortionFunction to calculate the distortion impact of embedding
     * @param seed long to be set as initial seed to randomize encoding
     * @param sequential true, if this Encoder should use sequential mode
     */
    public LossLessStcEncoder(Embedder<T> embedder, BuffImgOverlay<T> overlay,
                              DistortionFunction<T> distortion, boolean sequential, long seed) {
        super(embedder, overlay, distortion, sequential, seed);
    }

    /**
     * <p>Embeds differences between {@code steg} and {@code cvrRep} only by flipping the respective Cover Elements.</p>
     * <p>This procedure affects the minimum amount of Cover Elements as determined by the
     * {@link #viterbi_stc viterbi}-Algorithm and therefore achieves relatively high detection resistance, depending
     * on the {@link DistortionFunction} and {@link Embedder}.
     * But because only necessary changes are made, no Bit-flip boundaries are checked by the Embedders, rendering
     * this embeddign approach highly robust against detection, but highly susceptible to compression attacks.</p>
     */
    @Override
    protected void embed(BitSet steg, BitSet cvrRep, int cvrLength) throws EmbedderInputException {
        // XOR leaves ones at indices with differences
        steg.xor(cvrRep);
        // flip at differences
        for (int i = steg.nextSetBit(0); i >= 0; i = steg.nextSetBit(i+1)) {
            T cvrElem = this.overlay.get(i + this.sequencePosition);
            this.overlay.set(this.embedder.flip(cvrElem), i + this.sequencePosition);
        }
    }
}
