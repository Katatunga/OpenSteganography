/*
 * Copyright (c) 2020
 * Contributed by Henk Lubig
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package steganography.image.innerStructure.overlays.abstracts;

import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

/**
 * This class returns Pixels of the underlying BufferedImage in order from top left (x=0, y=0)
 * to bottom right (x=bufferedImage.getWidth(), y=bufferedImage.getHeight()).
 */
public abstract class SequenceOverlay<T> implements BuffImgOverlay<T> {

    protected final BufferedImage bufferedImage;
    protected final WritableRaster biRaster;
    protected List<Integer> chunkOrder;
    /**
     * The length of one side of the chunk.
     * chunkSize = 8 == 64 pixels (8x8)
     */
    protected final int csl;
    protected int truncHeight;
    protected int truncWidth;

    /**
     * <p>Creates a SequenceOverlay that returns Pixels of a square area of the underlying
     * {@link BufferedImage BufferedImage} ({@code BI}). The size of the area is determined
     * by {@code csl}, representing the length of one side of the square.</p>
     * <p>{@code Position} in this overlay refers to the top-leftmost pixel of a square.</p>
     * <p>This Overlay returns the square areas in sequential order</p>
     * <p>from top left</p>
     * <p>{@code [(x=0, y=0) -> position: 0]}</p>
     * <p>to bottom right</p>
     * <p>
     *     {@code [(x=BI.getWidth(), y=BI.getHeight()) ->
     *     position: BI.getWidth() * BI.getHeight()]}.
 *     </p>
     * @param bufferedImage the BufferedImage to represent the pixels of.
     * @param csl Length of one side of the chunk (same as sqrt(chunk.size)).
     */
    public SequenceOverlay(BufferedImage bufferedImage, int csl) {
        if (csl <= 0)
            throw new IllegalArgumentException("Length of the blocks side can't be smaller than 1.");

        this.csl = csl;
        this.bufferedImage = bufferedImage;
        this.biRaster = bufferedImage.getRaster();

        // truncate height and width, so they are evenly divisible by chunkSize
        this.truncHeight = bufferedImage.getHeight() - ((bufferedImage.getHeight() - csl) % csl);
        this.truncWidth = bufferedImage.getWidth() - ((bufferedImage.getWidth() - csl) % csl);
    }

    /**
     * <p>Creates the overlay as an independent method to address pixels without using
     * BufferedImages coordinates.</p>
     * <p>Subclasses overwrite this method to use their own logic of creating the overlay.</p>
     */
    protected void createOrder() {
        List<Integer> newChunkOrder = new ArrayList<>();

        // calculate amount of chunks in this image
        int amountOfChunks = (truncHeight * truncWidth) / (csl * csl);

        for (int i = 0; i < amountOfChunks; i++) {
            newChunkOrder.add(i);
        }
        this.chunkOrder = newChunkOrder;
    }

    private int calcX(int position) {
        return (this.chunkOrder.get(position) * csl) % truncWidth;
    }

    private int calcY(int position) {
        return (this.chunkOrder.get(position) * csl / truncWidth * csl);
    }

    @Override
    public T get(int position) {
        // calculate next left-top pixel (position means position in the order)
        return getRGB(calcX(position), calcY(position));
    }

    protected abstract T getRGB(int x, int y);

    @Override
    public void set(T value, int position) throws NoSuchElementException {
        if (position < 0 || position >= this.chunkOrder.size()) {
            throw new NoSuchElementException("No chunk at intended position");
        }
        setRGB(calcX(position), calcY(position), value);
    }

    protected abstract void setRGB(int x, int y, T value);

    @Override
    public int available() {
        return this.chunkOrder.size();
    }
}

