package steganography.exceptions;

/**
 * Thrown if the steganographically encoded (hidden) message cannot be extracted correctly.
 * This will probably be due to damages (like bit-flips) occurring during transmission.
 */
public class DamagedMessageException extends SteganographyException {

    public DamagedMessageException() {
        super();
    }

    public DamagedMessageException(String message) {
        super(message);
    }
}
