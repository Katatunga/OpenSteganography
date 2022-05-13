package steganography.image.innerStructure.encoders.mocks;

import steganography.exceptions.encoder.EmbedderInputException;
import steganography.image.innerStructure.embedders.Embedder;

public class MockEmbedder implements Embedder<Boolean> {
    @Override
    public Boolean embed(Boolean cvrElem, boolean one) throws EmbedderInputException {
        return one;
    }

    @Override
    public Boolean flip(Boolean cvrElem) throws EmbedderInputException {
        return !cvrElem;
    }

    @Override
    public boolean representsOne(Boolean cvrElem) throws EmbedderInputException {
        return cvrElem;
    }
}
