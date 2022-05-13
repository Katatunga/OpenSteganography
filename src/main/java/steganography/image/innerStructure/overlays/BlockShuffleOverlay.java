package steganography.image.innerStructure.overlays;

import steganography.image.innerStructure.overlays.abstracts.ShuffleOverlay;

import java.awt.image.BufferedImage;
import java.util.function.Predicate;

public class BlockShuffleOverlay extends ShuffleOverlay<int[]> {

    /**
     * <p>Creates a {@link ShuffleOverlay} that returns Pixels of a square area of the underlying
     * {@link BufferedImage BufferedImage} ({@code BI}) in a random order, where the order is determined by the
     * provided {@code seed}. The size of the area is determined by {@code csl}, representing the length of one
     * side of the square.</p>
     * <p>{@code Position} in this overlay refers to the top-leftmost pixel of a square.</p>
     * <p>Using this constructor will lead to ignoring transparency in the image.</p>
     * <p>This Overlay tries to match any item of the underlying BufferedImage with a provided
     * {@link Predicate condition}. If the condition fails, the item is removed from the sequence.</p>
     *
     * @param bufferedImage   the {@link BufferedImage BufferedImage} to represent the pixels of.
     * @param seed            Long to be used to affect the randomization of pixelorder.
     * @param chunkSideLength Length of one side of the chunk (same as sqrt(chunk.size)).
     * @param condition condition to match all cover elements with
     */
    public BlockShuffleOverlay(BufferedImage bufferedImage, long seed, int chunkSideLength, Predicate<int[]> condition) {
        super(bufferedImage, seed, chunkSideLength, condition);
        createOrder();
    }

    /**
     * <p>Creates a {@link ShuffleOverlay} that returns Pixels of a square area of the underlying
     * {@link BufferedImage BufferedImage} ({@code BI}) in a random order, where the order is determined by the
     * provided {@code seed}. The size of the area is determined by {@code csl}, representing the length of one
     * side of the square.</p>
     * <p>{@code Position} in this overlay refers to the top-leftmost pixel of a square.</p>
     * <p>If {@code allowTransparency} is set to false, every square containing one or more fully transparent
     * pixels is removed.</p>
     *
     * @param bufferedImage   the {@link BufferedImage BufferedImage} to represent the pixels of.
     * @param seed            Long to be used to affect the randomization of pixelorder.
     * @param chunkSideLength Length of one side of the chunk (same as sqrt(chunk.size)).
     */
    public BlockShuffleOverlay(BufferedImage bufferedImage, long seed, int chunkSideLength) {
        this(bufferedImage, seed, chunkSideLength, null);
    }

    @Override
    protected int[] getRGB(int x, int y) {
        int[] cValues = new int[csl * csl];
        bufferedImage.getRGB(x, y, csl, csl, cValues, 0, csl);
        return cValues;
    }

    @Override
    protected void setRGB(int x, int y, int[] value) {
        bufferedImage.setRGB(x, y, csl, csl, value, 0, csl);
    }
}
