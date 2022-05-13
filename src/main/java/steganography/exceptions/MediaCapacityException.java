package steganography.exceptions;

/**
 * Thrown if the medias capacity is too small to fit the message to hide
 */
public class MediaCapacityException extends SteganographyException {

    public MediaCapacityException() {
        super();
    }

    public MediaCapacityException(String message) {
        super(message);
    }
}
