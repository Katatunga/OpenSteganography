package steganography.image.innerStructure.integrations;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import steganography.exceptions.DamagedMessageException;
import steganography.exceptions.encoder.EncoderException;
import steganography.image.innerStructure.encoders.Encoder;
import steganography.image.exceptions.ImageCapacityException;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.BitSet;
import java.util.Random;

/**
 * These may look like filler tests, but they cover all the basics of construction, encode and decode integration tests
 * without too much disturbance. There could be more combinations tested, but this would not improve comprehensibility.
 * Please understand these tests as a description of the overall behaviour of certain inner structure combinations.
 * They can be extended easily to test your own combinations and decide a set of configurations.
 */
public abstract class TestInnerStructureBasics {
    private static final String PS = File.separator;
    protected final String baseFilePath = String.join(PS, "src", "test", "resources", "steganography", "image") + PS;
    protected final String baseFileName = "baum";

    // adds
    protected final String eightDivisible = "8";
    protected final String transparent = "_TP";
    private final Random random;

    protected abstract Encoder getEncoder(BufferedImage image);

    public TestInnerStructureBasics() {
        this.random = new Random(0);
    }

    /////////////////////////////////////////////////////////////////////////////////////
    //                                 UTILITY
    /////////////////////////////////////////////////////////////////////////////////////

    protected BufferedImage getBufferedImage(String path) throws IOException {
        return ImageIO.read(new File(path));
    }

    protected byte[] getRandomBytes(int length) {
        byte[] r = new byte[length];
        this.random.nextBytes(r);
        return r;
    }

    /////////////////////////////////////////////////////////////////////////////////////
    //                                  ENCODE_ONLY
    /////////////////////////////////////////////////////////////////////////////////////

    void input_format_encodeNone(String add) throws IOException, EncoderException, ImageCapacityException {
        getEncoder(getBufferedImage(baseFilePath + baseFileName + add)).encode(new byte[0]);
    }

    void input_format_encodeOne_random(String add) throws IOException, EncoderException, ImageCapacityException {
        byte[] payload = getRandomBytes(1);
        getEncoder(getBufferedImage(baseFilePath + baseFileName + add)).encode(payload);
    }

    void input_format_encodeOne_zeroes(String add) throws IOException, EncoderException, ImageCapacityException {
        getEncoder(getBufferedImage(baseFilePath + baseFileName + add)).encode(new byte[]{0});
    }

    void input_format_encodeOne_ones(String add) throws IOException, EncoderException, ImageCapacityException {
        getEncoder(getBufferedImage(baseFilePath + baseFileName + add)).encode(new byte[]{(byte)0xff});
    }

    // JPG none 8 divisible

    @Test
    void input_JPG_encodeNone() throws IOException, EncoderException, ImageCapacityException {
        input_format_encodeNone(".jpg");
    }

    @Test
    void input_JPG_encodeOne_random() throws IOException, EncoderException, ImageCapacityException {
        input_format_encodeOne_random(".jpg");
    }

    @Test
    void input_JPG_encodeOne_zeroes() throws IOException, EncoderException, ImageCapacityException {
        input_format_encodeOne_ones(".jpg");
    }

    @Test
    void input_JPG_encodeOne_ones() throws IOException, EncoderException, ImageCapacityException {
        input_format_encodeOne_zeroes(".jpg");
    }

    // JPG 8 divisible

    @Test
    void input_JPG8_encodeNone() throws IOException, EncoderException, ImageCapacityException {
        input_format_encodeNone(eightDivisible + ".jpg");
    }

    @Test
    void input_JPG8_encodeOne_random() throws IOException, EncoderException, ImageCapacityException {
        input_format_encodeOne_random(eightDivisible + ".jpg");
    }

    @Test
    void input_JPG8_encodeOne_zeroes() throws IOException, EncoderException, ImageCapacityException {
        input_format_encodeOne_ones(eightDivisible + ".jpg");
    }

    @Test
    void input_JPG8_encodeOne_ones() throws IOException, EncoderException, ImageCapacityException {
        input_format_encodeOne_zeroes(eightDivisible + ".jpg");
    }

    // PNG none 8 divisible

