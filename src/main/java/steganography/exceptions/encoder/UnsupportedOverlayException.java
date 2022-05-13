package steganography.exceptions.encoder;

import steganography.exceptions.SteganographyException;

/**
 * Thrown if an operation was attempted on a media it doesn't support.
 */
public class UnsupportedOverlayException extends EncoderException {

    public UnsupportedOverlayException() {
        super();
    }

    public UnsupportedOverlayException(String message) {
        super(message);
    }
}
