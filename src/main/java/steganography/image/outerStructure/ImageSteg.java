/*
 * Copyright (c) 2020
 * Contributed by Henk-Joas Lubig
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

import steganography.Steganography;
import steganography.exceptions.DamagedMessageException;
import steganography.exceptions.UnknownStegFormatException;
import steganography.exceptions.encoder.EncoderException;
import steganography.image.innerStructure.embedders.dct.dmas.DmasEmbedder;
import steganography.image.innerStructure.encoders.Encoder;
import steganography.image.innerStructure.encoders.stc.LossLessStcEncoder;
import steganography.image.exceptions.ImageCapacityException;
import steganography.image.exceptions.ImageWritingException;
import steganography.image.exceptions.NoImageException;
import steganography.image.exceptions.UnsupportedImageTypeException;
import steganography.image.innerStructure.encoders.wrappers.ReedSolomon;

import java.io.IOException;
import java.util.Arrays;

/**
 * Uses steganography to encode hidden messages ("payload") into images
 */
public class ImageSteg implements Steganography {

    public static final long DEFAULT_SEED = 1732341558;
    private static final int HEADER_SIGNATURE = 1349075561;
    private final boolean useDefaultHeader;
    private final boolean useErrorCorrection;
    private final Preset preset;

    /**
     * <p>Creates a new {@link #ImageSteg(boolean, boolean, Preset)}] with settings:</p>
     * <ul>
     *     <li>useDefaultHeader = true</li>
     *     <li>useErrorCorrection = true</li>
     *     <li>preset = {{@link Preset#RESISTANCE_HYBRID}}</li>
     * </ul>
     *
     * @see #ImageSteg(boolean, boolean, Preset)
     */
    public ImageSteg() {
        this(true, true, Preset.RESISTANCE_HYBRID);
    }

    /**
     * <p>Creates a new {@link #ImageSteg(boolean, boolean, Preset)}] with the settings:</p>
     * <ul>
     *     <li>useDefaultHeader = true</li>
     *     <li>useErrorCorrection = true</li>
     *     <li>preset = {@code preset}</li>
     * </ul>
     * @param preset Preset to use for encoding and decoding
     * @see #ImageSteg(boolean, boolean, Preset)
     */
    public ImageSteg(Preset preset) {
        this(true, true, preset);
    }

    /**
     * <p>Creates a new {@link #ImageSteg(boolean, boolean, Preset)}] with the settings:</p>
     * <ul>
     *     <li>useDefaultHeader = useDefaultHeader</li>
     *     <li>useErrorCorrection = true</li>
     *     <li>preset = {@code preset}</li>
     * </ul>
     *
     * @param useDefaultHeader true, if a header should be generated as prefix for the message
     * @param preset Preset to use for encoding and decoding
     * @see #ImageSteg(boolean, boolean, Preset)
     */
    public ImageSteg(boolean useDefaultHeader, Preset preset) {
        this(useDefaultHeader, true, preset);
    }

    /**
     * <p>Creates a new ImageSteg with the given settings.</p>
     * <b>useDefaultHeader</b>
     * <ul>
     *     <li>if true, the default header will be encoded in the image. The hidden message can then be
     *         decoded using ImageSteg.decode(...).
     *     </li>
     *     <li>
     *         if false, no header will be encoded in the image. The hidden message can only be decoded
     *         using ImageSteg.decode(length, ...)
     *     </li>
     * </ul>
     * <b>useErrorCorrection</b>
     * <ul>
     *     <li>
     *         if true, encoding will use error correction. This option only affects {@link Preset Presets} handling
     *         images with lossy compression, but regardless of image format
     *     </li>
     *     <li>
     *         if false, no error correction will be used, possibly resulting in decoding errors when using images with
     *         lossy compression
     *     </li>
     * </ul>
     *
     * @param useDefaultHeader true, if a header should be generated as prefix for the message
     * @param preset Preset to use for encoding and decoding
     * @param useErrorCorrection true, if error correction be used
     * @see #decode(byte[])
     * @see #decode(byte[], long)
     * @see #decode(int, byte[])
     * @see #decode(int, byte[], long)
     * @see ReedSolomon Error Correction
     */
    public ImageSteg(boolean useDefaultHeader, boolean useErrorCorrection, Preset preset) {
        this.useDefaultHeader = useDefaultHeader;
        this.useErrorCorrection = useErrorCorrection;
        this.preset = preset;
    }

