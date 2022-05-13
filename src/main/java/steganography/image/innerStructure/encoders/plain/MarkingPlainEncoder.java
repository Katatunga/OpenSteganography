package steganography.image.innerStructure.encoders.plain;

import steganography.exceptions.encoder.EmbedderInputException;
import steganography.image.innerStructure.embedders.dct.dcras.MarkingDcrasEmbedder;
import steganography.image.innerStructure.embedders.dct.dmas.MarkingEmbedder;
import steganography.image.innerStructure.overlays.abstracts.BuffImgOverlay;

public class MarkingPlainEncoder extends PlainEncoder<int[]> {
    MarkingDcrasEmbedder embedder;

    public MarkingPlainEncoder(MarkingDcrasEmbedder embedder, BuffImgOverlay<int[]> overlay) {
        super(embedder, overlay);
        this.embedder = embedder;
    }

    public MarkingPlainEncoder(MarkingDcrasEmbedder embedder, BuffImgOverlay<int[]> overlay, boolean sequential) {
        super(embedder, overlay, sequential);
        this.embedder = embedder;
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
    protected int[] embed(boolean embedOne, int[] cvrObj) throws EmbedderInputException {
        if (this.embedder.representsOne(cvrObj) != embedOne)
            return this.embedder.flipMark(cvrObj);
        return cvrObj;
    }
    // protected int[] embed(boolean embedOne, int[] cvrObj) throws EmbedderInputException {
    //     return this.embedder.embedMark(cvrObj, embedOne);
    // }
}
