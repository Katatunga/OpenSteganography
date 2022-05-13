package steganography.image.innerStructure.embedders;

import steganography.exceptions.encoder.EmbedderInputException;

public abstract class BlockEmbedder implements Embedder<int[]> {
    
    protected void acceptLength(int[] cvrElem, int acceptedLength) throws EmbedderInputException {
        if (cvrElem.length != acceptedLength)
            throw new EmbedderInputException(
                    String.format("Input needs to be %d pixels but was %d", acceptedLength, cvrElem.length));
    }
}
