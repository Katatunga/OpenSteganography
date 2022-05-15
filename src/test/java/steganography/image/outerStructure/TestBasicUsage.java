package steganography.image.outerStructure;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import steganography.Steganography;
import steganography.exceptions.*;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.BitSet;
import java.util.Random;

public class TestBasicUsage {

    private static final String PS = File.separator;
    protected final String baseFilePath = String.join(
            PS, "src", "test", "resources", "steganography", "image") + PS;

    //included images
    protected final String baum = "baum";       // contains a lot of white pixels
    protected final String baumTP = "baum_TP";  // contains a lot of transparent pixels
    protected final String rosehip = "rosehip"; // photo of a rosehip

    // formats
    protected final String png = ".png";
    protected final String jpeg = ".jpg";

    private final Random random;

    public TestBasicUsage() {
        this.random = new Random(0);
    }

    /////////////////////////////////////////////////////////////////////////////////////
    //                                 UTILITY
    /////////////////////////////////////////////////////////////////////////////////////

    /**
     * reads all bytes of a file and returns it as a byte-array
     * @param path path of the file to read in the {@link #baseFilePath}
     * @return byte-array containing the files contents
     * @throws IOException if {@link Files#readAllBytes(Path)} caused an error
     */
    protected byte[] readFile(String path) throws IOException {
        return Files.readAllBytes(new File(baseFilePath + path).toPath());
    }

    protected byte[] getRandomBytes(int length) {
        byte[] r = new byte[length];
        this.random.nextBytes(r);
        return r;
    }

    protected int countErrors(byte[] input, byte[] output) {
        BitSet inputB = BitSet.valueOf(input);
        inputB.xor(BitSet.valueOf(output));
        return inputB.cardinality();
    }

    /////////////////////////////////////////////////////////////////////////////////////
    //                                  TESTS
    /////////////////////////////////////////////////////////////////////////////////////

    /**
     * Shows the most basic usage. Watch out for proper error handling.
     * @throws SteganographyException - way too broad, look into JavaDoc of methods
     * @throws IOException - only thrown in this example if {@link #readFile(String)} has an error
     */
    @Test
    void basicFacadeNoArguments() throws SteganographyException, IOException {
        // get image as a byte array
        byte[] imageBytes = readFile(rosehip + jpeg);
        // get payload as a byte array
        byte[] payloadBytes = "Hello World".getBytes();

        // create an ImageSteg Object (arguments optional)
        Steganography steganography = new ImageSteg();

        // encode the payload into the image.
        byte[] stegoImageBytes = steganography.encode(imageBytes, payloadBytes);

        // stegoImageBytes is a complete image file stored in a byte array and could be stored on disk like as follows:
        // new FileOutputStream("path/to/imageOut.jpg").write(stegoImageBytes);

        // decode with the same ImageSteg
        byte[] decodedSame = steganography.decode(stegoImageBytes);

        // or use another instance
        byte[] decodedOther = new ImageSteg().decode(stegoImageBytes);

        Assertions.assertArrayEquals(decodedSame, decodedOther);
        Assertions.assertEquals(new String(decodedSame), "Hello World");
    }

    /**
     * Shows usage of other presets. Watch out for proper error handling.
     * @throws SteganographyException - way too broad, look into JavaDoc of methods
     * @throws IOException - only thrown in this example if {@link #readFile(String)} has an error
     */
    @Test
    void basicFacadeWithPreset() throws SteganographyException, IOException {
        // get image as a byte array
        byte[] imageBytes = readFile(rosehip + png);
        // get payload as a byte array
        byte[] payloadBytes = "Hello World".getBytes();

        // create an ImageSteg Object that uses Preset MINIMAL_IMPACT
        Steganography steganography = new ImageSteg(ImageSteg.Preset.MINIMAL_IMPACT);
        // encode the payload into the image
        byte[] stegoImageBytes = steganography.encode(imageBytes, payloadBytes);

        // decode with the same or another instance of ImageSteg
        byte[] decodedSamePreset = new ImageSteg(ImageSteg.Preset.MINIMAL_IMPACT).decode(stegoImageBytes);

        // decoding with other Presets will not work
        Assertions.assertThrows(DamagedMessageException.class, () ->
                        new ImageSteg(ImageSteg.Preset.DETECTION_RESISTANCE).decode(stegoImageBytes));

        Assertions.assertEquals(new String(decodedSamePreset), "Hello World");

    }

    /**
     * Shows usage without header, which has some advantages. Watch out for proper error handling.
     * @throws SteganographyException - way too broad, look into JavaDoc of methods
     * @throws IOException - only thrown in this example if {@link #readFile(String)} has an error
     */
    @Test
    void basicImageStegWithOutHeader() throws SteganographyException, IOException {
        // get image as a byte array
        byte[] imageBytes = readFile(rosehip + png);
        // get payload as a byte array
        byte[] payloadBytes = "Hello World".getBytes();

        // create an ImageSteg Object that uses Preset DETECTION_RESISTANCE and does NOT encode a header
        Steganography steganography = new ImageSteg(false, ImageSteg.Preset.DETECTION_RESISTANCE);
        // encode the payload into the image
        byte[] stegoImageBytes = steganography.encode(imageBytes, payloadBytes);

        // decoding with normal method will not work
        Assertions.assertThrows(DamagedMessageException.class, () ->
                new ImageSteg(ImageSteg.Preset.DETECTION_RESISTANCE).decode(stegoImageBytes));

        // decode with the same or another instance of ImageSteg by providing the length of the message
        byte[] decodedSamePreset = new ImageSteg(ImageSteg.Preset.DETECTION_RESISTANCE)
                        .decode(payloadBytes.length, stegoImageBytes);

        Assertions.assertEquals(new String(decodedSamePreset), "Hello World");
    }

    /**
     * Shows usage without header and error correction, resulting in some Errors using most Presets.
     * Watch out for proper error handling.
     * @throws SteganographyException - way too broad, look into JavaDoc of methods
     * @throws IOException - only thrown in this example if {@link #readFile(String)} has an error
     */
    @Test
    void basicImageStegWithOutHeaderAndErrorCorrection() throws SteganographyException, IOException {
        // get image as a byte array
        byte[] imageBytes = readFile(rosehip + png);
        // get payload as a byte array
        byte[] payloadBytes = "Hello World".getBytes();

        // create an ImageSteg Object that uses Preset DETECTION_RESISTANCE and does NOT encode a header
        Steganography steganography = new ImageSteg(false, false, ImageSteg.Preset.DETECTION_RESISTANCE);
        // encode the payload into the image
        byte[] stegoImageBytes = steganography.encode(imageBytes, payloadBytes);

        // decoding with normal method will not work
        Assertions.assertThrows(UnknownStegFormatException.class, () ->
                new ImageSteg(false, false, ImageSteg.Preset.DETECTION_RESISTANCE).decode(stegoImageBytes));

        // decode with the same or another instance of ImageSteg by providing the length of the message
        byte[] decodedSamePreset = new ImageSteg(false, false, ImageSteg.Preset.DETECTION_RESISTANCE)
                        .decode(payloadBytes.length, stegoImageBytes);

        Assertions.assertEquals(new String(decodedSamePreset), "Hello World");
    }
}