    @Override
    public byte[] encode(byte[] carrier, byte[] payload)
            throws IOException, UnsupportedImageTypeException, NoImageException,
            ImageWritingException, ImageCapacityException, EncoderException {

        return encode(carrier, payload, DEFAULT_SEED);
    }

    @Override
    public byte[] encode(byte[] carrier, byte[] payload, long seed)
            throws IOException, NoImageException, UnsupportedImageTypeException,
            ImageWritingException, ImageCapacityException, EncoderException {

        if (carrier == null)
            throw new NullPointerException("Parameter 'carrier' must not be null");
        if (payload == null)
            throw new NullPointerException("Parameter 'payload' must not be null");

        ImageStegIO imageStegIO = new ImageStegIOJava(carrier, this.preset);

        Encoder encoder = imageStegIO.getEncoder(seed, this.useDefaultHeader, this.useErrorCorrection);

        if (this.useDefaultHeader) {
            byte[] header = new byte[8];
            System.arraycopy(int2bytes(HEADER_SIGNATURE), 0, header, 0, 4);
            System.arraycopy(int2bytes(payload.length), 0, header, 4, 4);
            encoder.encode(header);
        }
        encoder.encode(payload);

        return imageStegIO.getImageAsByteArray();
    }

    /**
     * <p>Decodes a hidden message in the given steganographicData (an image) and returns it as a byte array.</p>
     * <p>This method will fail, if the message was hidden without using the default header.
     * Use ImageSteg.decodeRaw() for this purpose.</p>
     * <p>Reasons for failing with an UnknownStegFormatExceptions are:</p>
     * <ul>
     *      <li>there is no hidden message</li>
     *      <li>the message was hidden with 'useDefaultHeader = false'</li>
     *      <li>the value for 'useTransparent' was different when hiding the message</li>
     *      <li>the message was hidden using an unknown algorithm</li>
     * </ul>
     * @param steganographicData Image containing the hidden message to decode
     * @return the hidden message as a byte array
     * @throws IOException if an error occurs during reading 'steganographicData'
     * @throws NoImageException if no image could be read from 'steganographicData'
     * @throws UnsupportedImageTypeException if the type of the given image is not supported
     * @throws UnknownStegFormatException if the default header could not be found
     * @see #decode(int, byte[])
     */
    @Override
    public byte[] decode(byte[] steganographicData)
            throws IOException, UnsupportedImageTypeException, NoImageException, UnknownStegFormatException,
            DamagedMessageException, EncoderException {

        return decode(steganographicData, DEFAULT_SEED);
    }

    /**
     * <p>Decodes a hidden message in the given steganographicData (an image) and returns it as a byte array.</p>
     * <p>This method will fail, if the message was hidden without using the default header.
     * Use ImageSteg.decodeRaw() for this purpose.</p>
     * <p>Reasons for failing with an UnknownStegFormatExceptions are:</p>
     * <ul>
     *      <li>there is no hidden message</li>
     *      <li>the message was hidden with 'useDefaultHeader = false'</li>
     *      <li>the value for 'useTransparent' was different when hiding the message</li>
     *      <li>the message was hidden using an unknown algorithm</li>
     * </ul>
     * @param steganographicData Image containing the hidden message to decode
     * @param seed seed that was used to encode the given stenographicData
     * @return the hidden message as a byte array
     * @throws IOException if an error occurs during reading 'steganographicData'
     * @throws NoImageException if no image could be read from 'steganographicData'
     * @throws UnsupportedImageTypeException if the type of the given image is not supported
     * @throws UnknownStegFormatException if the default header could not be found
     * @see #decode(int, byte[], long)
     */
    @Override
    public byte[] decode(byte[] steganographicData, long seed)
            throws IOException, NoImageException, UnsupportedImageTypeException, UnknownStegFormatException,
            DamagedMessageException, EncoderException {

        if (steganographicData == null)
            throw new NullPointerException("Parameter 'steganographicData' must not be null");

        ImageStegIO imageStegIO = new ImageStegIOJava(steganographicData, this.preset);

        Encoder encoder = imageStegIO.getEncoder(seed, true, this.useErrorCorrection);

        // decode 4 bytes and compare them to header signature
        try {
            byte[] header = encoder.decode(8);
            if (bytesToInt(Arrays.copyOfRange(header, 0, 4)) != HEADER_SIGNATURE)
                throw new UnknownStegFormatException("No steganographic encoding found.");

            // decode the next 4 bytes to get the amount of bytes to read
            int length = bytesToInt(Arrays.copyOfRange(header, 4, 8));

            return encoder.decode(length);
        } catch (ImageCapacityException e) {
            UnknownStegFormatException ex = new UnknownStegFormatException("Encoded message length has illegal value");
            ex.addSuppressed(e);
            throw ex;
        }
    }