    @Test
    void input_PNG_encodeNone() throws IOException, EncoderException, ImageCapacityException {
        input_format_encodeNone(".png");
    }

    @Test
    void input_PNG_encodeOne_random() throws IOException, EncoderException, ImageCapacityException {
        input_format_encodeOne_random(".png");
    }

    @Test
    void input_PNG_encodeOne_zeroes() throws IOException, EncoderException, ImageCapacityException {
        input_format_encodeOne_ones(".png");
    }

    @Test
    void input_PNG_encodeOne_ones() throws IOException, EncoderException, ImageCapacityException {
        input_format_encodeOne_zeroes(".png");
    }

    // PNG 8 divisible

    @Test
    void input_PNG8_encodeNone() throws IOException, EncoderException, ImageCapacityException {
        input_format_encodeNone(eightDivisible + ".png");
    }

    @Test
    void input_PNG8_encodeOne_random() throws IOException, EncoderException, ImageCapacityException {
        input_format_encodeOne_random(eightDivisible + ".png");
    }

    @Test
    void input_PNG8_encodeOne_zeroes() throws IOException, EncoderException, ImageCapacityException {
        input_format_encodeOne_ones(eightDivisible + ".png");
    }

    @Test
    void input_PNG8_encodeOne_ones() throws IOException, EncoderException, ImageCapacityException {
        input_format_encodeOne_zeroes(eightDivisible + ".png");
    }

    // PNG transparent none 8 divisible

    @Test
    void input_PNGTP_encodeNone() throws IOException, EncoderException, ImageCapacityException {
        input_format_encodeNone(transparent + ".png");
    }

    @Test
    void input_PNGTP_encodeOne_random() throws IOException, EncoderException, ImageCapacityException {
        input_format_encodeOne_random(transparent + ".png");
    }

    @Test
    void input_PNGTP_encodeOne_zeroes() throws IOException, EncoderException, ImageCapacityException {
        input_format_encodeOne_ones(transparent + ".png");
    }

    @Test
    void input_PNGTP_encodeOne_ones() throws IOException, EncoderException, ImageCapacityException {
        input_format_encodeOne_zeroes(transparent + ".png");
    }

    // PNG transparent 8 divisible

    @Test
    void input_PNGTP8_encodeNone() throws IOException, EncoderException, ImageCapacityException {
        input_format_encodeNone(eightDivisible + transparent + ".png");
    }

    @Test
    void input_PNGTP8_encodeOne_random() throws IOException, EncoderException, ImageCapacityException {
        input_format_encodeOne_random(eightDivisible + transparent + ".png");
    }

    @Test
    void input_PNGTP8_encodeOne_zeroes() throws IOException, EncoderException, ImageCapacityException {
        input_format_encodeOne_ones(eightDivisible + transparent + ".png");
    }

    @Test
    void input_PNGTP8_encodeOne_ones() throws IOException, EncoderException, ImageCapacityException {
        input_format_encodeOne_zeroes(eightDivisible + transparent + ".png");
    }

    /////////////////////////////////////////////////////////////////////////////////////
    //                                ENCODE DECODE
    /////////////////////////////////////////////////////////////////////////////////////

    void input_format_encodeDecode_inX_acceptY(String add, byte[] payload, int acceptErrors) throws IOException, EncoderException, ImageCapacityException, DamagedMessageException {
        BufferedImage bi = getBufferedImage(baseFilePath + baseFileName + add);
        getEncoder(bi).encode(payload);
        BitSet payload_bitSet = BitSet.valueOf(payload);
        payload_bitSet.xor(BitSet.valueOf(getEncoder(bi).decode(payload.length)));
        Assertions.assertEquals(acceptErrors, payload_bitSet.cardinality(), "First error index: " + payload_bitSet.nextSetBit(0));
    }

    void input_format_encodeDecodeNone(String add) throws IOException, EncoderException, ImageCapacityException, DamagedMessageException {
        input_format_encodeDecodeNone(add, 0);
    }

    protected void input_format_encodeDecodeNone(String add, int acceptErrors) throws IOException, EncoderException, ImageCapacityException, DamagedMessageException {
        input_format_encodeDecode_inX_acceptY(add, new byte[0], acceptErrors);
    }

