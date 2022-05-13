package steganography.exceptions;

/**
 * Thrown if the format of a steganographically encoded (hidden) message cannot be determined and therefore
 * the message cannot be decoded (i.e. an expected header was not found).
 * The same applies, if there is no hidden message.
 */
public class UnknownStegFormatException extends SteganographyException {

    public UnknownStegFormatException() {
        super();
    }

    public UnknownStegFormatException(String message) {
        super(message);
    }
}
