package steganography.image.innerStructure.embedders;

import steganography.exceptions.encoder.EmbedderInputException;

/**
 * Super to block embedders, like {@link steganography.image.innerStructure.embedders.dct.dmas.DmasEmbedder DMAS}
 * and {@link steganography.image.innerStructure.embedders.dct.dcras.DcrasEmbedder DCRAS}
 */
public abstract class BlockEmbedder implements Embedder<int[]> {
    
    protected void acceptLength(int[] cvrElem, int acceptedLength) throws EmbedderInputException {
        if (cvrElem.length != acceptedLength)
            throw new EmbedderInputException(
                    String.format("Input needs to be %d pixels but was %d", acceptedLength, cvrElem.length));
    }
}