    void input_format_encodeDecodeOne_random(String add) throws IOException, EncoderException, ImageCapacityException, DamagedMessageException {
        input_format_encodeDecodeOne_random(add, 0);
    }

    protected void input_format_encodeDecodeOne_random(String add, int acceptErrors) throws IOException, EncoderException, ImageCapacityException, DamagedMessageException {
        input_format_encodeDecode_inX_acceptY(add, getRandomBytes(1), acceptErrors);
    }

    void input_format_encodeDecodeOne_zeroes(String add) throws IOException, EncoderException, ImageCapacityException, DamagedMessageException {
        input_format_encodeDecodeOne_zeroes(add, 0);
    }

    protected void input_format_encodeDecodeOne_zeroes(String add, int acceptErrors) throws IOException, EncoderException, ImageCapacityException, DamagedMessageException {
        input_format_encodeDecode_inX_acceptY(add, new byte[]{0}, acceptErrors);
    }

    void input_format_encodeDecodeOne_ones(String add) throws IOException, EncoderException, ImageCapacityException, DamagedMessageException {
        input_format_encodeDecodeOne_ones(add, 0);
    }

    protected void input_format_encodeDecodeOne_ones(String add, int acceptErrors) throws IOException, EncoderException, ImageCapacityException, DamagedMessageException {
        input_format_encodeDecode_inX_acceptY(add, new byte[]{(byte)0xff}, acceptErrors);
    }

    // JPG none 8 divisible

    @Test
    void input_JPG_encodeDecodeNone() throws IOException, EncoderException, ImageCapacityException, DamagedMessageException {
        input_format_encodeDecodeNone(".jpg");
    }

    @Test
    void input_JPG_encodeDecodeOne_random() throws IOException, EncoderException, ImageCapacityException, DamagedMessageException {
        input_format_encodeDecodeOne_random(".jpg");
    }

    @Test
    void input_JPG_encodeDecodeOne_zeroes() throws IOException, EncoderException, ImageCapacityException, DamagedMessageException {
        input_format_encodeDecodeOne_zeroes(".jpg");
    }

    @Test
    void input_JPG_encodeDecodeOne_ones() throws IOException, EncoderException, ImageCapacityException, DamagedMessageException {
        input_format_encodeDecodeOne_ones(".jpg");
    }

    // JPG 8 divisible

    @Test
    void input_JPG8_encodeDecodeNone() throws IOException, EncoderException, ImageCapacityException, DamagedMessageException {
        input_format_encodeDecodeNone(eightDivisible + ".jpg");
    }

    @Test
    void input_JPG8_encodeDecodeOne_random() throws IOException, EncoderException, ImageCapacityException, DamagedMessageException {
        input_format_encodeDecodeOne_random(eightDivisible + ".jpg");
    }

    @Test
    void input_JPG8_encodeDecodeOne_zeroes() throws IOException, EncoderException, ImageCapacityException, DamagedMessageException {
        input_format_encodeDecodeOne_zeroes(eightDivisible + ".jpg");
    }

    @Test
    void input_JPG8_encodeDecodeOne_ones() throws IOException, EncoderException, ImageCapacityException, DamagedMessageException {
        input_format_encodeDecodeOne_ones(eightDivisible + ".jpg");
    }

    // PNG none 8 divisible

    @Test
    void input_PNG_encodeDecodeNone() throws IOException, EncoderException, ImageCapacityException, DamagedMessageException {
        input_format_encodeDecodeNone(".png");
    }

    @Test
    void input_PNG_encodeDecodeOne_random() throws IOException, EncoderException, ImageCapacityException, DamagedMessageException {
        input_format_encodeDecodeOne_random(".png");
    }

    @Test
    void input_PNG_encodeDecodeOne_zeroes() throws IOException, EncoderException, ImageCapacityException, DamagedMessageException {
        input_format_encodeDecodeOne_zeroes(".png");
    }

    @Test
    void input_PNG_encodeDecodeOne_ones() throws IOException, EncoderException, ImageCapacityException, DamagedMessageException {
        input_format_encodeDecodeOne_ones(".png");
    }

