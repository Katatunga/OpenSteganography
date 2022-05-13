package steganography.exceptions.encoder;

import steganography.image.innerStructure.embedders.Embedder;

/**
 * Thrown if an {@link Embedder Embedder} received a wrong input.
 */
public class EmbedderInputException extends EncoderException {

    public EmbedderInputException() {
        super();
    }

    public EmbedderInputException(String message) {
        super(message);
    }
}
