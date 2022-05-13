package steganography.image.outerStructure;

import steganography.image.innerStructure.distortion.DistortionFunction;
import steganography.image.innerStructure.distortion.JUniward;
import steganography.image.innerStructure.embedders.Embedder;
import steganography.image.innerStructure.embedders.dct.dcras.MarkingDcrasEmbedder;
import steganography.image.innerStructure.embedders.dct.dmas.DmasEmbedder;
import steganography.image.innerStructure.embedders.dct.dmas.MarkingEmbedder;
import steganography.image.innerStructure.embedders.spatial.PixelBit;
import steganography.image.innerStructure.embedders.dct.dcras.DcrasEmbedder;
import steganography.image.innerStructure.encoders.Encoder;
import steganography.image.innerStructure.encoders.GeneralEncoder;
import steganography.image.innerStructure.encoders.plain.MarkingPlainEncoder;
import steganography.image.innerStructure.encoders.plain.PlainEncoder;
import steganography.image.innerStructure.encoders.stc.LossLessStcEncoder;
import steganography.image.innerStructure.encoders.stc.StcEncoder;
import steganography.image.innerStructure.encoders.wrappers.ReedSolomon;
import steganography.image.exceptions.ImageWritingException;
import steganography.image.exceptions.NoImageException;
import steganography.image.exceptions.UnsupportedImageTypeException;
import steganography.image.innerStructure.overlays.BlockShuffleOverlay;
import steganography.image.innerStructure.overlays.PixelShuffleOverlay;
import steganography.image.innerStructure.overlays.abstracts.BuffImgOverlay;
import steganography.image.operation.pixelTranslation.Rgb2YCbCr;
import steganography.transforms.FastDct8;
import steganography.transforms.Wavelet;

import javax.imageio.*;
import javax.imageio.stream.ImageInputStream;
import javax.imageio.stream.ImageOutputStream;
import javax.imageio.stream.MemoryCacheImageInputStream;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.*;
import java.util.function.Predicate;

/**
 * This Class exists to handle reading and writing of BufferedImages to and from byte arrays
 * as well as choosing the appropriate encoders (and their overlays) for the given image. It holds on to the image
 * during its en- or decoding.
 */
public class ImageStegIOJava implements ImageStegIO{

    /**
     * This preset determines the process of creating an Encoder
     * @see ImageSteg.Preset
     */
    private final ImageSteg.Preset preset;

    /**
     * The quality factor used for JPEG images if JPEG is chosen as the output format
     */
    protected float quality_factor = .95f;

    /**
     * The given input. Remains unchanged throughout.
     */
    protected final byte[] input;

    /**
     * The BufferedImage to handle the In- and Output of
     */
    private BufferedImage bufferedImage;

    /**
     * The format of the image as recognized while reading input
     */
    private String format;

    /**
     * A set of supported formats to look up
     */
    protected static final Set<String> SUPPORTED_FORMATS = new HashSet<>(
            Arrays.asList("bmp", "png", "jpg", "jpeg")
    );

    /**
     * Reusable check for transparency in single pixels
     */
    public static final Predicate<Integer> isPixelOpaque = pixel ->
            (pixel >> 24 & 0xff) > 0;

    /**
     * Reusable check for transparency in blocks - not one pixel may be transparent
     */
    public static final Predicate<int[]> allPixelsOpaque = pixels -> {
        for (int pixel : pixels) {
            if (!isPixelOpaque.test(pixel))
                return false;
        }
        return true;
    };

    /**
     * Reusable check to have at least two colors in a block
     */
    public static final Predicate<int[]> noSingleColors = pixels -> {
        int firstColor = pixels[0];
        for (int pixel : pixels) {
            if (pixel != firstColor)
                return true;
        }
        return false;
    };

    /**
     * <p>Creates an object that exists to handle reading and writing of BufferedImages to and from byte arrays
     * as well as choosing the appropriate combination of {@link GeneralEncoder Encoder},
     * {@link Embedder Embedder},
     * {@link BuffImgOverlay Overlay} and possibly
     * {@link DistortionFunction DistortionFunction} for the given image. This will
     * be the default combination, tested to be a decent {@link ImageSteg.Preset#RESISTANCE_HYBRID hybrid solution}.</p>
     * <p>It holds on to the image during its en- or decoding.</p>
     * <p>The image will only be processed if the methods getFormat() or getEncoder() are called.</p>
     * @param image the image to handle In- and Output of
     */
    public ImageStegIOJava(byte[] image) {
        this(image, ImageSteg.Preset.RESISTANCE_HYBRID);
    }

