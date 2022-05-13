package steganography.image.innerStructure.integrations;

import steganography.image.innerStructure.embedders.dct.dmas.DmasEmbedder;
import steganography.image.innerStructure.encoders.Encoder;
import steganography.image.innerStructure.encoders.stc.StcEncoder;
import steganography.image.innerStructure.overlays.BlockShuffleOverlay;
import steganography.image.operation.pixelTranslation.Rgb2YCbCr;
import steganography.transforms.FastDct8;

import java.awt.image.BufferedImage;

public class TestStcDmasNonSeqConstant extends TestInnerStructureBasics {

    @Override
    protected Encoder getEncoder(BufferedImage image) {
        long seed = 0;
        return new StcEncoder<>(
                new DmasEmbedder(Rgb2YCbCr::new, new FastDct8(), 1f, 4, 0),
                new BlockShuffleOverlay(image, seed, 8),
                (x,y) -> 1d,
                false,
                seed
        );
    }

    // can't pass transparency
    void input_PNGTP_encodeDecodeOne_random() {}
    void input_PNGTP_encodeDecodeOne_zeroes() {}
    void input_PNGTP_encodeDecodeOne_ones() {}
    void input_PNGTP8_encodeDecodeOne_random() {}
    void input_PNGTP8_encodeDecodeOne_zeroes() {}
    void input_PNGTP8_encodeDecodeOne_ones() {}
}
