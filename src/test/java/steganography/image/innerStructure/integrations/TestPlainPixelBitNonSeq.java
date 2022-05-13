package steganography.image.innerStructure.integrations;

import steganography.image.innerStructure.embedders.spatial.PixelBit;
import steganography.image.innerStructure.encoders.Encoder;
import steganography.image.innerStructure.encoders.plain.PlainEncoder;
import steganography.image.innerStructure.overlays.PixelShuffleOverlay;

import java.awt.image.BufferedImage;

public class TestPlainPixelBitNonSeq extends TestInnerStructureBasics {

    @Override
    protected Encoder getEncoder(BufferedImage image) {
        long seed = 0;
        return new PlainEncoder<>(
                new PixelBit(),
                new PixelShuffleOverlay(image, seed),
                false
        );
    }
}
