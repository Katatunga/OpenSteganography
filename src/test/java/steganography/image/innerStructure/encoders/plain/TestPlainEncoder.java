package steganography.image.innerStructure.encoders.plain;

import org.junit.jupiter.api.Assertions;
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

public class TestPlainEncoder extends TestEncoders {

    public TestPlainEncoder() {
        this.minCvrLength = MAX_PAYLOAD_LENGTH * 8;
    }

    @Override
    protected Encoder getEncoder(BitSet cvr, boolean sequential) {
        return new PlainEncoder<>(
                new MockEmbedder(),
                new MockOverlay(cvr),
                sequential
        );
    }

    @Override
    protected Encoder getThrowingEncoder(BitSet cvr, boolean sequential) {
        return new PlainEncoder<>(
                new ThrowingEmbedder(),
                new MockOverlay(cvr),
                sequential
        );
    }

    @Override
    protected Encoder getCountingEncoder(BitSet cvr, boolean sequential, CountingOverlay countingOverlay) {
        return new PlainEncoder<>(
                new MockEmbedder(),
                countingOverlay,
                sequential
        );
    }

    /**
     * Tests encode and immediate decode wih same encoder
     */
    @Test
    void test_enDecode_sameMessage() throws EncoderException, ImageCapacityException, DamagedMessageException {
        BitSet cvr = new BitSet();
        cvr.set(minCvrLength + 1);

        Encoder encoder = getEncoder(cvr, false);
        byte[] payload = getRandomBytes(MAX_PAYLOAD_LENGTH);

        encoder.encode(payload);
        byte[] decoded = encoder.decode(payload.length);

        Assertions.assertArrayEquals(payload, decoded);
    }

    /**
     * Tests decode of cover == BitSet.valueOf as cover
     */
    @Test
    void test_decode() throws EncoderException, ImageCapacityException, DamagedMessageException {
        byte[] payload = getRandomBytes(MAX_PAYLOAD_LENGTH);
        BitSet cvr = BitSet.valueOf(payload);
        cvr.set(minCvrLength + 1);

        Encoder encoder = getEncoder(cvr, false);
        byte[] decoded = encoder.decode(payload.length);

        Assertions.assertArrayEquals(payload, decoded);
    }

    /**
     * Tests sequential encode with one encoder, decode with another
     */
    @Test
    void test_encode_fullAmount_worstCase() throws EncoderException, ImageCapacityException, DamagedMessageException {
        byte[] payload = getRandomBytes(MAX_PAYLOAD_LENGTH);
        byte[] inverse = new byte[MAX_PAYLOAD_LENGTH];
        for (int i = 0; i < payload.length; i++) {
            inverse[i] = (byte) (~payload[i] & 255);
        }

        BitSet cvr = BitSet.valueOf(inverse);
        cvr.set(minCvrLength);

        CountingOverlay countingOverlay = new CountingOverlay(cvr);

        Encoder encoder = getCountingEncoder(cvr, false, countingOverlay);

        encoder.encode(payload);

        encoder = getEncoder(cvr, false);
        byte[] decoded = encoder.decode(payload.length);

        assertArrayEquals(payload, decoded);
        assertEquals(countingOverlay.changes, payload.length * 8);
    }

    ////////////////////////////////////////////////////////////////////////////
    // FAIL
    ////////////////////////////////////////////////////////////////////////////

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

        assertArrayEquals(payload1, decoded1);
        assertFalse(Arrays.equals(payload2, decoded2));
        Assertions.assertArrayEquals(decoded1, decoded2);
    }
}
