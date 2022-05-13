package steganography.image.innerStructure.embedders.spatial;

import steganography.image.innerStructure.embedders.Embedder;

public class TestLsbReplacer extends TestSpatialEmbedders {

    @Override
    protected Embedder<Integer> getEmbedder() {
        return new LsbReplacer();
    }
}
