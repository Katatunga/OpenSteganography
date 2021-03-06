package steganography.image.exceptions;

import steganography.exceptions.UnsupportedMediaTypeException;

/**
 * Thrown if an operation was attempted on an image type it doesn't support.
 */
public class UnsupportedImageTypeException extends UnsupportedMediaTypeException {

    public UnsupportedImageTypeException() {
        super();
    }

    public UnsupportedImageTypeException(String message) {
        super(message);
    }
}
