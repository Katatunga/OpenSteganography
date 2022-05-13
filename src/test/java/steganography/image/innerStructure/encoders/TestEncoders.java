package steganography.image.innerStructure.encoders;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import steganography.exceptions.DamagedMessageException;
import steganography.exceptions.encoder.EmbedderInputException;
import steganography.exceptions.encoder.EncoderException;
import steganography.image.innerStructure.encoders.mocks.CountingOverlay;
import steganography.image.exceptions.ImageCapacityException;

import java.util.Arrays;
import java.util.BitSet;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

public abstract class TestEncoders {

    private final Random random;
    protected static final int MAX_PAYLOAD_LENGTH = 10;
    protected int minCvrLength = MAX_PAYLOAD_LENGTH * 8;

    public TestEncoders() {
        this.random = new Random(1L);
    }

    protected byte[] getRandomBytes(int length) {
        byte[] payload = new byte[length];;
        this.random.nextBytes(payload);
        return payload;
    }

    protected abstract Encoder getEncoder(BitSet cvr, boolean sequential);
    protected abstract Encoder getThrowingEncoder(BitSet cvr, boolean sequential);
    protected abstract Encoder getCountingEncoder(BitSet cvr, boolean sequential, CountingOverlay countingEmbedder);


    //////////////////////////////////////////////////////////////////////////////
    //                               ENCODE
    //////////////////////////////////////////////////////////////////////////////

    /**
     * Tests encode with one encoder, look for changes
     */
    @Test
    void test_emptyCvr_encodeSingle_changed() throws EncoderException, ImageCapacityException, DamagedMessageException {
        BitSet cvr = new BitSet();
        cvr.set(minCvrLength + 1);

        byte[] before = cvr.toByteArray();

        Encoder encoder = getEncoder(cvr, false);
        byte[] payload = getRandomBytes(MAX_PAYLOAD_LENGTH);

        encoder.encode(payload);
        cvr.set(minCvrLength + 1);

        byte[] after = cvr.toByteArray();

        assertFalse(Arrays.equals(before, after));
    }

    /**
     * Tests encode with one encoder, look for changes
     */
    @Test
    void test_emptyCvr_encodeSequential_changed() throws EncoderException, ImageCapacityException, DamagedMessageException {
        BitSet cvr = new BitSet();
        cvr.set(minCvrLength + 1);

        byte[] before = cvr.toByteArray();

        Encoder encoder = getEncoder(cvr, true);
        byte[] payload = getRandomBytes(MAX_PAYLOAD_LENGTH);

        encoder.encode(payload);
        cvr.set(minCvrLength + 1);

        byte[] after = cvr.toByteArray();

        assertFalse(Arrays.equals(before, after));
    }

    /**
     * Tests encode with one encoder, look for changes
     */
    @Test
    void test_randomCvr_encodeSingle_changed() throws EncoderException, ImageCapacityException, DamagedMessageException {
        BitSet cvr = BitSet.valueOf(getRandomBytes(minCvrLength));
        cvr.set(minCvrLength + 1);

        byte[] before = cvr.toByteArray();

        Encoder encoder = getEncoder(cvr, false);
        byte[] payload = getRandomBytes(MAX_PAYLOAD_LENGTH);

        encoder.encode(payload);
        cvr.set(minCvrLength + 1);

        byte[] after = cvr.toByteArray();

        assertFalse(Arrays.equals(before, after));
    }

    /**
     * Tests encode with one encoder, look for changes
     */
    @Test
    void test_randomCvr_encodeSequential_changed() throws EncoderException, ImageCapacityException, DamagedMessageException {
        BitSet cvr = BitSet.valueOf(getRandomBytes(minCvrLength));
        cvr.set(minCvrLength + 1);

        byte[] before = cvr.toByteArray();

        Encoder encoder = getEncoder(cvr, true);
        byte[] payload = getRandomBytes(MAX_PAYLOAD_LENGTH);

        encoder.encode(payload);
        cvr.set(minCvrLength + 1);

        byte[] after = cvr.toByteArray();

        assertFalse(Arrays.equals(before, after));
    }

    //////////////////////////////////////////////////////////////////////////////
    //                            ENCODE_DECODE
    //////////////////////////////////////////////////////////////////////////////