    // PNG 8 divisible

    @Test
    void input_PNG8_encodeDecodeNone() throws IOException, EncoderException, ImageCapacityException, DamagedMessageException {
        input_format_encodeDecodeNone(eightDivisible + ".png");
    }

    @Test
    void input_PNG8_encodeDecodeOne_random() throws IOException, EncoderException, ImageCapacityException, DamagedMessageException {
        input_format_encodeDecodeOne_random(eightDivisible + ".png");
    }

    @Test
    void input_PNG8_encodeDecodeOne_zeroes() throws IOException, EncoderException, ImageCapacityException, DamagedMessageException {
        input_format_encodeDecodeOne_zeroes(eightDivisible + ".png");
    }

    @Test
    void input_PNG8_encodeDecodeOne_ones() throws IOException, EncoderException, ImageCapacityException, DamagedMessageException {
        input_format_encodeDecodeOne_ones(eightDivisible + ".png");
    }

    // PNG transparent none 8 divisible

    @Test
    void input_PNGTP_encodeDecodeNone() throws IOException, EncoderException, ImageCapacityException, DamagedMessageException {
        input_format_encodeDecodeNone(transparent + ".png");
    }

    @Test
    void input_PNGTP_encodeDecodeOne_random() throws IOException, EncoderException, ImageCapacityException, DamagedMessageException {
        input_format_encodeDecodeOne_random(transparent + ".png");
    }

    @Test
    void input_PNGTP_encodeDecodeOne_zeroes() throws IOException, EncoderException, ImageCapacityException, DamagedMessageException {
        input_format_encodeDecodeOne_zeroes(transparent + ".png");
    }

    @Test
    void input_PNGTP_encodeDecodeOne_ones() throws IOException, EncoderException, ImageCapacityException, DamagedMessageException {
        input_format_encodeDecodeOne_ones(transparent + ".png");
    }

    // PNG transparent 8 divisible

    @Test
    void input_PNGTP8_encodeDecodeNone() throws IOException, EncoderException, ImageCapacityException, DamagedMessageException {
        input_format_encodeDecodeNone(eightDivisible + transparent + ".png");
    }

    @Test
    void input_PNGTP8_encodeDecodeOne_random() throws IOException, EncoderException, ImageCapacityException, DamagedMessageException {
        input_format_encodeDecodeOne_random(eightDivisible + transparent + ".png");
    }

    @Test
    void input_PNGTP8_encodeDecodeOne_zeroes() throws IOException, EncoderException, ImageCapacityException, DamagedMessageException {
        input_format_encodeDecodeOne_zeroes(eightDivisible + transparent + ".png");
    }

    @Test
    void input_PNGTP8_encodeDecodeOne_ones() throws IOException, EncoderException, ImageCapacityException, DamagedMessageException {
        input_format_encodeDecodeOne_ones(eightDivisible + transparent + ".png");
    }


    @Test
    void input_PNG_decodeCorrectLength() throws IOException, EncoderException, ImageCapacityException, DamagedMessageException {
        byte[] decoded = getEncoder(getBufferedImage(baseFilePath + baseFileName + ".png")).decode(50);
        Assertions.assertEquals(50, decoded.length);
    }

    @Test
    void input_JPG_decodeCorrectLength() throws IOException, EncoderException, ImageCapacityException, DamagedMessageException {
        byte[] decoded = getEncoder(getBufferedImage(baseFilePath + baseFileName + ".jpg")).decode(50);
        Assertions.assertEquals(50, decoded.length);
    }

    @Test
    void input_PNG_encodeTooMuch_expectThrow() {
        Assertions.assertThrows(ImageCapacityException.class, () ->
                getEncoder(getBufferedImage(baseFilePath + baseFileName + ".png"))
                        .encode(new byte[100000])
        );
    }

    @Test
    void input_JPG_encodeTooMuch_expectThrow() {
        Assertions.assertThrows(ImageCapacityException.class, () ->
                getEncoder(getBufferedImage(baseFilePath + baseFileName + ".jpg"))
                        .encode(new byte[100000])
        );
    }
}
