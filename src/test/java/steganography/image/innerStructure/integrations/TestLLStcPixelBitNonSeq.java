package steganography.image.innerStructure.integrations;

import steganography.image.innerStructure.embedders.spatial.PixelBit;
import steganography.image.innerStructure.encoders.Encoder;
import steganography.image.innerStructure.encoders.stc.LossLessStcEncoder;
import steganography.image.innerStructure.overlays.PixelShuffleOverlay;

import java.awt.image.BufferedImage;

public class TestLLStcPixelBitNonSeq extends TestInnerStructureBasics {

    @Override
    protected Encoder getEncoder(BufferedImage image) {
        long seed = 0;
        return new LossLessStcEncoder<>(
                new PixelBit(),
                new PixelShuffleOverlay(image, seed),
                (x,y) -> 1d,
                false,
                seed
        );
    }
}
