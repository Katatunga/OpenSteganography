package steganography.exceptions.encoder;

import steganography.exceptions.SteganographyException;
import steganography.image.innerStructure.embedders.Embedder;

/**
 * Thrown if an {@link Embedder Embedder} received a wrong input.
 */
public class EncoderException extends SteganographyException {

    public EncoderException() {
        super();
    }

    public EncoderException(String message) {
        super(message);
    }
}
