package steganography.exceptions;

/**
 * Thrown if an operation was attempted on a media it doesn't support.
 */
public class UnsupportedMediaTypeException extends SteganographyException {

    public UnsupportedMediaTypeException() {
        super();
    }

    public UnsupportedMediaTypeException(String message) {
        super(message);
    }
}
