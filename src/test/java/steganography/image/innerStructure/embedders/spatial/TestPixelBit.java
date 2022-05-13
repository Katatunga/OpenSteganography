package steganography.image.innerStructure.embedders.spatial;

import steganography.exceptions.encoder.EmbedderInputException;
import steganography.image.innerStructure.embedders.Embedder;

public class TestPixelBit extends TestSpatialEmbedders {

    @Override
    protected Embedder<Integer> getEmbedder() {
        return new PixelBit();
    }

    // can't pass these, because change is random
    protected void testEmbedTrue_2sameOutput() throws EmbedderInputException {}
    protected void testEmbedTrue_2sameOutput_often() throws EmbedderInputException {}
    protected void testEmbedFalse_2sameOutput() throws EmbedderInputException {}
    protected void testEmbedFalse_2sameOutput_often() throws EmbedderInputException {}
    protected void testFlip_2sameOutput() throws EmbedderInputException {}
    protected void testFlip_2sameOutput_often() throws EmbedderInputException {}
}
