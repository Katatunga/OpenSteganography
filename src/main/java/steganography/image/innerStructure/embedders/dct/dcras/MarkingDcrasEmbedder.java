package steganography.image.innerStructure.embedders.dct.dcras;

import steganography.exceptions.encoder.EmbedderInputException;
import steganography.image.innerStructure.embedders.dct.dmas.DmasEmbedder;
import steganography.image.operation.pixelTranslation.PixelTranslator;
import steganography.image.operation.pixelTranslation.TranslatorSupplier;
import steganography.transforms.Transform;

public class MarkingDcrasEmbedder extends DcrasEmbedder {
    public MarkingDcrasEmbedder(TranslatorSupplier<PixelTranslator> translatorSupplier, Transform<double[][]> dctTransform, Float qf) {
        super(translatorSupplier, dctTransform, qf);
    }

    public MarkingDcrasEmbedder(TranslatorSupplier<PixelTranslator> translatorSupplier, Transform<double[][]> dctTransform, Float qf, int refX, int refY) {
        super(translatorSupplier, dctTransform, qf, refX, refY);
    }



    private void markChunk(int[] cvrElem, int borderColor) {
        for (int i = 0; i < 16; i++) {
            cvrElem[i*16] = borderColor;
            cvrElem[i*16+15] = borderColor;
            cvrElem[i] = borderColor;
            cvrElem[255-i] = borderColor;
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
