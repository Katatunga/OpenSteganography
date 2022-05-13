package steganography.image.innerStructure.integrations;

import steganography.image.innerStructure.embedders.dct.dcras.DcrasEmbedder;
import steganography.image.innerStructure.encoders.Encoder;
import steganography.image.innerStructure.encoders.stc.LossLessStcEncoder;
import steganography.image.innerStructure.overlays.BlockShuffleOverlay;
import steganography.image.operation.pixelTranslation.Rgb2YCbCr;
import steganography.transforms.FastDct8;

import java.awt.image.BufferedImage;

public class TestLLStcDcrasNonSeqConstant extends TestInnerStructureBasics {

    @Override
    protected Encoder getEncoder(BufferedImage image) {
        long seed = 0;
        return new LossLessStcEncoder<>(
                new DcrasEmbedder(Rgb2YCbCr::new, new FastDct8(), 1f, 3, 2),
                new BlockShuffleOverlay(image, seed, 16),
                (x,y) -> 1d,
                false,
                seed
        );
    }
}
