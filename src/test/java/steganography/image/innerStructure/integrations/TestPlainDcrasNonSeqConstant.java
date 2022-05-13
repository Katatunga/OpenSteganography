package steganography.image.innerStructure.integrations;

import steganography.image.outerStructure.ImageStegIOJava;
import steganography.image.innerStructure.embedders.dct.dcras.DcrasEmbedder;
import steganography.image.innerStructure.encoders.Encoder;
import steganography.image.innerStructure.encoders.plain.PlainEncoder;
import steganography.image.innerStructure.overlays.BlockShuffleOverlay;
import steganography.image.operation.pixelTranslation.Rgb2YCbCr;
import steganography.transforms.FastDct8;

import java.awt.image.BufferedImage;

public class TestPlainDcrasNonSeqConstant extends TestInnerStructureBasics {

    @Override
    protected Encoder getEncoder(BufferedImage image) {
        long seed = 0;
        return new PlainEncoder<>(
                new DcrasEmbedder(Rgb2YCbCr::new, new FastDct8(), 1f, 4, 0),
                new BlockShuffleOverlay(image, seed, 16, ImageStegIOJava.allPixelsOpaque),
                false
        );
    }
}
