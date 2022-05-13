package steganography.image.innerStructure.embedders.dct;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import steganography.exceptions.encoder.EmbedderInputException;
import steganography.image.innerStructure.embedders.Embedder;
import steganography.image.innerStructure.embedders.TestEmbeddersUnit;
import steganography.image.operation.pixelTranslation.PixelTranslator;
import steganography.image.operation.pixelTranslation.Rgb2YCbCr;
import steganography.transforms.FastDct8;
import steganography.transforms.Transform;
import steganography.util.Quantizer;

import java.util.Arrays;
import java.util.Random;

public abstract class TestBlockEmbeddersUnit extends TestEmbeddersUnit<int[]> {
    private final Random random;
    Transform<double[][]> dct;

    protected static final float QF = .85f;

    public TestBlockEmbeddersUnit() {
        this.random = new Random(128);
        this.dct = new FastDct8();
    }

    @Override
    protected int[] getRandomInput() {
        return this.random.ints(getLength()).toArray();
    }

    @Override
    protected int[] getUpperEdge() {
        int[] upper = new int[getLength()];
        Arrays.fill(upper, 0xffffffff);
        return upper;
    }

    @Override
    protected int[] getLowerEdge() {
        int[] lower = new int[getLength()];
        Arrays.fill(lower, 0xff000000);
        return lower;
    }

    protected abstract int getLength();

    @Override
    protected void assertEquals(int[] expected, int[] actual) {
        Assertions.assertArrayEquals(expected, actual);
    }

    @Override
    protected void assertEquals(int[] expected, int[] actual, String message) {
        Assertions.assertArrayEquals(expected, actual, message);
    }

    //////////////////////////////////////////////////////////////////////////////
    //                               RECOMPRESS
    //////////////////////////////////////////////////////////////////////////////

    private int[] recompress(int[] values) {
        PixelTranslator pt = new Rgb2YCbCr(values, 8);
        pt.setValues(
                this.dct.reverse(
                        Quantizer.prequantize(
                                this.dct.forward(
                                        pt.getValues()
                                ),
                                QF)
                )
        );
        return pt.asARGB();
    }

    //////////////////////////////////////////////////////////////////////////////
    //                               EMBED
    //////////////////////////////////////////////////////////////////////////////

    @Test
    protected void testEmbedTrueRandom_recompressed() throws EmbedderInputException {
        Embedder<int[]> embedder = getEmbedder();

        for (int i = 0; i < 10; i++) {
            int[] input = getRandomInput();
            int[] output_rc = recompress(embedder.embed(input, true));
            Assertions.assertTrue(embedder.representsOne(output_rc), "in iteration " + i);
        }
    }

    @Test
    protected void testEmbedFalseRandom_recompressed() throws EmbedderInputException {
        Embedder<int[]> embedder = getEmbedder();

        for (int i = 0; i < 10; i++) {
            int[] input = getRandomInput();
            int[] output_rc = recompress(embedder.embed(input, false));
            Assertions.assertFalse(embedder.representsOne(output_rc), "in iteration " + i);
        }
    }

    @Test
    protected void testEmbedTrueUpperEdge_recompressed() throws EmbedderInputException {
        Embedder<int[]> embedder = getEmbedder();

        for (int i = 0; i < 10; i++) {
            int[] input = getUpperEdge();
            int[] output_rc = recompress(embedder.embed(input, true));
            Assertions.assertTrue(embedder.representsOne(output_rc), "in iteration " + i);
        }
    }

    @Test
    protected void testEmbedFalseUpperEdge_recompressed() throws EmbedderInputException {
        Embedder<int[]> embedder = getEmbedder();

        for (int i = 0; i < 10; i++) {
            int[] input = getUpperEdge();
            int[] output_rc = recompress(embedder.embed(input, false));
            Assertions.assertFalse(embedder.representsOne(output_rc), "in iteration " + i);
        }
    }

    @Test
    protected void testEmbedTrueLowerEdge_recompressed() throws EmbedderInputException {
        Embedder<int[]> embedder = getEmbedder();

        for (int i = 0; i < 10; i++) {
            int[] input = getLowerEdge();
            int[] output_rc = recompress(embedder.embed(input, true));
            Assertions.assertTrue(embedder.representsOne(output_rc), "in iteration " + i);
        }
    }

    @Test
    protected void testEmbedFalseLowerEdge_recompressed() throws EmbedderInputException {
        Embedder<int[]> embedder = getEmbedder();

        for (int i = 0; i < 10; i++) {
            int[] input = getLowerEdge();
            int[] output_rc = recompress(embedder.embed(input, false));
            Assertions.assertFalse(embedder.representsOne(output_rc), "in iteration " + i);
        }
    }

    //////////////////////////////////////////////////////////////////////////////
    //                               FLIP
    //////////////////////////////////////////////////////////////////////////////

    @Test
    protected void testFlipSingleBit_recompressed() throws EmbedderInputException {
        Embedder<int[]> embedder = getEmbedder();

        for (int i = 0; i < 10; i++) {
            int[] input = getRandomInput();

            boolean inputIsOne = embedder.representsOne(input);
            int[] flipped = embedder.flip(input);
            boolean outputIsOne = embedder.representsOne(recompress(flipped));

            Assertions.assertNotEquals(inputIsOne, outputIsOne, "in iteration " + i);
        }
    }

    @Test
    protected void testFlipBitUpperEdge_recompressed() throws EmbedderInputException {
        Embedder<int[]> embedder = getEmbedder();

        int[] input = getUpperEdge();

        boolean inputIsOne = embedder.representsOne(input);
        int[] flipped = embedder.flip(input);
        boolean outputIsOne = embedder.representsOne(recompress(flipped));

        Assertions.assertNotEquals(inputIsOne, outputIsOne);
    }

    @Test
    protected void testFlipBitLowerEdge_recompressed() throws EmbedderInputException {
        Embedder<int[]> embedder = getEmbedder();

        int[] input = getLowerEdge();

        boolean inputIsOne = embedder.representsOne(input);
        int[] flipped = embedder.flip(input);
        boolean outputIsOne = embedder.representsOne(recompress(flipped));

        Assertions.assertNotEquals(inputIsOne, outputIsOne);
    }

    //////////////////////////////////////////////////////////////////////////////
    //                               FAIL
    //////////////////////////////////////////////////////////////////////////////

    @Test
    void testInputTooSmall_expectThrow_EmbedderInputException() throws EmbedderInputException {
        Assertions.assertThrows(EmbedderInputException.class, () -> getEmbedder().embed(new int[16], true));
    }

    @Test
    void testInputTooBig_expectThrow_EmbedderInputException() throws EmbedderInputException {
        Assertions.assertThrows(EmbedderInputException.class, () -> getEmbedder().embed(new int[1024], true));
    }

    @Test
    void testInputEmpty_expectThrow_EmbedderInputException() throws EmbedderInputException {
        Assertions.assertThrows(EmbedderInputException.class, () -> getEmbedder().embed(new int[0], true));
    }

    @Test
    void testInputOdd_expectThrow_EmbedderInputException() throws EmbedderInputException {
        Assertions.assertThrows(EmbedderInputException.class, () -> getEmbedder().embed(new int[63], true));
    }
}
