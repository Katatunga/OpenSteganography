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

package steganography.image.innerStructure.embedders;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import steganography.exceptions.encoder.EmbedderInputException;
import steganography.image.innerStructure.embedders.Embedder;

public abstract class TestEmbeddersUnit<T> {

    protected abstract Embedder<T> getEmbedder();

    protected abstract T getRandomInput();

    protected abstract T getUpperEdge();

    protected abstract T getLowerEdge();

    protected abstract void assertEquals(T expected, T actual);
    protected abstract void assertEquals(T expected, T actual, String message);

    //////////////////////////////////////////////////////////////////////////////
    //                               EMBED
    //////////////////////////////////////////////////////////////////////////////

    @Test
    protected void testEmbedTrueRandom_often() throws EmbedderInputException {
        Embedder<T> embedder = getEmbedder();

        for (int i = 0; i < 10; i++) {
            T input = getRandomInput();
            T output = embedder.embed(input, true);
            Assertions.assertTrue(embedder.representsOne(output), "in iteration " + i);
        }
    }

    @Test
    protected void testEmbedFalseRandom_often() throws EmbedderInputException {
        Embedder<T> embedder = getEmbedder();

        for (int i = 0; i < 10; i++) {
            T input = getRandomInput();
            T output = embedder.embed(input, false);
            Assertions.assertFalse(embedder.representsOne(output), "in iteration " + i);
        }
    }

    @Test
    protected void testEmbedTrueUpperEdge_often() throws EmbedderInputException {
        Embedder<T> embedder = getEmbedder();

        for (int i = 0; i < 10; i++) {
            T input = getUpperEdge();
            T output = embedder.embed(input, true);
            Assertions.assertTrue(embedder.representsOne(output), "in iteration " + i);
        }
    }

    @Test
    protected void testEmbedFalseUpperEdge_often() throws EmbedderInputException {
        Embedder<T> embedder = getEmbedder();

        for (int i = 0; i < 10; i++) {
            T input = getUpperEdge();
            T output = embedder.embed(input, false);
            Assertions.assertFalse(embedder.representsOne(output), "in iteration " + i);
        }
    }

    @Test
    protected void testEmbedTrueLowerEdge_often() throws EmbedderInputException {
        Embedder<T> embedder = getEmbedder();

        for (int i = 0; i < 10; i++) {
            T input = getLowerEdge();
            T output = embedder.embed(input, true);
            Assertions.assertTrue(embedder.representsOne(output), "in iteration " + i);
        }
    }

    @Test
    protected void testEmbedFalseLowerEdge_often() throws EmbedderInputException {
        Embedder<T> embedder = getEmbedder();

        for (int i = 0; i < 10; i++) {
            T input = getLowerEdge();
            T output = embedder.embed(input, false);
            Assertions.assertFalse(embedder.representsOne(output), "in iteration " + i);
        }
    }

    @Test
    protected void testEmbedTrue_2sameOutput() throws EmbedderInputException {
        Embedder<T> embedder = getEmbedder();

        T input = getRandomInput();
        T output1 = embedder.embed(input, true);
        T output2 = embedder.embed(input, true);

        assertEquals(output1, output2);
    }

    @Test
    protected void testEmbedTrue_2sameOutput_often() throws EmbedderInputException {
        Embedder<T> embedder = getEmbedder();

        for (int i = 0; i < 10; i++) {
            T input = getRandomInput();
            T output1 = embedder.embed(input, true);
            T output2 = embedder.embed(input, true);

            assertEquals(output1, output2, "in iteration " + i);
        }
    }

    @Test
    protected void testEmbedFalse_2sameOutput() throws EmbedderInputException {
        Embedder<T> embedder = getEmbedder();

        T input = getRandomInput();
        T output1 = embedder.embed(input, false);
        T output2 = embedder.embed(input, false);

        assertEquals(output1, output2);
    }

    @Test
    protected void testEmbedFalse_2sameOutput_often() throws EmbedderInputException {
        Embedder<T> embedder = getEmbedder();

        for (int i = 0; i < 10; i++) {
            T input = getRandomInput();
            T output1 = embedder.embed(input, false);
            T output2 = embedder.embed(input, false);

            assertEquals(output1, output2, "in iteration " + i);
        }
    }

    //////////////////////////////////////////////////////////////////////////////
    //                               FLIP
    //////////////////////////////////////////////////////////////////////////////

    @Test
    protected void testFlipSingleBit_often() throws EmbedderInputException {
        Embedder<T> embedder = getEmbedder();

        for (int i = 0; i < 10; i++) {
            T input = getRandomInput();

            boolean inputIsOne = embedder.representsOne(input);
            boolean outputIsOne = embedder.representsOne(embedder.flip(input));

            Assertions.assertNotEquals(inputIsOne, outputIsOne, "in iteration " + i);
        }
    }

    @Test
    protected void testFlipBitUpperEdge() throws EmbedderInputException {
        Embedder<T> embedder = getEmbedder();

        T input = getUpperEdge();

        boolean inputIsOne = embedder.representsOne(input);
        boolean outputIsOne = embedder.representsOne(embedder.flip(input));

        Assertions.assertNotEquals(inputIsOne, outputIsOne);
    }

    @Test
    protected void testFlipBitLowerEdge() throws EmbedderInputException {
        Embedder<T> embedder = getEmbedder();

        T input = getLowerEdge();

        boolean inputIsOne = embedder.representsOne(input);
        boolean outputIsOne = embedder.representsOne(embedder.flip(input));

        Assertions.assertNotEquals(inputIsOne, outputIsOne);
    }

    @Test
    protected void testFlip_2sameOutput() throws EmbedderInputException {
        Embedder<T> embedder = getEmbedder();

        T input = getRandomInput();
        T output1 = embedder.flip(input);
        T output2 = embedder.flip(input);

        assertEquals(output1, output2);
    }

    @Test
    protected void testFlip_2sameOutput_often() throws EmbedderInputException {
        Embedder<T> embedder = getEmbedder();

        for (int i = 0; i < 10; i++) {
            T input = getRandomInput();
            T output1 = embedder.flip(input);
            T output2 = embedder.flip(input);

            assertEquals(output1, output2, "in iteration " + i);
        }
    }

    //////////////////////////////////////////////////////////////////////////////
    //                               FAIL
    //////////////////////////////////////////////////////////////////////////////

}