    /**
     * Tests encode with one encoder, decode with another
     */
    @Test
    void test_emptyCvr_encode_decode_sameMessage() throws EncoderException, ImageCapacityException, DamagedMessageException {
        BitSet cvr = new BitSet();
        cvr.set(minCvrLength + 1);

        Encoder encoder = getEncoder(cvr, false);
        byte[] payload = getRandomBytes(MAX_PAYLOAD_LENGTH);

        encoder.encode(payload);
        cvr.set(minCvrLength + 1);

        encoder = getEncoder(cvr, false);
        byte[] decoded = encoder.decode(payload.length);

        Assertions.assertArrayEquals(payload, decoded);
    }

    /**
     * Tests encode with one encoder, decode with another
     */
    @Test
    void test_randomCvr_encode_decode_sameMessage() throws EncoderException, ImageCapacityException, DamagedMessageException {
        BitSet cvr = BitSet.valueOf(getRandomBytes(minCvrLength));
        cvr.set(minCvrLength + 1);

        Encoder encoder = getEncoder(cvr, false);
        byte[] payload = getRandomBytes(MAX_PAYLOAD_LENGTH);

        encoder.encode(payload);

        encoder = getEncoder(cvr, false);
        byte[] decoded = encoder.decode(payload.length);

        Assertions.assertArrayEquals(payload, decoded);
    }

    /**
     * Tests sequential encode with one encoder, decode with another
     */
    @Test
    void test_emptyCvr_sequential_encode_decodeMessage() throws EncoderException, ImageCapacityException, DamagedMessageException {
        BitSet cvr = new BitSet();
        cvr.set(minCvrLength*2 + 1);

        Encoder encoder = getEncoder(cvr, true);
        byte[] payload1 = getRandomBytes(MAX_PAYLOAD_LENGTH);
        byte[] payload2 = getRandomBytes(MAX_PAYLOAD_LENGTH);

        encoder.encode(payload1);
        encoder.encode(payload2);

        encoder = getEncoder(cvr, true);
        byte[] decoded1 = encoder.decode(payload1.length);
        byte[] decoded2 = encoder.decode(payload2.length);


        Assertions.assertArrayEquals(payload1, decoded1);
        Assertions.assertArrayEquals(payload2, decoded2);
    }

    /**
     * Tests sequential encode with one encoder, decode with another
     */
    @Test
    void test_randomCvr_sequential_encode_decodeMessage() throws EncoderException, ImageCapacityException, DamagedMessageException {
        BitSet cvr = BitSet.valueOf(getRandomBytes(minCvrLength*2));
        cvr.set(minCvrLength*2 + 1);

        Encoder encoder = getEncoder(cvr, true);
        byte[] payload1 = getRandomBytes(MAX_PAYLOAD_LENGTH);
        byte[] payload2 = getRandomBytes(MAX_PAYLOAD_LENGTH);

        encoder.encode(payload1);
        encoder.encode(payload2);

        encoder = getEncoder(cvr, true);
        byte[] decoded1 = encoder.decode(payload1.length);
        byte[] decoded2 = encoder.decode(payload2.length);


        Assertions.assertArrayEquals(payload1, decoded1);
        Assertions.assertArrayEquals(payload2, decoded2);
    }

    //////////////////////////////////////////////////////////////////////////////
    //                               DECODE
    //////////////////////////////////////////////////////////////////////////////

    /**
     * Tests 2x decode to be the same
     */
    @Test
    void test_emptyCvr_decodesSame_single() throws EncoderException, ImageCapacityException, DamagedMessageException {
        BitSet cvr = new BitSet();
        cvr.set(minCvrLength + 1);

        Encoder encoder = getEncoder(cvr, false);
        byte[] decoded1 = encoder.decode(MAX_PAYLOAD_LENGTH);

        encoder = getEncoder(cvr, false);
        byte[] decoded2 = encoder.decode(MAX_PAYLOAD_LENGTH);

        Assertions.assertArrayEquals(decoded1, decoded2);
    }

    /**
     * Tests 2x decode to be the same
     */
    @Test
    void test_randomCvr_decodesSame_single() throws EncoderException, ImageCapacityException, DamagedMessageException {
        BitSet cvr = BitSet.valueOf(getRandomBytes(minCvrLength));
        cvr.set(minCvrLength + 1);

        Encoder encoder = getEncoder(cvr, false);
        byte[] decoded1 = encoder.decode(MAX_PAYLOAD_LENGTH);

        encoder = getEncoder(cvr, false);
        byte[] decoded2 = encoder.decode(MAX_PAYLOAD_LENGTH);

        Assertions.assertArrayEquals(decoded1, decoded2);
    }

