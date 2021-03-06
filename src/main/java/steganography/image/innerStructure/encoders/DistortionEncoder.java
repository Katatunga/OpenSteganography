package steganography.image.innerStructure.encoders;

import steganography.image.innerStructure.distortion.DistortionFunction;
import steganography.image.innerStructure.embedders.Embedder;
import steganography.image.innerStructure.overlays.abstracts.BuffImgOverlay;

/**
 * Super class to Encoders using a DistortionFunction
 * @param <T> Representation of pixel values dictated by return value of
 *           the used {@link BuffImgOverlay}
 */
public abstract class DistortionEncoder<T> extends GeneralEncoder<T> {
    protected final DistortionFunction<T> distortion;

    protected DistortionEncoder(Embedder<T> embedder, BuffImgOverlay<T> overlay, DistortionFunction<T> distortion) {
        super(embedder, overlay);
        this.distortion = distortion;
    }

    protected DistortionEncoder(Embedder<T> embedder, BuffImgOverlay<T> overlay, DistortionFunction<T> distortion,
                                boolean sequential) {
        super(embedder, overlay, sequential);
        this.distortion = distortion;
    }
}
