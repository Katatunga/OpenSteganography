package steganography.exceptions;

/**
 * Thrown if the attempt to read a certain type of media failed.
 */
public class MediaNotFoundException extends SteganographyException {

    public MediaNotFoundException() {
        super();
    }

    public MediaNotFoundException(String message) {
        super(message);
    }
}