    /**
     * <p>Creates an object that exists to handle reading and writing of BufferedImages to and from byte arrays
     * as well as choosing the appropriate combination of {@link GeneralEncoder Encoder},
     * {@link Embedder Embedder},
     * {@link BuffImgOverlay Overlay} and possibly
     * {@link DistortionFunction DistortionFunction} for the given image according to
     * the provided {@link ImageSteg.Preset}.
     * It holds on to the image during its en- or decoding.</p>
     * <p>The image will only be processed if the methods getFormat() or getEncoder() are called.</p>
     * @param image the image to handle In- and Output of
     * @param preset Preset to use for encoding and decoding
     */
    public ImageStegIOJava(byte[] image, ImageSteg.Preset preset) {
        this.input = image;
        this.preset = preset;
    }

    private void processImage(byte[] carrier)
            throws IOException, NoImageException, UnsupportedImageTypeException {

        try(ImageInputStream imageInputStream = new MemoryCacheImageInputStream(new ByteArrayInputStream(carrier))) {
            Iterator<ImageReader> readers = ImageIO.getImageReaders(imageInputStream);

            if (readers.hasNext()) {
                ImageReader reader = readers.next();

                String formatName = reader.getFormatName();

                try {
                    reader.setInput(imageInputStream);

                    BufferedImage buffImg = reader.read(0);

                    this.format = formatName;
                    this.bufferedImage = buffImg;

                    throwUnsupportedCombination();
                } finally {
                    reader.dispose();
                }
            } else {
                throw new NoImageException("No image could be read from input.");
            }
        }
    }

    /**
     * @throws UnsupportedImageTypeException if the combination of {@link #bufferedImage}, {@link #format}
     * and {@link #preset} is unsupported
     */
    private void throwUnsupportedCombination() throws UnsupportedImageTypeException {
        
        if (!SUPPORTED_FORMATS.contains(this.format.toLowerCase(Locale.ROOT)))
            throw new UnsupportedImageTypeException(
                    String.format("The Image format (%s) is not supported.", this.format)
            );

        // BMPs with transparency cause an exception when trying to write, so not supported
        // Could probably be solved by replacing this.format with "png", but is inconsistent
        if (this.format.equalsIgnoreCase("bmp") && this.bufferedImage.getColorModel().hasAlpha())
            throw new UnsupportedImageTypeException(
                    "Image format (bmp containing transparency) is not supported."
            );

        if (outputJpeg() && !formatIsJpeg())
            throw new UnsupportedImageTypeException(String.format(
                    "Preset %s only supports JPEG images",this.preset
            ));

        if (!outputJpeg() && formatIsJpeg())
            throw new UnsupportedImageTypeException(String.format(
                    "Preset %s does not support the use of JPEG", this.preset
            ));
    }

    private boolean formatIsJpeg() {
        return this.format.equalsIgnoreCase("jpg") || this.format.equalsIgnoreCase("jpeg");
    }

    /**
     * Determines and returns whether the output format will be jpeg
     */
    private boolean outputJpeg() {
        return  this.preset == ImageSteg.Preset.COMPRESSION_RESISTANCE
            ||  this.preset == ImageSteg.Preset.RESISTANCE_HYBRID;
    }

    /**
     * <p>Returns the image in its current state (Output-Image) as a byte Array.</p>
     * <p>If the image was not yet processed, return == input</p>
     * @return the image in its current state as a byte array
     * @throws IOException if there was an error during writing of BufferedImage to a byte array
     * @throws ImageWritingException if the image was not written to a byte array for unknown reasons
     */
    @Override
    public byte[] getImageAsByteArray() throws IOException, ImageWritingException {
        if (this.bufferedImage == null)
            return input;

        ByteArrayOutputStream resultImage = new ByteArrayOutputStream();

        // If format is JPEG or output needs to be, return JPEG with decent QF
        if (outputJpeg() || formatIsJpeg()) {
            writeJPG(this.bufferedImage, this.quality_factor, resultImage);
        }
        // else, return in-format
        else if (!ImageIO.write(this.bufferedImage, this.format, resultImage)) {
            throw new ImageWritingException("Could not write image. Unknown, internal error");
        }

        return resultImage.toByteArray();
    }

    /**
     * Writes the provided {@code bufferedImage} as a JPEG image to provided {@code os}, using the provided
     * {@code quality} as quality factor
     * @param buffImg image to write
     * @param quality quality factor to use
     * @param os OutputStream to write the JPEG image to
     * @throws IOException if writing to {@code os} resulted in an IOException, passing the Exception
     */
    private static void writeJPG(BufferedImage buffImg, float quality, OutputStream os) throws IOException {
        ImageOutputStream ios =  ImageIO.createImageOutputStream(os);
        ImageWriter writer = ImageIO.getImageWritersByFormatName("jpeg").next();

        ImageWriteParam iwp = writer.getDefaultWriteParam();
        iwp.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
        iwp.setCompressionQuality(quality);

        writer.setOutput(ios);
        writer.write(null, new IIOImage(buffImg,null,null), iwp);
        writer.dispose();
    }

