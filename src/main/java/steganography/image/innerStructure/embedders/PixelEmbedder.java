package steganography.image.innerStructure.embedders;

/**
 * Super class to Embedders using pixels, like
 * {@link steganography.image.innerStructure.embedders.spatial.PixelBit PixelBit}
 * or
 * {@link steganography.image.innerStructure.embedders.spatial.LsbReplacer LsbReplacer}
 */
public abstract class PixelEmbedder implements Embedder<Integer> {
}
