package steganography.image.innerStructure.encoders.mocks;

import java.util.BitSet;
import java.util.NoSuchElementException;

public class CountingOverlay extends MockOverlay {
    public int changes = 0;

    public CountingOverlay(BitSet cvr_elements) {
        super(cvr_elements);
    }

    @Override
    public void set(Boolean value, int position) throws NoSuchElementException {
        changes++;
        super.set(value, position);
    }
}
