/*
 * Copyright (c) 2020
 * Contributed by Henk Lubig
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package steganography.image.outerStructure;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.opentest4j.AssertionFailedError;
import steganography.exceptions.*;
import steganography.image.exceptions.NoImageException;
import steganography.image.exceptions.UnsupportedImageTypeException;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.file.Files;
import java.util.BitSet;
import java.util.Random;

public class TestOuterStructure {
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

    public TestOuterStructure() {
        this.random = new Random(0);
    }

    /////////////////////////////////////////////////////////////////////////////////////
    //                                 UTILITY
    /////////////////////////////////////////////////////////////////////////////////////

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

    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //                                          EN-DECODING
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////


    // BASE
    // ------------------------------------

    void base_given_IMAGE_when_PRESET_HEADER_expect_success(
            ImageSteg.Preset preset, int payloadLength, String imgPath, boolean useHeader)
            throws SteganographyException, IOException {

        byte[] img = readFile(imgPath);
        byte[] payload = getRandomBytes(payloadLength);

        /////// ENCODE
        ImageSteg imageSteg = new ImageSteg(useHeader, false, preset);

        byte[] imageIntermediate;
        imageIntermediate = imageSteg.encode(img, payload);

        ////// DECODE
        imageSteg = new ImageSteg(useHeader, false, preset);

        byte[] result;
        if (useHeader)
            result = imageSteg.decode(imageIntermediate);
        else {
            result = imageSteg.decode(payloadLength, imageIntermediate);
        }

        BitSet resBitSet = BitSet.valueOf(result);
        resBitSet.xor(BitSet.valueOf(payload));

        Assertions.assertArrayEquals(payload, result, "Amount of errors:" + resBitSet.cardinality());
    }

    void base_given_someImage_when_PRESET_withHeader_expect_success(ImageSteg.Preset preset, int payloadLength, String imgPath)
            throws SteganographyException, IOException {
        base_given_IMAGE_when_PRESET_HEADER_expect_success(preset, payloadLength, imgPath, true);
    }

    void base_given_someImage_when_PRESET_noHeader_expect_success(ImageSteg.Preset preset, int payloadLength, String imgPath)
            throws SteganographyException, IOException {
        base_given_IMAGE_when_PRESET_HEADER_expect_success(preset, payloadLength, imgPath, false);
    }

    void base_given_BigPngNoTransparencyNoSeed_when_PRESET_withHeader_expect_success(ImageSteg.Preset preset, int payloadLength)
            throws SteganographyException, IOException {
        base_given_someImage_when_PRESET_withHeader_expect_success(preset, payloadLength, rosehip + png);
    }


    // PNG No Transparency With Header
    // ------------------------------------

    @Test
    void given_PNGNoTransparency_when_MINIMAL_IMPACT_withHeader_expect_success()
            throws SteganographyException, IOException {
        base_given_BigPngNoTransparencyNoSeed_when_PRESET_withHeader_expect_success(ImageSteg.Preset.MINIMAL_IMPACT, 1000);
    }

    @Test
    void given_PNGNoTransparency_when_RESISTANCE_HYBRID_withHeader_expect_UnsupportedImage() {
        Assertions.assertThrows(UnsupportedImageTypeException.class, () ->
                base_given_BigPngNoTransparencyNoSeed_when_PRESET_withHeader_expect_success(
                        ImageSteg.Preset.RESISTANCE_HYBRID, 0)
        );
    }

    @Test
    void given_PNGNoTransparency_when_DETECTION_RESISTANCE_withHeader_expect_fail() {
        // This specific setup fails from this payloadLength on, see test above
        Assertions.assertThrows(AssertionFailedError.class, () ->
                base_given_BigPngNoTransparencyNoSeed_when_PRESET_withHeader_expect_success(
                        ImageSteg.Preset.DETECTION_RESISTANCE, 670));
    }

    @Test
    void given_PNGNoTransparency_when_COMPRESSION_RESISTANCE_withHeader_expect_UnsupportedImage() {
        Assertions.assertThrows(UnsupportedImageTypeException.class, () ->
                base_given_BigPngNoTransparencyNoSeed_when_PRESET_withHeader_expect_success(
                        ImageSteg.Preset.COMPRESSION_RESISTANCE, 0)
        );
    }

    // PNG No Transparency No Header
    // ------------------------------------

    void base_given_BigPngNoTransparencyNoSeed_when_PRESET_noHeader_expect_success(ImageSteg.Preset preset, int payloadLength)
            throws SteganographyException, IOException {
        base_given_someImage_when_PRESET_noHeader_expect_success(preset, payloadLength, rosehip + png);
    }

    @Test
    void given_PNGNoTransparency_when_MINIMAL_IMPACT_noHeader_expect_success()
            throws SteganographyException, IOException {
        base_given_BigPngNoTransparencyNoSeed_when_PRESET_noHeader_expect_success(ImageSteg.Preset.MINIMAL_IMPACT, 1000);
    }


    // PNG With Transparency No Header
    // ------------------------------------

    void base_given_PngWithTransparencyNoSeed_when_PRESET_noHeader_expect_success(ImageSteg.Preset preset, int payloadLength)
            throws SteganographyException, IOException {
        base_given_someImage_when_PRESET_noHeader_expect_success(preset, payloadLength, baumTP + png);
    }

    @Test
    void given_PngWithTransparency_when_MINIMAL_IMPACT_noHeader_expect_success()
            throws SteganographyException, IOException {
        base_given_PngWithTransparencyNoSeed_when_PRESET_noHeader_expect_success(ImageSteg.Preset.MINIMAL_IMPACT, 1000);
    }

    @Test
    void given_PngWithTransparency_when_DETECTION_RESISTANCE_noHeader_expect_success()
            throws SteganographyException, IOException {
        base_given_PngWithTransparencyNoSeed_when_PRESET_noHeader_expect_success(
                ImageSteg.Preset.DETECTION_RESISTANCE, 121);
    }

    // PNG With erased Transparency No Header - Payload length matters
    // ------------------------------------

    void base_given_PngNoTransparencyNoSeed_when_PRESET_noHeader_expect_success(ImageSteg.Preset preset, int payloadLength)
            throws SteganographyException, IOException {
        base_given_someImage_when_PRESET_noHeader_expect_success(preset, payloadLength, baum + png);
    }

    /**
     * This one fails, because it needs to encode in pure white blocks
     */
    @Test
    void given_PngNoTransparency_when_BigPayload_DETECTION_RESISTANCE_noHeader_expect_failure() {
        Assertions.assertThrows(AssertionFailedError.class, () ->
                base_given_PngNoTransparencyNoSeed_when_PRESET_noHeader_expect_success(
                        ImageSteg.Preset.DETECTION_RESISTANCE, 215)
        );
    }

    /**
     * This one succeeds, because it will only implement in non-white blocks
     */
    @Test
    void given_PngNoTransparency_when_smallPayload_DETECTION_RESISTANCE_noHeader_expect_success()
            throws SteganographyException, IOException {
        base_given_PngNoTransparencyNoSeed_when_PRESET_noHeader_expect_success(
                ImageSteg.Preset.DETECTION_RESISTANCE, 214);
    }

    @Test
    void given_PNGNoTransparency_when_DETECTION_RESISTANCE_withHeader_expect_success()
            throws SteganographyException, IOException {


        byte[] imageInput = Files.readAllBytes(new File(baseFilePath + rosehip + png).toPath());
        ImageSteg imageSteg = new ImageSteg(true, false, ImageSteg.Preset.DETECTION_RESISTANCE);

        int capacity = imageSteg.getImageCapacity(imageInput);
        byte[] payload = getRandomBytes(capacity);
        byte[] imageIntermediate = imageSteg.encode(imageInput, payload);

        byte[] decoded = imageSteg.decode(imageIntermediate);
        int errors = countErrors(payload, decoded);


        Assertions.assertTrue(capacity * 8 * 0.01 > errors,
                String.format("Errors more than 1 percent: capacity: %d; Errors: %d", capacity, errors));
    }

    // DR Erased Transparency Full Capacity -> High error rate
    @Test
    void given_PNGErasedTransparency_FullCapacity_DETECTION_RESISTANCE_when_encodingAndDecodingNoHeader_expect_Lt50PercentErrors()
            throws SteganographyException, IOException {


        byte[] imageInput = Files.readAllBytes(new File(baseFilePath + baum + png).toPath());
        ImageSteg imageSteg = new ImageSteg(false, false, ImageSteg.Preset.DETECTION_RESISTANCE);

        int capacity = imageSteg.getImageCapacity(imageInput);
        byte[] payload = getRandomBytes(capacity);
        byte[] imageIntermediate = imageSteg.encode(imageInput, payload, 3);

        byte[] decoded = imageSteg.decode(payload.length, imageIntermediate, 3);
        int errors = countErrors(payload, decoded);


        Assertions.assertTrue(capacity * 8 * 0.06 > errors,
                String.format("Errors more than 6 percent: capacity: %d; Errors: %d", capacity, errors));
    }

    // DR Erased Transparency
    @Test
    void given_PNGErasedTransparency_HalfCapacity_DETECTION_RESISTANCE_when_encodingAndDecodingNoHeader_expect_NoErrors()
            throws SteganographyException, IOException {


        byte[] imageInput = Files.readAllBytes(new File(baseFilePath + baum + png).toPath());
        ImageSteg imageSteg = new ImageSteg(false, false, ImageSteg.Preset.DETECTION_RESISTANCE);

        int capacity = imageSteg.getImageCapacity(imageInput) / 2;
        byte[] payload = getRandomBytes(capacity);
        byte[] imageIntermediate = imageSteg.encode(imageInput, payload, 3);

        byte[] decoded = imageSteg.decode(payload.length, imageIntermediate, 3);
        int errors = countErrors(payload, decoded);


        Assertions.assertEquals(0, errors, String.format("More than 1 error: capacity: %d; Errors: %d", capacity, errors));
    }

    // PNG With Transparency - No Transparency used
    // --------------------------------------------

    @Test
    void given_PNGWithTransparency_MINIMAL_IMPACT_when_encodingAndDecoding_expect_noTransparencyChanged()
            throws SteganographyException, IOException {


        byte[] imageInput = Files.readAllBytes(new File(baseFilePath + baumTP + png).toPath());
        ImageSteg imageSteg = new ImageSteg(false, false, ImageSteg.Preset.MINIMAL_IMPACT);

        int capacity = imageSteg.getImageCapacity(imageInput);
        byte[] imageIntermediate = imageSteg.encode(imageInput, getRandomBytes(capacity));

        Assertions.assertFalse(findChangedTransparentPixels(imageInput, imageIntermediate));
    }

    @Test
    void given_PNGWithTransparency_DETECTION_RESISTANCE_when_encodingAndDecoding_expect_noTransparencyChanged()
            throws SteganographyException, IOException {

        byte[] imageInput = Files.readAllBytes(new File(baseFilePath + baumTP + png).toPath());
        ImageSteg imageSteg = new ImageSteg(false, false, ImageSteg.Preset.DETECTION_RESISTANCE);

        int capacity = imageSteg.getImageCapacity(imageInput);
        byte[] imageIntermediate = imageSteg.encode(imageInput, getRandomBytes(capacity));

        Assertions.assertFalse(findChangedTransparentPixels(imageInput, imageIntermediate));
    }

    /**
     * Returns true, when there is at least one transparent pixel in {@code before} which has different
     * values in {@code after}.
     */
    private boolean findChangedTransparentPixels(byte[] imageBefore, byte[] imageAfter) throws IOException {
        BufferedImage imgBefore = ImageIO.read(new ByteArrayInputStream(imageBefore));
        BufferedImage imgAfter = ImageIO.read(new ByteArrayInputStream(imageAfter));
        for (int y = 0; y < imgBefore.getHeight(); y++) {
            for (int x = 0; x < imgBefore.getWidth(); x++) {
                if (
                        ((imgBefore.getRGB(x, y) >> 24) & 0xff) == 0 && // pixel is transparent
                        imgBefore.getRGB(x, y) != imgAfter.getRGB(x, y) // pixel is not equal before and after
                ) {
                    return true;
                }
            }
        }
        return false;
    }


    // Use header no error correction - every preset
    // --------------------------------------------

    // MP Erased Transparency
    @Test
    void given_PNGErasedTransparency_MINIMAL_IMPACT_when_encodingAndDecodingWithHeader_expect_success()
            throws SteganographyException, IOException {


        byte[] imageInput = Files.readAllBytes(new File(baseFilePath + baum + png).toPath());
        ImageSteg imageSteg = new ImageSteg(true, false, ImageSteg.Preset.MINIMAL_IMPACT);

        int capacity = imageSteg.getImageCapacity(imageInput);
        byte[] payload = getRandomBytes(capacity);
        byte[] imageIntermediate = imageSteg.encode(imageInput, payload);

        byte[] decoded = imageSteg.decode(imageIntermediate);

        Assertions.assertArrayEquals(payload, decoded);
    }

    // MP With Transparency
    @Test
    void given_PNGWithTransparency_MINIMAL_IMPACT_when_encodingAndDecodingWithHeader_expect_success()
            throws SteganographyException, IOException {


        byte[] imageInput = Files.readAllBytes(new File(baseFilePath + baumTP + png).toPath());
        ImageSteg imageSteg = new ImageSteg(true, false, ImageSteg.Preset.MINIMAL_IMPACT);

        int capacity = imageSteg.getImageCapacity(imageInput);
        byte[] payload = getRandomBytes(capacity);
        byte[] imageIntermediate = imageSteg.encode(imageInput, payload);

        byte[] decoded = imageSteg.decode(imageIntermediate);

        Assertions.assertArrayEquals(payload, decoded);
    }

    // MP No Transparency
    @Test
    void given_PNGNoTransparency_MINIMAL_IMPACT_when_encodingAndDecodingWithHeader_expect_success()
            throws SteganographyException, IOException {


        byte[] imageInput = Files.readAllBytes(new File(baseFilePath + rosehip + png).toPath());
        ImageSteg imageSteg = new ImageSteg(true, false, ImageSteg.Preset.MINIMAL_IMPACT);

        int capacity = imageSteg.getImageCapacity(imageInput);
        byte[] payload = getRandomBytes(capacity);
        byte[] imageIntermediate = imageSteg.encode(imageInput, payload);

        byte[] decoded = imageSteg.decode(imageIntermediate);

        Assertions.assertArrayEquals(payload, decoded);
    }

    // DR Erased Transparency Full Capacity -> fails if single colored (white) blocks are used
    @Test
    void given_PNGErasedTransparency_FullCapacity_DETECTION_RESISTANCE_when_enDecodingWithHeader_expect_conditionalFailure()
            throws SteganographyException, IOException {
        boolean whiteBlocksUsed = true;

        byte[] imageInput = Files.readAllBytes(new File(baseFilePath + baum + png).toPath());
        ImageSteg imageSteg = new ImageSteg(true, false, ImageSteg.Preset.DETECTION_RESISTANCE);

        int capacity = imageSteg.getImageCapacity(imageInput);
        byte[] payload = getRandomBytes(capacity);
        byte[] imageIntermediate = imageSteg.encode(imageInput, payload);

        // if white blocks where used, the header should be unrecognizable with default seed
        if (whiteBlocksUsed) {
            Assertions.assertThrows(UnknownStegFormatException.class, () -> imageSteg.decode(imageIntermediate, 3));
            return;
        }

        byte[] decoded = imageSteg.decode(imageIntermediate);
        int errors = countErrors(payload, decoded);

        // if no white blocks where used, no errors should occur
        Assertions.assertEquals(0, errors,
                    String.format("Errors occurred: capacity: %d; Errors: %d", capacity, errors));
    }

    // DR With Transparency
    @Test
    void given_PNGWithTransparency_DETECTION_RESISTANCE_when_encodingAndDecodingWithHeader_expect_success()
            throws SteganographyException, IOException {


        byte[] imageInput = Files.readAllBytes(new File(baseFilePath + baumTP + png).toPath());
        ImageSteg imageSteg = new ImageSteg(true, false, ImageSteg.Preset.DETECTION_RESISTANCE);

        int capacity = imageSteg.getImageCapacity(imageInput);
        byte[] payload = getRandomBytes(capacity);
        byte[] imageIntermediate = imageSteg.encode(imageInput, payload);

        byte[] decoded = imageSteg.decode(imageIntermediate);
        int errors = countErrors(payload, decoded);


        Assertions.assertEquals(0, errors,
                String.format("Errors occurred: capacity: %d; Errors: %d", capacity, errors));
    }

    // DR No Transparency
    @Test
    void given_PNGNoTransparency_DETECTION_RESISTANCE_when_encodingAndDecodingWithHeader_expect_Lt1PercentErrors()
            throws SteganographyException, IOException {


        byte[] imageInput = Files.readAllBytes(new File(baseFilePath + rosehip + png).toPath());
        ImageSteg imageSteg = new ImageSteg(true, false, ImageSteg.Preset.DETECTION_RESISTANCE);

        int capacity = imageSteg.getImageCapacity(imageInput);
        byte[] payload = getRandomBytes(capacity);
        byte[] imageIntermediate = imageSteg.encode(imageInput, payload);

        byte[] decoded = imageSteg.decode(imageIntermediate);
        int errors = countErrors(payload, decoded);


        Assertions.assertTrue(capacity * 8 * 0.01 > errors,
                String.format("Errors more than 1 percent: capacity: %d; Errors: %d", capacity, errors));
    }

    // RH mostly white image
    @Test
    void given_JPGMostlyWhite_RESISTANCE_HYBRID_when_encodingAndDecodingWithHeader_expect_Lt1PercentErrors()
            throws SteganographyException, IOException {


        byte[] imageInput = Files.readAllBytes(new File(baseFilePath + baum + jpeg).toPath());
        ImageSteg imageSteg = new ImageSteg(true, false, ImageSteg.Preset.RESISTANCE_HYBRID);

        int capacity = imageSteg.getImageCapacity(imageInput);
        byte[] payload = getRandomBytes(capacity);
        byte[] imageIntermediate = imageSteg.encode(imageInput, payload);

        byte[] decoded = imageSteg.decode(imageIntermediate);
        int errors = countErrors(payload, decoded);


        Assertions.assertTrue(capacity * 8 * 0.01 > errors,
                String.format("Errors more than 0.4 percent: capacity: %d; Errors: %d", capacity, errors));
    }

    // RH normal image
    @Test
    void given_JPG_RESISTANCE_HYBRID_when_encodingAndDecodingWithHeader_expect_Lt1PercentErrors()
            throws SteganographyException, IOException {


        byte[] imageInput = Files.readAllBytes(new File(baseFilePath + rosehip + jpeg).toPath());
        ImageSteg imageSteg = new ImageSteg(true, false, ImageSteg.Preset.RESISTANCE_HYBRID);

        int capacity = imageSteg.getImageCapacity(imageInput);
        byte[] payload = getRandomBytes(capacity);
        byte[] imageIntermediate = imageSteg.encode(imageInput, payload);

        byte[] decoded = imageSteg.decode(imageIntermediate);
        int errors = countErrors(payload, decoded);


        Assertions.assertEquals(0, errors,
                String.format("Errors occurred: capacity: %d; Errors: %d", capacity, errors));
    }

    // CR mostly white iamge
    @Test
    void given_JPGMostlyWhite_COMPRESSION_RESISTANCE_when_encodingAndDecodingWithHeader_expect_Lt1PercentErrors()
            throws SteganographyException, IOException {


        byte[] imageInput = Files.readAllBytes(new File(baseFilePath + baum + jpeg).toPath());
        ImageSteg imageSteg = new ImageSteg(true, false, ImageSteg.Preset.COMPRESSION_RESISTANCE);

        int capacity = imageSteg.getImageCapacity(imageInput);
        byte[] payload = getRandomBytes(capacity);
        byte[] imageIntermediate = imageSteg.encode(imageInput, payload);

        byte[] decoded = imageSteg.decode(imageIntermediate);
        int errors = countErrors(payload, decoded);


        Assertions.assertTrue(capacity * 8 * 0.01 > errors,
                String.format("Errors more than 1 percent: capacity: %d; Errors: %d", capacity, errors));
    }

    // CR normal image
    @Test
    void given_JPG_COMPRESSION_RESISTANCE_when_encodingAndDecodingWithHeader_expect_Lt1PercentErrors()
            throws SteganographyException, IOException {


        byte[] imageInput = Files.readAllBytes(new File(baseFilePath + rosehip + jpeg).toPath());
        ImageSteg imageSteg = new ImageSteg(true, false, ImageSteg.Preset.COMPRESSION_RESISTANCE);

        int capacity = imageSteg.getImageCapacity(imageInput);
        byte[] payload = getRandomBytes(capacity);
        byte[] imageIntermediate = imageSteg.encode(imageInput, payload);

        byte[] decoded = imageSteg.decode(imageIntermediate);
        int errors = countErrors(payload, decoded);


        Assertions.assertTrue(capacity * 8 * 0.01 > errors,
                String.format("Errors more than 1 percent: capacity: %d; Errors: %d", capacity, errors));
    }


    // UNSUPPORTED_FORMATS
    // ------------------------------------

    @Test
    void given_TIFF_when_encoding_expect_UnsupportedImageTypeException() throws IOException {

        BufferedImage bufferedImage = new BufferedImage(1, 1, BufferedImage.TYPE_3BYTE_BGR);

        String loremIpsum = "Hello World";
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(bufferedImage, "tiff", baos);

        Assertions.assertThrows(
                UnsupportedImageTypeException.class,
                () -> new ImageSteg().encode(baos.toByteArray(), loremIpsum.getBytes())
        );

    }

    @Test
    void given_TIFF_when_decoding_expect_UnsupportedImageTypeException() throws IOException {

        BufferedImage bufferedImage = new BufferedImage(1, 1, BufferedImage.TYPE_3BYTE_BGR);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(bufferedImage, "tiff", baos);

        Assertions.assertThrows(
                UnsupportedImageTypeException.class,
                () -> new ImageSteg().decode(baos.toByteArray())
        );

    }

    @Test
    void given_WBMP_when_encoding_expect_UnsupportedImageTypeException() throws IOException {

        BufferedImage bufferedImage = new BufferedImage(1, 1, BufferedImage.TYPE_BYTE_BINARY);

        String loremIpsum = "Hello World";
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(bufferedImage, "wbmp", baos);

        Assertions.assertThrows(
                UnsupportedImageTypeException.class,
                () -> new ImageSteg().encode(baos.toByteArray(), loremIpsum.getBytes())
        );


    }

    @Test
    void given_JPG_when_encoding_MINIMAL_IMPACT_expect_UnsupportedImageTypeException() throws IOException {

        BufferedImage bufferedImage = new BufferedImage(1, 1, BufferedImage.TYPE_BYTE_BINARY);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(bufferedImage, "jpg", baos);

        Assertions.assertThrows(
                UnsupportedImageTypeException.class,
                () -> new ImageSteg(false, false, ImageSteg.Preset.MINIMAL_IMPACT)
                        .encode(baos.toByteArray(), new byte[0])
        );
    }

    @Test
    void given_JPG_when_encodingNoHeader_DETECTION_RESISTANCE_expect_UnsupportedImageTypeException() throws IOException {

        BufferedImage bufferedImage = new BufferedImage(1, 1, BufferedImage.TYPE_BYTE_BINARY);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(bufferedImage, "jpg", baos);

        Assertions.assertThrows(
                UnsupportedImageTypeException.class,
                () -> new ImageSteg(false, false, ImageSteg.Preset.DETECTION_RESISTANCE)
                        .encode(baos.toByteArray(), new byte[0])
        );
    }

    @Test
    void given_PNG_when_encodingNoHeader_RESISTANCE_HYBRID_expect_UnsupportedImageTypeException() throws IOException {

        BufferedImage bufferedImage = new BufferedImage(1, 1, BufferedImage.TYPE_BYTE_BINARY);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(bufferedImage, "png", baos);

        Assertions.assertThrows(
                UnsupportedImageTypeException.class,
                () -> new ImageSteg(false, false, ImageSteg.Preset.RESISTANCE_HYBRID)
                        .encode(baos.toByteArray(), new byte[0])
        );
    }

    @Test
    void given_BMP_when_encodingNoHeader_RESISTANCE_HYBRID_expect_UnsupportedImageTypeException() throws IOException {

        BufferedImage bufferedImage = new BufferedImage(1, 1, BufferedImage.TYPE_BYTE_BINARY);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(bufferedImage, "bmp", baos);

        Assertions.assertThrows(
                UnsupportedImageTypeException.class,
                () -> new ImageSteg(false, false, ImageSteg.Preset.RESISTANCE_HYBRID)
                        .encode(baos.toByteArray(), new byte[0])
        );
    }

    @Test
    void given_PNG_when_encodingNoHeader_COMPRESSION_RESISTANCE_expect_UnsupportedImageTypeException() throws IOException {

        BufferedImage bufferedImage = new BufferedImage(1, 1, BufferedImage.TYPE_BYTE_BINARY);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(bufferedImage, "png", baos);

        Assertions.assertThrows(
                UnsupportedImageTypeException.class,
                () -> new ImageSteg(false, false, ImageSteg.Preset.COMPRESSION_RESISTANCE)
                        .encode(baos.toByteArray(), new byte[0])
        );
    }

    @Test
    void given_BMP_when_encodingNoHeader_COMPRESSION_RESISTANCE_expect_UnsupportedImageTypeException() throws IOException {

        BufferedImage bufferedImage = new BufferedImage(1, 1, BufferedImage.TYPE_BYTE_BINARY);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(bufferedImage, "bmp", baos);

        Assertions.assertThrows(
                UnsupportedImageTypeException.class,
                () -> new ImageSteg(false, false, ImageSteg.Preset.COMPRESSION_RESISTANCE)
                        .encode(baos.toByteArray(), new byte[0])
        );
    }


    // NULL_VALUES
    // ------------------------------------

    @Test
    void given_imageIsNull_when_encoding_expect_NullPointerException() {

        Assertions.assertThrows(
                NullPointerException.class,
                () -> new ImageSteg().encode(null, "Hello World".getBytes())
        );
    }

    @Test
    void given_imageIsEmpty_when_encoding_expect_NoImageException() {

        Assertions.assertThrows(
                NoImageException.class,
                () -> new ImageSteg().encode(new byte[0], "Hello World".getBytes())
        );
    }

    @Test
    void given_imageIsNull_when_decoding_expect_NullPointerException() {

        Assertions.assertThrows(
                NullPointerException.class,
                () -> new ImageSteg().decode(null)
        );
    }

    @Test
    void given_payloadIsNull_when_encoding_expect_NullPointerException() {

        String pathToImage = "src/test/resources/steganography/image/baum.bmp";

        Assertions.assertThrows(
                NullPointerException.class,
                () -> new ImageSteg().encode(Files.readAllBytes(new File(pathToImage).toPath()), null)
        );
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //                                          IMAGE CAPACITY
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////


    // SUCCESS
    // ------------------------------------

    @Test
    void given_2048PixelPNGNoDefaultHeader_MINIMAL_IMPACT_when_getCapacity_expect_128() throws IOException, SteganographyException {

        BufferedImage bufferedImage = new BufferedImage(40, 400, BufferedImage.TYPE_3BYTE_BGR);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(bufferedImage, "png", baos);

        Assertions.assertEquals(
                1000,
                new ImageSteg(false, false, ImageSteg.Preset.MINIMAL_IMPACT).getImageCapacity(
                        baos.toByteArray()
                )
        );

    }

    @Test
    void given_1024000PixelPNGNoDefaultHeader_DETECTION_RESISTANCE_when_getCapacity_expect_1000() throws IOException, SteganographyException {

        BufferedImage bufferedImage = new BufferedImage(3200, 320, BufferedImage.TYPE_3BYTE_BGR);

        // in case single color blocks are forbidden
        bufferedImage.setRGB(0, 0, 3200, 320, new Random().ints(3200*320).toArray(), 0, 64);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(bufferedImage, "png", baos);

        Assertions.assertEquals(
                1000,
                new ImageSteg(false, false, ImageSteg.Preset.DETECTION_RESISTANCE).getImageCapacity(
                        baos.toByteArray()
                )
        );

    }

    @Test
    void given_1024000PixelPNGNoDefaultHeader_RESISTANCE_HYBRID_when_getCapacity_expect_1000() throws IOException, SteganographyException {

        BufferedImage bufferedImage = new BufferedImage(320, 3200, BufferedImage.TYPE_3BYTE_BGR);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(bufferedImage, "jpg", baos);

        Assertions.assertEquals(
                1000,
                new ImageSteg(false, false, ImageSteg.Preset.RESISTANCE_HYBRID).getImageCapacity(
                        baos.toByteArray()
                )
        );

    }


    @Test
    void given_2048000PixelPNGNoDefaultHeader_COMPRESSION_RESISTANCE_when_getCapacity_expect_1000() throws IOException, SteganographyException {

        BufferedImage bufferedImage = new BufferedImage(640, 3200, BufferedImage.TYPE_3BYTE_BGR);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(bufferedImage, "jpg", baos);

        Assertions.assertEquals(
                1000,
                new ImageSteg(false, false, ImageSteg.Preset.COMPRESSION_RESISTANCE).getImageCapacity(
                        baos.toByteArray()
                )
        );

    }
}
