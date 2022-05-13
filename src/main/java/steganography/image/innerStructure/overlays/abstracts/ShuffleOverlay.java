/*
 * Copyright (c) 2020
 * Contributed by Henk-Joas Lubig
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
import java.util.Collections;
import java.util.Random;
import java.util.function.Predicate;

/**
 * This class returns Pixels of the underlying BufferedImage in a random order determined by the seed
 * that is given to its constructor
 */
public abstract class ShuffleOverlay<T> extends ConditionedOverlay<T> {

    protected Random random;

    /**
     * <p>Creates a ShuffleOverlay that organizes Pixels of a square area of the underlying
     * {@link BufferedImage BufferedImage} ({@code BI}) in a random order, where the order is determined by the
     * provided {@code seed}. The size of the area is determined by {@code csl}, representing the length of one
     * side of the square.</p>
     * <p>{@code Position} in this overlay refers to the top-leftmost pixel of a square.</p>
     *
     * @param bufferedImage the {@link BufferedImage BufferedImage} to represent the pixels of.
     * @param seed Long to be used to affect the randomization of pixelorder.
     * @param chunkSideLength Length of one side of the chunk (same as sqrt(chunk.size)).
     * @param condition condition to match all cover elements with
     */
    public ShuffleOverlay(BufferedImage bufferedImage, long seed, int chunkSideLength, Predicate<T> condition) {
        super(bufferedImage, chunkSideLength, condition);
        this.random = new Random(seed);
    }

    /**
     * <p>Creates the overlay as an independent method to address pixels without using
     * BufferedImages coordinates. Uses two protected methods to separate the creation
     * of the overlay from its randomization.</p>
     * <p>Subclasses should only overwrite this method to alter this separation.</p>
     */
    @Override
    protected void createOrder() {
        super.createOrder();
        Collections.shuffle(this.chunkOrder, this.random);
    }
}
