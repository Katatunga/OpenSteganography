package steganography.image.innerStructure.overlays;

import steganography.image.innerStructure.overlays.abstracts.ShuffleOverlay;

import java.awt.image.BufferedImage;
import java.util.function.Predicate;

public class PixelShuffleOverlay extends ShuffleOverlay<Integer> {
    /**
     * <p>Creates a ShuffleOverlay that returns Pixels of the underlying BufferedImage in a random order
     * determined by the seed that is given to its constructor.</p>
     * <p>This Overlay tries to match any item of the underlying BufferedImage with a provided
     * {@link Predicate condition}. If the condition fails, the item is removed from the sequence.</p>
     *
     * @param bufferedImage   the {@link BufferedImage BufferedImage} to represent the pixels of.
     * @param seed            Long to be used to affect the randomization of pixelorder.
     * @param condition condition to match all cover elements with
     */
    public PixelShuffleOverlay(BufferedImage bufferedImage, long seed, Predicate<Integer> condition) {
        super(bufferedImage, seed, 1, condition);
        super.createOrder();
    }

    /**
     * Creates a ShuffleOverlay that returns Pixels of the underlying BufferedImage in a random order
     * determined by the seed that is given to its constructor.
     *
     * @param bufferedImage   the {@link BufferedImage BufferedImage} to represent the pixels of.
     * @param seed            Long to be used to affect the randomization of pixelorder.
     */
    public PixelShuffleOverlay(BufferedImage bufferedImage, long seed) {
        this(bufferedImage, seed, null);
        super.createOrder();
    }

    @Override
    protected Integer getRGB(int x, int y) {
        return this.bufferedImage.getRGB(x, y);
    }

    @Override
    protected void setRGB(int x, int y, Integer value) {
        this.bufferedImage.setRGB(x, y, value);
    }
}
