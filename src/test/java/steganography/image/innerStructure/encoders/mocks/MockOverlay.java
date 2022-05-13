package steganography.image.innerStructure.encoders.mocks;

import steganography.image.innerStructure.overlays.abstracts.BuffImgOverlay;

import java.util.BitSet;
import java.util.NoSuchElementException;

public class MockOverlay implements BuffImgOverlay<Boolean> {
    private final BitSet cvr_elements;

    public MockOverlay(BitSet cvr_elements) {
        this.cvr_elements = cvr_elements;
    }

    @Override
    public Boolean get(int position) throws NoSuchElementException {
        return this.cvr_elements.get(position);
    }

    @Override
    public void set(Boolean value, int position) throws NoSuchElementException {
        this.cvr_elements.set(position, value);
    }

    @Override
    public int available() {
        return this.cvr_elements.length();
    }
}
