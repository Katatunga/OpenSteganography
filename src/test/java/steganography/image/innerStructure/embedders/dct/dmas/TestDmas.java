package steganography.image.innerStructure.embedders.dct.dmas;

import steganography.image.innerStructure.embedders.Embedder;
import steganography.image.innerStructure.embedders.dct.TestBlockEmbeddersUnit;
import steganography.image.operation.pixelTranslation.Rgb2YCbCr;
import steganography.transforms.FastDct8;

public class TestDmas extends TestBlockEmbeddersUnit {
    @Override
    protected Embedder<int[]> getEmbedder() {
        return new DmasEmbedder(Rgb2YCbCr::new, new FastDct8(), QF-0.3f);
    }

    @Override
    protected int getLength() {
        return 64;
    }
}
