package steganography.image.innerStructure.embedders;

import steganography.exceptions.encoder.EmbedderInputException;

public interface Embedder<T> {

    /**
     * <p>Manipulates the provided Cover Element ({@code cvrElem}) to embed a single Bit into it
     * and returns the result.</p>
     * <p>The embedded Bit is one if the provided boolean {@code one} is true and zero otherwise.</p>
     * <p>This interface does not specify whether the manipulation is in-place, so only the return
     * value is reliable.</p>
     * @param cvrElem Cover Element to embed a Bit into
     * @param one boolean indicating if Bit to embed should be a one (true) or a zero (false)
     * @return Manipulated Cover Element (with embedded Bit)
     * @throws EmbedderInputException if the provided Cover Element is unsuitable for this Embedder
     */
    T embed(T cvrElem, boolean one) throws EmbedderInputException;

    /**
     * <p>Manipulates the provided Cover Element ({@code cvrElem}) to flip the Bit it represents
     * (according to this Embedder, see {@link #representsOne}) and returns the result.</p>
     * <p>This interface does not specify whether the manipulation is in-place, so only the return
     * value is reliable.</p>
     * @param cvrElem Cover Element to embed a Bit into
     * @return Manipulated Cover Element (with embedded Bit)
     * @throws EmbedderInputException if the provided Cover Element is unsuitable for this Embedder
     */
    T flip(T cvrElem) throws EmbedderInputException;

    /**
     * <p>Analyzes the provided Cover Element ({@code cvrElem}) and returns the Bit it represents
     * according to this Embedder as a boolean.</p>
     * <p>The embedded Bit is one if the returned boolean is true and zero otherwise.</p>
     * @param cvrElem Cover Element to read the Bit from
     * @return true if the provided {@code cvrElem} represents a one, false if it represents a zero.
     * @throws EmbedderInputException if the provided Cover Element is unsuitable for this Embedder
     */
    boolean representsOne(T cvrElem) throws EmbedderInputException;
}
