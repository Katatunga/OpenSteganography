package steganography.image.innerStructure.encoders.stc;

import org.junit.jupiter.api.Test;
import steganography.exceptions.DamagedMessageException;
import steganography.exceptions.encoder.EncoderException;
import steganography.image.innerStructure.encoders.Encoder;
import steganography.image.innerStructure.encoders.mocks.CountingOverlay;
import steganography.image.innerStructure.encoders.mocks.MockEmbedder;
import steganography.image.innerStructure.encoders.mocks.MockOverlay;
import steganography.image.innerStructure.encoders.mocks.ThrowingEmbedder;
import steganography.image.innerStructure.encoders.TestEncoders;
import steganography.image.exceptions.ImageCapacityException;

import java.util.Arrays;
import java.util.BitSet;

import static org.junit.jupiter.api.Assertions.*;

public class TestStcEncoder extends TestEncoders {

    public TestStcEncoder() {
        this.minCvrLength = MAX_PAYLOAD_LENGTH * 2 * 8;
    }

    @Override
    protected Encoder getEncoder(BitSet cvr, boolean sequential) {
        return new StcEncoder<>(
                new MockEmbedder(),
                new MockOverlay(cvr),
                (x, y) -> 1d,
                sequential
        );
    }

    @Override
    protected Encoder getThrowingEncoder(BitSet cvr, boolean sequential) {
        return new StcEncoder<>(
                new ThrowingEmbedder(),
                new MockOverlay(cvr),
                (x, y) -> 1d,
                sequential
        );
    }

    @Override
    protected Encoder getCountingEncoder(BitSet cvr, boolean sequential, CountingOverlay countingOverlay) {
        return new StcEncoder<>(
                new MockEmbedder(),
                countingOverlay,
                (x, y) -> 1d,
                sequential
        );
    }


    /////////////////////////////////////////////////////////////////////////////////////////////////
    //                                     ENCODE
    /////////////////////////////////////////////////////////////////////////////////////////////////


    /**
     * Tests encode with one encoder, look for changes
     */
    @Test
    void test_emptyCvr_encodeSingle_changedAmount() throws EncoderException, ImageCapacityException, DamagedMessageException {
        BitSet cvr = new BitSet();
        cvr.set(minCvrLength + 1);

        CountingOverlay countingOverlay = new CountingOverlay(cvr);

        Encoder encoder = getCountingEncoder(cvr, false, countingOverlay);
        byte[] payload = getRandomBytes(MAX_PAYLOAD_LENGTH);

        encoder.encode(payload);
        cvr.set(minCvrLength + 1);

        assertEquals(cvr.length(), countingOverlay.changes);
    }

    /**
     * Tests encode with one encoder, look for changes
     */
    @Test
    void test_emptyCvr_encodeSequential_changedAmount() throws EncoderException, ImageCapacityException, DamagedMessageException {
        BitSet cvr = new BitSet();
        cvr.set(minCvrLength + 1);

        CountingOverlay countingOverlay = new CountingOverlay(cvr);

        Encoder encoder = getCountingEncoder(cvr, true, countingOverlay);
        byte[] payload = getRandomBytes(MAX_PAYLOAD_LENGTH);

        encoder.encode(payload);
        cvr.set(minCvrLength + 1);

        assertEquals(payload.length*8*2, countingOverlay.changes);
    }

    /**
     * Tests encode with one encoder, look for changes
     */
    @Test
    void test_randomCvr_encodeSingle_changedAmount() throws EncoderException, ImageCapacityException, DamagedMessageException {
        BitSet cvr = BitSet.valueOf(getRandomBytes(MAX_PAYLOAD_LENGTH));
        cvr.set(minCvrLength + 1);

        CountingOverlay countingOverlay = new CountingOverlay(cvr);
        Encoder encoder = getCountingEncoder(cvr, false, countingOverlay);
        byte[] payload = getRandomBytes(MAX_PAYLOAD_LENGTH);

        encoder.encode(payload);
        cvr.set(minCvrLength + 1);

        assertEquals(cvr.length(), countingOverlay.changes);
    }

