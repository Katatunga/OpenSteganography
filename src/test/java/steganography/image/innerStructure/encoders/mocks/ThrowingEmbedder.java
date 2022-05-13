package steganography.image.innerStructure.encoders.mocks;

import steganography.exceptions.encoder.EmbedderInputException;
import steganography.image.innerStructure.embedders.Embedder;

public class ThrowingEmbedder implements Embedder<Boolean> {
    @Override
    public Boolean embed(Boolean cvrElem, boolean one) throws EmbedderInputException {
        throw new EmbedderInputException();
    }

    @Override
    public Boolean flip(Boolean cvrElem) throws EmbedderInputException {
        throw new EmbedderInputException();
    }

    @Override
    public boolean representsOne(Boolean cvrElem) throws EmbedderInputException {
        throw new EmbedderInputException();
    }
}