    /**
     * Tests 2x decode to be the same
     */
    @Test
    void test_emptyCvr_decodesSame_sequential() throws EncoderException, ImageCapacityException, DamagedMessageException {
        BitSet cvr = new BitSet();
        cvr.set(minCvrLength + 1);

        Encoder encoder = getEncoder(cvr, true);
        byte[] decoded1 = encoder.decode(MAX_PAYLOAD_LENGTH);

        encoder = getEncoder(cvr, true);
        byte[] decoded2 = encoder.decode(MAX_PAYLOAD_LENGTH);

        Assertions.assertArrayEquals(decoded1, decoded2);
    }

    /**
     * Tests 2x decode to be the same
     */
    @Test
    void test_randomCvr_decodesSame_sequential() throws EncoderException, ImageCapacityException, DamagedMessageException {
        BitSet cvr = BitSet.valueOf(getRandomBytes(minCvrLength));
        cvr.set(minCvrLength + 1);

        Encoder encoder = getEncoder(cvr, true);
        byte[] decoded1 = encoder.decode(MAX_PAYLOAD_LENGTH);

        encoder = getEncoder(cvr, true);
        byte[] decoded2 = encoder.decode(MAX_PAYLOAD_LENGTH);

        Assertions.assertArrayEquals(decoded1, decoded2);
    }

    /**
     * Tests 2x decode sequential to be different
     */
    @Test
    void test_almostEmptyCvr_decodesDiff_sequential() throws EncoderException, ImageCapacityException, DamagedMessageException {
        BitSet cvr = new BitSet();
        cvr.set(minCvrLength * 2 + 1);

        Encoder encoder = getEncoder(cvr, true);
        byte[] decoded1 = encoder.decode(MAX_PAYLOAD_LENGTH);
        byte[] decoded2 = encoder.decode(MAX_PAYLOAD_LENGTH);

        assertArrayEquals(decoded1, decoded2);
    }

    /**
     * Tests 2x decode sequential to be different
     */
    @Test
    void test_randomCvr_decodesDiff_sequential() throws EncoderException, ImageCapacityException, DamagedMessageException {
        BitSet cvr = BitSet.valueOf(getRandomBytes(minCvrLength*2));
        cvr.set(minCvrLength*2 + 1);

        Encoder encoder = getEncoder(cvr, true);
        byte[] decoded1 = encoder.decode(MAX_PAYLOAD_LENGTH);

        byte[] decoded2 = encoder.decode(MAX_PAYLOAD_LENGTH);

        assertFalse(Arrays.equals(decoded1, decoded2));
    }



    ///////////////////////////////////////////////////////////////////////////////
    // FAILS
    ///////////////////////////////////////////////////////////////////////////////

    /**
     * Tests sequential encode with one encoder, decode with another
     */
    @Test
    void test_sequentialEncode_singleDecode_expectFail() throws EncoderException, ImageCapacityException, DamagedMessageException {
        BitSet cvr = new BitSet();
        cvr.set(minCvrLength*4 + 1);

        Encoder encoder = getEncoder(cvr, true);
        byte[] payload1 = getRandomBytes(MAX_PAYLOAD_LENGTH);
        byte[] payload2 = getRandomBytes(MAX_PAYLOAD_LENGTH);

        encoder.encode(payload1);
        encoder.encode(payload2);

        encoder = getEncoder(cvr, false);
        byte[] decoded1 = encoder.decode(payload1.length);
        byte[] decoded2 = encoder.decode(payload2.length);

        assertFalse(Arrays.equals(payload1, decoded1));
        assertFalse(Arrays.equals(payload2, decoded2));
        Assertions.assertArrayEquals(decoded1, decoded2);
    }

    /**
     * Tests too small cover
     */
    @Test
    void test_encode_coverTooSmall_expectFail() throws EncoderException, ImageCapacityException, DamagedMessageException {
        BitSet cvr = new BitSet();
        cvr.set(minCvrLength - 2);

        Encoder encoder = getEncoder(cvr, true);
        byte[] payload1 = getRandomBytes(MAX_PAYLOAD_LENGTH);

        assertThrows(ImageCapacityException.class, () -> encoder.encode(payload1));
    }

    @Test
    void test_expectThrow_EncoderException() throws EmbedderInputException {
        BitSet cvr = new BitSet();
        cvr.set(minCvrLength);
        Assertions.assertThrows(EncoderException.class,
                () -> getThrowingEncoder(cvr, false).encode(new byte[1]));
    }

    @Test
    void testInputTooSmall_expectThrow_EncoderException() throws EmbedderInputException {
        BitSet cvr = new BitSet();
        cvr.set(minCvrLength);
        Assertions.assertThrows(EncoderException.class,
                () -> getThrowingEncoder(cvr, false).decode(1));
    }
}
