package steganography.exceptions;

/**
 * Thrown if the media could not be reassembled successfully after encoding a message in it.
 */
public class MediaReassemblingException extends SteganographyException {

    public MediaReassemblingException() {
        super();
    }

    public MediaReassemblingException(String message) {
        super(message);
    }
}
