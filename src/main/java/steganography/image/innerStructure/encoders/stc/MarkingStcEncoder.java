package steganography.image.innerStructure.encoders.stc;

import steganography.exceptions.encoder.EmbedderInputException;
import steganography.image.innerStructure.distortion.DistortionFunction;
import steganography.image.innerStructure.embedders.Embedder;
import steganography.image.innerStructure.embedders.dct.dmas.MarkingEmbedder;
import steganography.image.innerStructure.overlays.abstracts.BuffImgOverlay;

import java.util.BitSet;

public class MarkingStcEncoder extends StcEncoder<int[]>{
    MarkingEmbedder embedder;

    public MarkingStcEncoder(MarkingEmbedder embedder, BuffImgOverlay<int[]> overlay, DistortionFunction<int[]> distortion) {
        super(embedder, overlay, distortion);
        this.embedder = embedder;
    }

    public MarkingStcEncoder(MarkingEmbedder embedder, BuffImgOverlay<int[]> overlay, DistortionFunction<int[]> distortion, long seed) {
        super(embedder, overlay, distortion, seed);
        this.embedder = embedder;
    }

    public MarkingStcEncoder(MarkingEmbedder embedder, BuffImgOverlay<int[]> overlay, DistortionFunction<int[]> distortion, boolean sequential) {
        super(embedder, overlay, distortion, sequential);
        this.embedder = embedder;
    }

    public MarkingStcEncoder(MarkingEmbedder embedder, BuffImgOverlay<int[]> overlay, DistortionFunction<int[]> distortion, boolean sequential, long seed) {
        super(embedder, overlay, distortion, sequential, seed);
        this.embedder = embedder;
    }

    /**
     * <p>Embeds differences between {@code steg} and {@code cvrRep} only by flipping the respective Cover Elements.</p>
     * <p>This procedure affects the minimum amount of Cover Elements as determined by the
     * {@link #viterbi_stc viterbi}-Algorithm and therefore achieves relatively high detection resistance, depending
     * on the {@link DistortionFunction} and {@link Embedder}.
     * But because only necessary changes are made, no Bit-flip boundaries are checked by the Embedders, rendering
     * this embeddign approach highly robust against detection, but highly susceptible to compression attacks.</p>
     */
    // @Override
    // protected void embed(BitSet steg, BitSet cvrRep, int cvrLength) throws EmbedderInputException {
    //     // XOR leaves ones at indices with differences
    //     steg.xor(cvrRep);
    //     // flip at differences
    //     for (int i = steg.nextSetBit(0); i >= 0; i = steg.nextSetBit(i+1)) {
    //         int[] cvrElem = this.overlay.get(i + this.sequencePosition);
    //         this.overlay.set(this.embedder.flipMark(cvrElem), i + this.sequencePosition);
    //     }
    // }

    protected void embed(BitSet steg, BitSet cvrRep, int cvrLength) throws EmbedderInputException {
        for (int i = 0; i < cvrLength; i++) {
            int[] cvrElem = this.overlay.get(i + this.sequencePosition);
            this.overlay.set(this.embedder.embedMark(cvrElem, steg.get(i)), i + this.sequencePosition);
        }
    }
}
