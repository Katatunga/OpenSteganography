package steganography.image.innerStructure.embedders.dct.dcras;

import steganography.image.operation.pixelTranslation.PixelTranslator;
import steganography.image.operation.pixelTranslation.TranslatorSupplier;
import steganography.transforms.Transform;

import java.util.Random;

/**
 * Not a usable class, but maybe the idea is helpful to someone
 */
public class RandomDcras extends DcrasEmbedder {
    private final Random random;
    private int currentIndex = 0;

    /**
     * Valid coefficients (y,x) to use for embedding
     */
    private static final int[] COEFFICIENTS = new int[]{0,4, 1,3, 0,3, 3,0, 1,2, 2,1, 2,2, 2,3, 3,2, 3,1, 4,1, 4,0, 5,0};
    /**
     * Valid (tested) value per coefficient to use as minimum threshold
     */
    private static final int[] THRESHOLDS = new int[]{10, 13, 10, 9, 13, 10};

    public RandomDcras(TranslatorSupplier<PixelTranslator> translatorSupplier, Transform<double[][]> dctTransform,
                       Random random, Float qf) {
        super(translatorSupplier, dctTransform, qf);
        this.random = random;
    }

    @Override
    protected int pickEmbeddingChunk(PixelTranslator[] chunks) {
        this.currentIndex = this.random.nextInt(COEFFICIENTS.length / 2) * 2;
        return random.nextInt(chunks.length);
    }

    protected double getRefValue(PixelTranslator chunk) {
        return chunk.get(COEFFICIENTS[this.currentIndex + 1], COEFFICIENTS[this.currentIndex]);
    }

    protected void setCoefficient(EmbeddingData ed, double value) {
        ed.getEmbChunk().set(COEFFICIENTS[this.currentIndex + 1], COEFFICIENTS[this.currentIndex], value);
    }

    protected double getMinDistance() {
        return THRESHOLDS[this.currentIndex / 2] * 1.3;
    }
}