    /**
     * <p>Returns the images format.</p>
     * <p>Processes the image if necessary.</p>
     * @return the images format (png, bmp, ...) as a String
     * @throws UnsupportedImageTypeException if the image type read from input is not supported
     * @throws IOException if there was an error during reading of input
     * @throws NoImageException if no image could be read from input
     */
    @Override
    public String getFormat() throws UnsupportedImageTypeException, IOException, NoImageException {
        if (this.bufferedImage == null)
            processImage(this.input);

        return this.format;
    }

    /**
     * Determines and returns the suitable EnDecoder according to {@link #preset} and type of {@link #bufferedImage},
     * passing the provided parameters.
     */
    @Override
    public Encoder getEncoder(long seed, boolean sequential, boolean useErrorCorrection)
            throws UnsupportedImageTypeException, IOException, NoImageException {
        if (this.bufferedImage == null)
            processImage(this.input);

        int type = bufferedImage.getType();

        Encoder enDecoder;

        switch (type) {

            // Supported Types
            //----------------------------------------------------------------------------------
            case BufferedImage.TYPE_4BYTE_ABGR:
            case BufferedImage.TYPE_3BYTE_BGR:
            case BufferedImage.TYPE_INT_ARGB:
            case BufferedImage.TYPE_INT_RGB:
            case BufferedImage.TYPE_INT_BGR:
            // Types that have not been tested, but should be suitable for all Algorithms
            //----------------------------------------------------------------------------------
            case BufferedImage.TYPE_4BYTE_ABGR_PRE: // could not be found or artificially created
            case BufferedImage.TYPE_INT_ARGB_PRE: // could not be found or artificially created
                enDecoder = createEncoder(seed, sequential);
                break;

            // Types that are not supported - explicit for the sake of completeness
            //----------------------------------------------------------------------------------
            case BufferedImage.TYPE_BYTE_BINARY:
            case BufferedImage.TYPE_CUSTOM:
            case BufferedImage.TYPE_USHORT_555_RGB:
            case BufferedImage.TYPE_USHORT_565_RGB:
            case BufferedImage.TYPE_USHORT_GRAY:
            // not supported anymore due to deletion of GIF portion
            case BufferedImage.TYPE_BYTE_INDEXED:
            // could be supported when JPEG -> would need new Overlay and PixelTranslator
            case BufferedImage.TYPE_BYTE_GRAY:
            default:
                throw new UnsupportedImageTypeException("Image type (BufferedImage.TYPE = " + type + ") is not supported");
        }

        return useErrorCorrection ? new ReedSolomon(enDecoder) : enDecoder;
    }

    private Encoder createEncoder(long seed, boolean sequential) {
        switch (this.preset) {

            case TEST:
                return new MarkingPlainEncoder(
                        new MarkingDcrasEmbedder(Rgb2YCbCr::new, new FastDct8(), .85f),
                        new BlockShuffleOverlay(bufferedImage, seed, 16/*, noSingleColors*/),
                        false
                );


            case COMPRESSION_RESISTANCE:
                return new PlainEncoder<>(
                        new DcrasEmbedder(Rgb2YCbCr::new, new FastDct8(), .65f, 3, 2),
                        new BlockShuffleOverlay(bufferedImage, seed, 16),
                        sequential
                );

            case DETECTION_RESISTANCE:
                return new LossLessStcEncoder<>(
                        new DmasEmbedder(Rgb2YCbCr::new, new FastDct8(), .95f, 7, 7),
                        new BlockShuffleOverlay(bufferedImage, seed, 8,
                                allPixelsOpaque/*.and(noSingleColors)*/),
                        new JUniward(Rgb2YCbCr::new, new Wavelet()),
                        sequential,
                        seed
                );

            case MINIMAL_IMPACT:
                return new LossLessStcEncoder<>(
                        new PixelBit(),
                        new PixelShuffleOverlay(this.bufferedImage, seed, isPixelOpaque),
                        (x, y) -> 1d,
                        sequential,
                        seed
                );

            case RESISTANCE_HYBRID:
            default:
                return new StcEncoder<>(
                        new DmasEmbedder(Rgb2YCbCr::new, new FastDct8(), .85f-0.3f),
                        new BlockShuffleOverlay(bufferedImage, seed, 8),
                        new JUniward(Rgb2YCbCr::new, new Wavelet()),
                        sequential,
                        seed
                );
        }
    }
}
