package steganography.image.innerStructure.encoders.wrappers;

import steganography.exceptions.DamagedMessageException;
import steganography.exceptions.encoder.EncoderException;
import steganography.image.innerStructure.encoders.Encoder;
import steganography.image.exceptions.ImageCapacityException;
import steganography.image.operation.reedsolomon.GenericGF;
import steganography.image.operation.reedsolomon.ReedSolomonDecoder;
import steganography.image.operation.reedsolomon.ReedSolomonEncoder;
import steganography.image.operation.reedsolomon.ReedSolomonException;
import steganography.util.ArrayUtils;

public class ReedSolomon implements Encoder {

    protected final Encoder enDecoder;

    private final float errorRate;
    private static final GenericGF galoisField = GenericGF.DATA_MATRIX_FIELD_256;

    public ReedSolomon(Encoder enDecoder) {
        this.enDecoder = enDecoder;
        this.errorRate = .5f;
    }

    public ReedSolomon(Encoder enDecoder, float prepErrorRate) {
        this.enDecoder = enDecoder;

        if (prepErrorRate > 1 || prepErrorRate < 0)
            throw new IllegalArgumentException("The prepared error rate must be between 0 and 1 (both inclusive)");
        this.errorRate = prepErrorRate;
    }


    private int redundancy(double payloadLength) {
        return 2 * Double.valueOf(Math.ceil(payloadLength * errorRate)).intValue();
    }

    @Override
    public void encode(byte[] payload) throws ImageCapacityException, EncoderException {
        encode(payload, DEFAULT_SEED);
    }

    @Override
    public void encode(byte[] payload, long seed) throws ImageCapacityException, EncoderException {
        int available = this.available();
        int bitLen = payload.length * 8;
        if (bitLen > available)
            throw new ImageCapacityException(String.format(
                    "Payload (%d) is longer than (remaining) Image capacity (%d) (error correction " +
                            "included in calculation)",
                    bitLen, available));

        int redundancy = redundancy(payload.length);
        int[] rsPayload = new int[payload.length + redundancy];


        ArrayUtils.bytes2Ints(payload, rsPayload);
        new ReedSolomonEncoder(galoisField).encode(rsPayload, redundancy);

        this.enDecoder.encode(ArrayUtils.ints2Bytes(rsPayload, new byte[rsPayload.length]));
    }

    @Override
    public byte[] decode(int bLength) throws DamagedMessageException, EncoderException, ImageCapacityException {
        return decode(bLength, DEFAULT_SEED);
    }

    @Override
    public byte[] decode(int bLength, long seed) throws DamagedMessageException, EncoderException, ImageCapacityException {
        int redundancy = redundancy(bLength);

        byte[] msg = this.enDecoder.decode(bLength + redundancy);

        int[] intPayload = ArrayUtils.bytes2Ints(msg, new int[msg.length]);

        try {
            new ReedSolomonDecoder(galoisField).decode(intPayload, redundancy);
        } catch (ReedSolomonException e) {
            throw new DamagedMessageException("Message contains too many errors to be decoded: " + e.getMessage());
        }

        return ArrayUtils.ints2Bytes(intPayload, new byte[bLength]);
    }

    @Override
    public int available() {
        return this.enDecoder.available() / Double.valueOf(1 + (errorRate * 2)).intValue();
    }
}
