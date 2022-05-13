package steganography.image.innerStructure.overlays.abstracts;

import java.util.NoSuchElementException;

/**
 * <p>Implementing classes (Overlays) provide means to get and set pixels in an underlying
 * {@link java.awt.image.BufferedImage BufferedImage}.</p>
 * <p>Overlays abstract the coordinates and replace them with a sequential position.
 * The order of the sequence is determined by the implementation.</p>
 * @param <T> Object representing a number of pixels
 */
public interface BuffImgOverlay<T> {

    /**
     * Returns the {@code T} representing one or multiple pixels in the underlying BufferedImage
     * as determined by the Implementation.
     * @param position refers to the position / index in the order of this overlay
     * @return {@code T} representing the pixels at position {@code position} of the Overlay.
     * @throws NoSuchElementException if {@code position} refers to a position outside the scope of the overlay.
     */
    T get(int position) throws NoSuchElementException;

    /**
     * Sets all value of an intended {@code T} to the values of the provided {@code T}.
     * The intended {@code T} is determined by {@code position} referring to the
     * position in the order of this overlay.
     * @param position the overlays position of the intended {@code T}.
     * @param value stego or cover element to set on the current position
     * @throws NoSuchElementException if {@code position} refers to a position outside the scope of the overlay.
     */
    void set(T value, int position) throws NoSuchElementException;

    /**
     * Returns the number of remaining values (not yet returned by next()). Generally, this number represents
     * the capacity of implementing classes in Bits. Other cases should be stated in the implementers JavaDoc.
     * @return number of remaining values
     */
    int available();
}
