package steganography.exceptions;

/**
 * Super to all exceptions in this library
 */
public class SteganographyException extends Exception{

    public SteganographyException() {
        super();
    }

    public SteganographyException(String message) {
        super(message);
    }
}
