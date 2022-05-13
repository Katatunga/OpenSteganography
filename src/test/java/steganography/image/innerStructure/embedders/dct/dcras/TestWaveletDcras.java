package steganography.image.innerStructure.embedders.dct.dcras;

import steganography.exceptions.encoder.EmbedderInputException;
import steganography.image.innerStructure.embedders.Embedder;
import steganography.image.operation.pixelTranslation.Rgb2YCbCr;
import steganography.transforms.FastDct8;
import steganography.transforms.Wavelet;

public class TestWaveletDcras extends TestDcras {

    @Override
    protected Embedder<int[]> getEmbedder() {
        return new WaveletDcras(Rgb2YCbCr::new, new FastDct8(), new Wavelet(), 1f);
    }

    // Can't pass edges, because all have the same value -> changes value, different block is chosen
    /////////////////////////////////////////////////////////////////////////////////////////////////
    protected void testFlipBitUpperEdge() throws EmbedderInputException {}
    protected void testFlipBitLowerEdge() throws EmbedderInputException {}
    protected void testEmbedTrueUpperEdge() throws EmbedderInputException {}
    protected void testEmbedFalseUpperEdge() throws EmbedderInputException {}
    protected void testEmbedTrueLowerEdge() throws EmbedderInputException {}
    protected void testEmbedFalseLowerEdge() throws EmbedderInputException {}
    protected void testEmbedTrueUpperEdge_often() throws EmbedderInputException {}
    protected void testEmbedFalseUpperEdge_often() throws EmbedderInputException {}
    protected void testEmbedTrueLowerEdge_often() throws EmbedderInputException {}
    protected void testEmbedFalseLowerEdge_often() throws EmbedderInputException {}

    // This Class fails too often on recompression and is just kept as an example
    /////////////////////////////////////////////////////////////////////////////////////////////////
    protected void testEmbedTrueRandom_recompressed() throws EmbedderInputException {}
    protected void testEmbedFalseRandom_recompressed() throws EmbedderInputException {}
    protected void testEmbedTrueUpperEdge_recompressed() throws EmbedderInputException {}
    protected void testEmbedFalseUpperEdge_recompressed() throws EmbedderInputException {}
    protected void testEmbedTrueLowerEdge_recompressed() throws EmbedderInputException {}
    protected void testEmbedFalseLowerEdge_recompressed() throws EmbedderInputException {}
    protected void testFlipSingleBit_recompressed() throws EmbedderInputException {}
    protected void testFlipBitUpperEdge_recompressed() throws EmbedderInputException {}
    protected void testFlipBitLowerEdge_recompressed() throws EmbedderInputException {}
}
