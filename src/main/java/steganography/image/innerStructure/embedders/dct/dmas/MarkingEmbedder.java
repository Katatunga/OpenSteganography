package steganography.image.innerStructure.embedders.dct.dmas;

import steganography.exceptions.encoder.EmbedderInputException;
import steganography.image.operation.pixelTranslation.PixelTranslator;
import steganography.image.operation.pixelTranslation.TranslatorSupplier;
import steganography.transforms.Transform;

public class MarkingEmbedder extends DmasEmbedder {
    public MarkingEmbedder(TranslatorSupplier<PixelTranslator> translatorSupplier, Transform<double[][]> dctTransform, Float qf) {
        super(translatorSupplier, dctTransform, qf);
    }

    public MarkingEmbedder(TranslatorSupplier<PixelTranslator> translatorSupplier, Transform<double[][]> dctTransform, Float qf, int refX, int refY) {
        super(translatorSupplier, dctTransform, qf, refX, refY);
    }



    private void markChunk(int[] cvrElem, int borderColor) {
        for (int i = 0; i < 8; i++) {
            cvrElem[i*8] = borderColor;
            cvrElem[i*8+7] = borderColor;
            cvrElem[i] = borderColor;
            cvrElem[63-i] = borderColor;
        }
    }


    public int[] embedMark(int[] cvrElem, boolean one) throws EmbedderInputException {
        markChunk(cvrElem, (representsOne(cvrElem) != one ? 0xffff0000 : 0xff00ff00));
        return cvrElem;
    }


    public int[] flipMark(int[] cvrElem) throws EmbedderInputException {
        markChunk(cvrElem, 0xffff0000);
        return cvrElem;
    }
}
