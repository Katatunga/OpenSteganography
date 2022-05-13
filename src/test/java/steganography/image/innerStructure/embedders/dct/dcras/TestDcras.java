package steganography.image.innerStructure.embedders.dct.dcras;

import steganography.image.innerStructure.embedders.Embedder;
import steganography.image.innerStructure.embedders.dct.TestBlockEmbeddersUnit;
import steganography.image.operation.pixelTranslation.Rgb2YCbCr;
import steganography.transforms.FastDct8;

public class TestDcras extends TestBlockEmbeddersUnit {
    @Override
    protected Embedder<int[]> getEmbedder() {
        return new DcrasEmbedder(Rgb2YCbCr::new, new FastDct8(), QF-0.2f);
    }

    @Override
    protected int getLength() {
        return 256;
    }
}
