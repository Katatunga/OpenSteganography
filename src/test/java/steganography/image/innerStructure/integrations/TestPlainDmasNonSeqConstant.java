package steganography.image.innerStructure.integrations;

import steganography.image.innerStructure.embedders.dct.dmas.DmasEmbedder;
import steganography.image.innerStructure.encoders.Encoder;
import steganography.image.innerStructure.encoders.plain.PlainEncoder;
import steganography.image.innerStructure.overlays.BlockShuffleOverlay;
import steganography.image.operation.pixelTranslation.Rgb2YCbCr;
import steganography.transforms.FastDct8;

import java.awt.image.BufferedImage;

public class TestPlainDmasNonSeqConstant extends TestInnerStructureBasics {

    @Override
    protected Encoder getEncoder(BufferedImage image) {
        long seed = 0;
        return new PlainEncoder<>(
                new DmasEmbedder(Rgb2YCbCr::new, new FastDct8(), 1f, 4, 0),
                new BlockShuffleOverlay(image, seed, 8),
                false
        );
    }
}
