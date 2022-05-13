package steganography.image.innerStructure.integrations;

import steganography.image.innerStructure.embedders.spatial.LsbReplacer;
import steganography.image.innerStructure.encoders.Encoder;
import steganography.image.innerStructure.encoders.stc.StcEncoder;
import steganography.image.innerStructure.overlays.PixelShuffleOverlay;

import java.awt.image.BufferedImage;

public class TestStcLsbReplacerNonSeq extends TestInnerStructureBasics {

    @Override
    protected Encoder getEncoder(BufferedImage image) {
        long seed = 0;
        return new StcEncoder<>(
                new LsbReplacer(),
                new PixelShuffleOverlay(image, seed),
                (x,y) -> 1d,
                false,
                seed
        );
    }
}
