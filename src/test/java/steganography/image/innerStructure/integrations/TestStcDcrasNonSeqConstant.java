package steganography.image.innerStructure.integrations;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import steganography.exceptions.DamagedMessageException;
import steganography.exceptions.encoder.EncoderException;
import steganography.image.innerStructure.embedders.dct.dcras.DcrasEmbedder;
import steganography.image.innerStructure.encoders.Encoder;
import steganography.image.innerStructure.encoders.stc.StcEncoder;
import steganography.image.exceptions.ImageCapacityException;
import steganography.image.innerStructure.overlays.BlockShuffleOverlay;
import steganography.image.operation.pixelTranslation.Rgb2YCbCr;
import steganography.transforms.FastDct8;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.BitSet;

public class TestStcDcrasNonSeqConstant extends TestInnerStructureBasics {

    @Override
    protected Encoder getEncoder(BufferedImage image) {
        long seed = 0;
        return new StcEncoder<>(
                new DcrasEmbedder(Rgb2YCbCr::new, new FastDct8(), 1f, 4, 0),
                new BlockShuffleOverlay(image, seed, 16),
                (x,y) -> 1d,
                false,
                seed
        );
    }

    @Test
    void input_PNG8_encodeDecodeOne_random() throws IOException, EncoderException, ImageCapacityException, DamagedMessageException {
        input_format_encodeDecodeOne_random(eightDivisible + ".png", 3);
    }
}
