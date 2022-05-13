package steganography.image.innerStructure.encoders;

import steganography.exceptions.DamagedMessageException;
import steganography.exceptions.encoder.EncoderException;
import steganography.image.exceptions.ImageCapacityException;

public interface Encoder {

    /**
     * Seed which is used for randomization if no other seed is provided.
     */
    long DEFAULT_SEED = -915016527058693274L;

    void encode(byte[] payload) throws ImageCapacityException, EncoderException;

    void encode(byte[] payload, long seed) throws ImageCapacityException, EncoderException;

    byte[] decode(int bLength) throws DamagedMessageException, EncoderException, ImageCapacityException;

    byte[] decode(int bLength, long seed) throws DamagedMessageException, EncoderException, ImageCapacityException;

    /**
     * @return remaining encoding capacity in number of bits.
     */
    int available();
}
