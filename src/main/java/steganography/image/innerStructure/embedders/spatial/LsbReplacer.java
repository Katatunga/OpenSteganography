package steganography.image.innerStructure.embedders.spatial;

import steganography.exceptions.encoder.EmbedderInputException;
import steganography.image.innerStructure.embedders.PixelEmbedder;

/**
 * This is a showcase for easy embedding. Not resistant against any attack
 */
public class LsbReplacer extends PixelEmbedder {

    @Override
    public Integer embed(Integer cvrElem, boolean one) throws EmbedderInputException {
        return one ? cvrElem | 1 : cvrElem & ~0 << 1;
    }

    @Override
    public Integer flip(Integer cvrElem) throws EmbedderInputException {
        return representsOne(cvrElem) ? embed(cvrElem, false) : embed(cvrElem, true);
    }

    @Override
    public boolean representsOne(Integer cvrElem) throws EmbedderInputException {
        return (cvrElem & 1) > 0;
    }
}
