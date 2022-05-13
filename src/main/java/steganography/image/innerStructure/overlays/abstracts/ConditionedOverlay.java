package steganography.image.innerStructure.overlays.abstracts;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

public abstract class ConditionedOverlay<T> extends SequenceOverlay<T> {

    private final Predicate<T> condition;

    /**
     * <p>Creates a SequenceOverlay that returns Pixels of a square area of the underlying
     * {@link BufferedImage BufferedImage} ({@code BI}), if that square Area fallows a condition.
     * The size of the area is determined
     * by {@code csl}, representing the length of one side of the square.</p>
     * <p>{@code Position} in this overlay refers to the top-leftmost pixel of a square.</p>
     * <p>This Overlay returns the square areas in sequential order</p>
     * <p>from top left</p>
     * <p>{@code [(x=0, y=0) -> position: 0]}</p>
     * <p>to bottom right</p>
     * <p>
     * {@code [(x=BI.getWidth(), y=BI.getHeight()) ->
     * position: BI.getWidth() * BI.getHeight()]}.
     * </p>
     * <p>This Overlay tries to match any item of the underlying BufferedImage with a provided
     * {@link Predicate condition}. If the condition fails, the item is removed from the sequence.</p>
     * <p>Providing {@code condition = null} essentially creates a {@link SequenceOverlay}.</p>
     *
     * @param bufferedImage the BufferedImage to represent the pixels of.
     * @param csl           Length of one side of the chunk (same as sqrt(chunk.size)).
     * @param condition condition to match all cover elements with
     */
    public ConditionedOverlay(BufferedImage bufferedImage, int csl, Predicate<T> condition) {
        super(bufferedImage, csl);
        this.condition = condition;
    }

    @Override
    protected void createOrder() {
        super.createOrder();
        if (this.condition != null)
            removeFailingConditions();
    }

    /**
     * Iterates through Blocks after creating the base order, copying all Blocks that
     * do not include a pixel with Alpha-channel == 0. Other Blocks will be ignored,
     * effectively removing them from this overlay.
     */
    private void removeFailingConditions() {
        List<Integer> newChunkOrder = new ArrayList<>();
        for (int i = 0; i < this.available(); i++) {
            if (this.condition.test(this.get(i)))
                newChunkOrder.add(i);
        }
        this.chunkOrder = newChunkOrder;
    }
}