    /**
     * Tests encode with one encoder, look for changes
     */
    @Test
    void test_randomCvr_encodeSequential_changedAmount() throws EncoderException, ImageCapacityException, DamagedMessageException {
        BitSet cvr = BitSet.valueOf(getRandomBytes(minCvrLength));
        cvr.set(minCvrLength + 1);

        CountingOverlay countingOverlay = new CountingOverlay(cvr);

        Encoder encoder = getCountingEncoder(cvr, true, countingOverlay);
        byte[] payload = getRandomBytes(MAX_PAYLOAD_LENGTH);

        encoder.encode(payload);
        cvr.set(minCvrLength + 1);

        assertEquals(payload.length*8*2, countingOverlay.changes);
    }

    /**
     * Tests sequential encode with one encoder, decode with another
     */
    @Test
    void test_encode_fewerThanPayload_worstCase_single() throws EncoderException, ImageCapacityException, DamagedMessageException {
        byte[] payload = getRandomBytes(MAX_PAYLOAD_LENGTH);
        byte[] inverse = new byte[MAX_PAYLOAD_LENGTH];
        for (int i = 0; i < payload.length; i++) {
            inverse[i] = (byte) (~payload[i] & 255);
        }

        BitSet cvr = BitSet.valueOf(inverse);
        cvr.set(minCvrLength-1);

        CountingOverlay countingOverlay = new CountingOverlay(cvr);

        Encoder encoder = getCountingEncoder(cvr, false, countingOverlay);

        encoder.encode(payload);

        encoder = getEncoder(cvr, false);
        byte[] decoded = encoder.decode(payload.length);

        assertArrayEquals(payload, decoded);
        assertEquals(cvr.length(), countingOverlay.changes);
    }

    /**
     * Tests sequential encode with one encoder, decode with another
     */
    @Test
    void test_encode_fewerThanPayload_worstCase_sequential() throws EncoderException, ImageCapacityException, DamagedMessageException {
        byte[] payload = getRandomBytes(MAX_PAYLOAD_LENGTH);
        byte[] inverse = new byte[MAX_PAYLOAD_LENGTH];
        for (int i = 0; i < payload.length; i++) {
            inverse[i] = (byte) (~payload[i] & 255);
        }

        BitSet cvr = BitSet.valueOf(inverse);
        cvr.set(minCvrLength - 1);

        CountingOverlay countingOverlay = new CountingOverlay(cvr);

        Encoder encoder = getCountingEncoder(cvr, true, countingOverlay);

        encoder.encode(payload);

        encoder = getEncoder(cvr, true);
        byte[] decoded = encoder.decode(payload.length);

        assertArrayEquals(payload, decoded);
        assertEquals(countingOverlay.changes, payload.length * 8 * 2);
    }

    /**
     * Tests sequential encode with one encoder, decode with another
     */
    @Test
    void test_encode_fewerThanPayload_worstCase_sequentialThenSingle() throws EncoderException, ImageCapacityException, DamagedMessageException {
        byte[] payload = getRandomBytes(MAX_PAYLOAD_LENGTH);
        byte[] inverse = new byte[MAX_PAYLOAD_LENGTH];
        for (int i = 0; i < payload.length; i++) {
            inverse[i] = (byte) (~payload[i] & 255);
        }

        BitSet cvr = BitSet.valueOf(inverse);
        cvr.set(minCvrLength*2);

        CountingOverlay countingOverlay = new CountingOverlay(cvr);

        Encoder encoder = getCountingEncoder(cvr, true, countingOverlay);

        encoder.encode(payload);

        encoder = getEncoder(cvr, false);
        byte[] decoded = encoder.decode(payload.length);

        assertFalse(Arrays.equals(payload, decoded));
        assertEquals(countingOverlay.changes, payload.length * 8 * 2);
    }

    /////////////////////////////////////////////////////////////////////////////////
    // FAIL
    /////////////////////////////////////////////////////////////////////////////////

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
    }

}