    /**
     * <p>Decodes the amount of (length * 8) cover elements and returns the result as a byte array.</p>
     * <p>This method will not search for a header or validate the retrieved data in any form. If 'steganographicData'
     * contains a supported image, this method will always return a result. Whether this result is the hidden message,
     * depends on the settings used. The following configurations, provided to the constructor of this class,
     * need to be the same during encoding and decoding to return a valid hidden message:</p>
     *
     * <ul>
     *     <li>{@link #useDefaultHeader}</li>
     *     <li>{@link Preset}</li>
     * </ul>
     *
     * @param length Length (in bytes) of the hidden message
     * @param steganographicData Data containing data to extract
     * @return a byte array of length == "length" as a result of decoding (length * 8) pixels
     * @throws IOException if an error occurs during reading 'steganographicData'
     * @throws NoImageException if no image could be read from 'steganographicData'
     * @throws UnsupportedImageTypeException if the type of the given image is not supported
     * @throws DamagedMessageException if the message has too many errors to be decoded
     * @throws EncoderException if there was a mismatch between classes of the inner structure
     * @throws ImageCapacityException if the provided {@code length} exceeds the capacity
     */
    public byte[] decode(int length, byte[] steganographicData)
            throws IOException, NoImageException, UnsupportedImageTypeException, DamagedMessageException,
            EncoderException, ImageCapacityException {

        return decode(length, steganographicData, DEFAULT_SEED);
    }

    /**
     * <p>Decodes the amount of (length * 8) cover elements and returns the result as a byte array.</p>
     * <p>This method will not search for a header or validate the retrieved data in any form. If 'steganographicData'
     * contains a supported image, this method will always return a result. Whether this result is the hidden message,
     * depends on the settings used. The following configurations, provided to the constructor of this class,
     * need to be the same during encoding and decoding to return a valid hidden message:</p>
     *
     * <ul>
     *     <li>{@link #useDefaultHeader}</li>
     *     <li>{@link Preset}</li>
     *     <li>{@code seed}</li>
     * </ul>
     *
     * @param length Length (in bytes) of the hidden message
     * @param steganographicData Data containing data to extract
     * @param seed the same seed that was used while encoding the message
     * @return a byte array of length == "length" as a result of decoding (length * 8) pixels
     * @throws IOException if an error occurs during reading 'steganographicData'
     * @throws NoImageException if no image could be read from 'steganographicData'
     * @throws UnsupportedImageTypeException if the type of the given image is not supported
     * @throws DamagedMessageException if the message has too many errors to be decoded
     * @throws EncoderException if there was a mismatch between classes of the inner structure
     * @throws ImageCapacityException if the provided {@code length} exceeds the capacity
     */
    public byte[] decode(int length, byte[] steganographicData, long seed)
            throws IOException, NoImageException, UnsupportedImageTypeException, DamagedMessageException,
            EncoderException, ImageCapacityException {

        if (steganographicData == null)
            throw new NullPointerException("Parameter 'steganographicData' must not be null");

        ImageStegIO imageStegIO = new ImageStegIOJava(steganographicData, this.preset);

        Encoder encoder = imageStegIO.getEncoder(seed, false, this.useErrorCorrection);

        return encoder.decode(length);
    }

    @Override
    public boolean isSteganographicData(byte[] data)
            throws IOException, NoImageException, UnsupportedImageTypeException, DamagedMessageException, EncoderException {

        return isSteganographicData(data, DEFAULT_SEED);
    }

