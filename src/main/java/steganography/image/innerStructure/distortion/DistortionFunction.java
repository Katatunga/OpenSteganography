package steganography.image.innerStructure.distortion;

import steganography.image.innerStructure.embedders.Embedder;

/**
 * <p>Functional interface to implement by classes calculating Distortions. The intended use is the calculation
 * of an embedding impact into spatial covers of an image.</p>
 * @param <T> any class that can represent (part of) a spatial image.
 */
public interface DistortionFunction<T> {

    /**
     * <p>Calculates and returns a value indicating the magnitude of distortion between the provided parameters.</p>
     * <p>The parameters represent Cover Elements before ({@code original}) and after ({@code embedded})
     * manipulation by an {@link Embedder Embedder} or any other manipulator.</p>
     * @param original typically a spatial cover element before an arbitrary manipulation,
     *                 for example by an {@link Embedder Embedder}
     * @param embedded typically a spatial cover element after an arbitrary manipulation,
     *                 for example by an {@link Embedder Embedder}
     * @return a value indicating the magnitude of distortion (or difference) between the provided elements.
     */
    Double calculateDistortion(T original, T embedded);
}
