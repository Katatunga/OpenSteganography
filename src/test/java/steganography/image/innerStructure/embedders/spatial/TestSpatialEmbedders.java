package steganography.image.innerStructure.embedders.spatial;

import org.junit.jupiter.api.Assertions;
import steganography.image.innerStructure.embedders.TestEmbeddersUnit;

import java.util.Random;

public abstract class TestSpatialEmbedders extends TestEmbeddersUnit<Integer> {
    private final Random random;

    TestSpatialEmbedders() {
        this.random = new Random();
    }

    @Override
    protected Integer getRandomInput() {
        return this.random.nextInt();
    }

    @Override
    protected Integer getUpperEdge() {
        return 0xffffffff;
    }

    @Override
    protected Integer getLowerEdge() {
        return 0xff000000;
    }

    @Override
    protected void assertEquals(Integer expected, Integer actual) {
        Assertions.assertEquals(expected, actual);
    }

    @Override
    protected void assertEquals(Integer expected, Integer actual, String message) {
        Assertions.assertEquals(expected, actual, message);
    }
}