    @Override
    public boolean isSteganographicData(byte[] data, long seed)
            throws IOException, NoImageException, UnsupportedImageTypeException, DamagedMessageException, EncoderException {

        if (data == null)
            throw new NullPointerException("Parameter 'data' must not be null");

        Encoder encoder = new ImageStegIOJava(data, this.preset).getEncoder(seed, true, this.useErrorCorrection);

        try {
            return bytesToInt(encoder.decode(4)) == HEADER_SIGNATURE;
        } catch (ImageCapacityException e) {
            return false;
        }
    }

    /**
     * Returns the maximum number of bytes that can be encoded (as payload) in the given image with the provided
     * {@link Preset preset}. Analogous to {@link #isSteganographicData}, this method saves very little resources
     * compared to just trying to encode and catching the {@link ImageCapacityException}.
     * @param image image to potentially encode bytes in
     * @return the payload-capacity of image
     * @throws IOException if an error occurs during reading the image
     * @throws NoImageException if no image could be read from the image
     * @throws UnsupportedImageTypeException if the type of the given image is not supported
     * @see #ImageSteg(boolean, boolean, Preset)
     */
    public int getImageCapacity(byte[] image)
            throws IOException, NoImageException, UnsupportedImageTypeException {

        Encoder encoder = new ImageStegIOJava(image, this.preset)
                .getEncoder(DEFAULT_SEED, false, this.useErrorCorrection);

        int capacity = encoder.available() / 8;

        return this.useDefaultHeader ? (capacity - 8) : capacity;
    }

    /**
     * This enum provides Presets to the creation of Encoders to simplify this process for users.
     * @see #DETECTION_RESISTANCE
     * @see #COMPRESSION_RESISTANCE
     * @see #RESISTANCE_HYBRID
     * @see #MINIMAL_IMPACT
     */
    public enum Preset {
        /**
         * <p>Results in composited Encoder best suited for detection resistance of PNGs and BMPs. The output will
         * be the input format, which is not compression resistant.</p>
         * <p>It uses the {@link LossLessStcEncoder LossLessStcEncoder}, affecting only necessary Cover Elements.</p>
         * <p>The main use of this Preset is the output of images resisting steganalysis to a high degree.</p>
         */
        DETECTION_RESISTANCE,

        /**
         * <p>Results in composited Encoder best suited for compression resistance, while being less detection
         * resistant.</p>
         * <p>This Encoder will use no STCs, to reduce embedding impact, which also means no usage of a
         * DistortionFunction. The default target compression resistance QF 65.</p>
         */
        COMPRESSION_RESISTANCE,

        /**
         * <p>Results in composited Encoder trying to find a hybrid solution between compression and detection
         * resistance.</p>
         * <p>This Encoder will use STCs, mainly to take advantage of a DistortionFunction, but will affect every
         * Cover Element of the provided image, up to the Embedders maximum. This will ensure higher compression
         * resistance, while still maintaining reasonable detection resistance.</p>
         * <p>The Embedder used will be {@link DmasEmbedder DmasEmbedder}</p>
         */
        RESISTANCE_HYBRID,

        /**
         * <p>Results in composited Encoder only suited for Lossless formats (png and bmp).</p>
         * <p>This Preset uses the {@link LossLessStcEncoder LossLessStcEncoder} and the constant profile as
         * DistortionFunction to affect as little Cover Elements as possible.</p>
         * <p>While being the optimal solution for detection resistance, the output images are not at all
         * compression resistant and tend to be relatively big in disk space.</p>
         */
        MINIMAL_IMPACT,

        TEST
    }

    ////////////////////////////////////////////////////////////////////////////////////////////
    //                                       UTIL
    ////////////////////////////////////////////////////////////////////////////////////////////

    private byte[] int2bytes(int integer) {
        return new byte[] {
                (byte) ((integer >> 24) & 0xFF),
                (byte) ((integer >> 16) & 0xFF),
                (byte) ((integer >> 8) & 0xFF),
                (byte) (integer & 0xFF)
        };
    }

    private int bytesToInt(byte[] b) {
        return   b[3] & 0xFF |
                (b[2] & 0xFF) << 8 |
                (b[1] & 0xFF) << 16 |
                (b[0] & 0xFF) << 24;
    }
}
