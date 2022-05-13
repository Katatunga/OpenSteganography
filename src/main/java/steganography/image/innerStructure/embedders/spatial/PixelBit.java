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

package steganography.image.innerStructure.embedders.spatial;

import steganography.exceptions.encoder.EmbedderInputException;
import steganography.image.innerStructure.embedders.PixelEmbedder;

import java.util.Random;

/**
 * Implementation of RandomLSB, an algorithm to encode hidden messages into images
 * @author Henk-Joas Lubig
 */
public class PixelBit extends PixelEmbedder {

    @Override
    public Integer embed(Integer cvrElem, boolean one) throws EmbedderInputException {
        // if payload bit != pixelBit -> flip pixelBit
        return (one != representsOne(cvrElem)) ? flip(cvrElem) : cvrElem;
    }

    /**
     * <p>Returns true, if the parity of the individual bytes of {@code cvrElem} is an uneven number
     * ((A+R+G+B) mod 2 == 1).</p>
     * <p>Differently put: It determines whether the amount of 1's in the least significant bits
     * of each individual byte of pixelARGB is uneven.</p>
     * @param cvrElem pixel that represents a bit.
     * @return true if the given pixel represents a 1 bit.
     */
    @Override
    public boolean representsOne(Integer cvrElem) {
        return (
                (cvrElem & 1) ^
                        (cvrElem >> 8 & 1) ^
                        (cvrElem >> 16 & 1) ^
                        (cvrElem >> 24 & 1)
        ) > 0;
    }

    /**
     * <p>Changes the value of a random color channel (ARGB) of the given pixel
     * by +1 or -1 (randomly, but avoiding overflow).</p>
     * <p>Since a pixel represents a bit, this method "flips" it.
     * (By changing the outcome of (A+R+G+B) &amp; 1 == 0)</p>
     * @param cvrElem the pixelValue to change
     * @return the changed pixelValue
     */
    @Override
    public Integer flip(Integer cvrElem) throws EmbedderInputException {
        Random rng = new Random();

        // pick random channel
        int channelPick = rng.nextInt(3) * 8;
        // extract the byte of picked channel
        int channel = ((cvrElem >> channelPick) & 0xff);

        // check if addition or subtraction would cause overflow and prevent it
        // (not channel ^ 1, because change would not be random)
        int addition;
        // if all bits are 1, subtract 1
        if ((channel & 0xff) == 0xff) {
            addition = -1;
            // if all bits are 0, add 1
        } else if (channel == 0) {
            addition = 1;
        } else {
            // if there is no overflow add or subtract 1 at random
            addition = (rng.nextBoolean() ? 1 : -1);
        }
        channel += addition;

        // put modified byte back to its place in the int
        return (cvrElem | (0xff << channelPick)) & ~((~channel & 0xff) << channelPick);
        // overwrite previous picked byte in original int (pxInt) with 1111 1111
        // invert channel, position it in another int and invert again -> 11..channel..11
        // bitwise AND replaces old byte with channel and keeps the rest of pxInt
    }
}
