package steganography.image.innerStructure.encoders.stc;

import steganography.exceptions.encoder.EmbedderInputException;
import steganography.image.innerStructure.distortion.DistortionFunction;
import steganography.image.innerStructure.embedders.Embedder;
import steganography.image.innerStructure.encoders.DistortionEncoder;
import steganography.image.innerStructure.overlays.abstracts.BuffImgOverlay;
import steganography.util.ArrayUtils;

import java.util.*;

/**
 * <p>Implementation of an Encoder and Decoder using STCs as explained in the paper
 * <a href=http://ieeexplore.ieee.org/document/5740590/>
 * Minimizing Additive Distortion in Steganography Using Syndrome-Trellis Codes</a>.</p>
 * <p>Comments in this class referring to the paper will do so by stating: <em>{@link StcEncoder reference}</em>
 * linking to this JavaDoc</p>
 * <p>STCs minimize the embedding impact by determining the least changes possible using matrix embedding.
 * The matrix is constructed in a deterministic way from a submatrix shared between sender and receiver.
 * The matrix multiplied with the embedded sequence resolves to the intended message.</p>
 *
 * <p>The implementation uses {@link BitSet BitSets} to minimize the storage impact and improve the speed of
 * matrix multiplication.</p>
 *
 * @param <T> A class capable of representing one or multiple pixels of a spatial image.
 */
public class StcEncoder<T> extends DistortionEncoder<T> {

    /**
     * Height of the H_hat matrix <em>(constraint height)</em>. Design parameter that affects the
     * algorithms (STCs) speed and efficiency, "typically, 6 <= h <= 15" (see <em>{@link StcEncoder reference p.6}</em>)
     */
    private static final int HAT_HEIGHT = 10;

    private static final int MAX_INT_NOTATION = (int) Math.pow(2, HAT_HEIGHT);

    /**
     * {@link Random} which is used to randomize certain parts of the algorithm
     * @see #build_H_hat
     */
    private final Random random;

    /**
     * <p>Creates an Encoder and Decoder that coordinates embedding using STCs (see <em>{@link StcEncoder reference}</em>)
     * to minimize embedding distortion.</p>
     * <p>This StcEncoder {@link #embed(BitSet, BitSet, int) embeds} the complete stego vector (result of
     * {@link #viterbi_stc viterbi}), affecting every {@code Cover Element} (depending on {@link Embedder}
     * implementation), resulting in higher compression resistance. This approach could be part of a hybrid embedding
     * process, trying to balance detection and compression robustness.</p>
     * <p>This Encoder offers a sequential mode
     * (see {@link #StcEncoder(Embedder, BuffImgOverlay, DistortionFunction, boolean)}.</p>
     * <p>This Encoder is compatible with: {@link LossLessStcEncoder}</p>
     * @param embedder Embedder to flip or embed Bits in Cover Elements
     * @param overlay Overlay returning Pixels as if part of a sequence
     * @param distortion DistortionFunction to calculate the distortion impact of embedding
     */
    public StcEncoder(Embedder<T> embedder, BuffImgOverlay<T> overlay, DistortionFunction<T> distortion) {
        super(embedder, overlay, distortion);
        this.random = new Random(DEFAULT_SEED);
    }

    /**
     * <p>Creates an Encoder and Decoder that coordinates embedding using STCs (see <em>{@link StcEncoder reference}</em>)
     * to minimize embedding distortion.</p>
     * <p>This StcEncoder {@link #embed(BitSet, BitSet, int) embeds} the complete stego vector (result of
     * {@link #viterbi_stc viterbi}), affecting every {@code Cover Element} (depending on {@link Embedder}
     * implementation), resulting in higher compression resistance. This approach could be part of a hybrid embedding
     * process, trying to balance detection and compression robustness.</p>
     * <p>This Encoder offers a sequential mode
     * (see {@link #StcEncoder(Embedder, BuffImgOverlay, DistortionFunction, boolean, long)}.</p>
     * <p>This Encoder is compatible with: {@link LossLessStcEncoder}</p>
     * @param embedder Embedder to flip or embed Bits in Cover Elements
     * @param overlay Overlay returning Pixels as if part of a sequence
     * @param distortion DistortionFunction to calculate the distortion impact of embedding
     * @param seed long to be set as initial seed to randomize encoding
     */
    public StcEncoder(Embedder<T> embedder, BuffImgOverlay<T> overlay, DistortionFunction<T> distortion, long seed) {
        super(embedder, overlay, distortion);
        this.random = new Random(seed);
    }

    /**
     * <p>Creates an Encoder and Decoder that coordinates embedding using STCs (see <em>{@link StcEncoder reference}</em>)
     * to minimize embedding distortion.</p>
     * <p>This StcEncoder {@link #embed(BitSet, BitSet, int) embeds} the complete stego vector (result of
     * {@link #viterbi_stc viterbi}), affecting every {@code Cover Element} (depending on {@link Embedder}
     * implementation), resulting in higher compression resistance. This approach could be part of a hybrid embedding
     * process, trying to balance detection and compression robustness.</p>
     * <p>This Encoder offers a sequential mode, allowing subsequent calls to {@link #encode} or {@link #decode}
     * without overwriting / rereading Cover Elements from the beginning. Sequential mode is used if the parameter
     * {@code sequential} is set to true. If sequential mode is active, this Encoder will always use
     * Bits(payload).length * 2 Cover Elements to embed the payload, leading to lower detection resistance.</p>
     * <p>This Encoder is compatible with: {@link LossLessStcEncoder}</p>
     * @param embedder Embedder to flip or embed Bits in Cover Elements
     * @param overlay Overlay returning Pixels as if part of a sequence
     * @param distortion DistortionFunction to calculate the distortion impact of embedding
     * @param sequential true, if this Encoder should use sequential mode
     */
    public StcEncoder(Embedder<T> embedder, BuffImgOverlay<T> overlay, DistortionFunction<T> distortion, boolean sequential) {
        super(embedder, overlay, distortion, sequential);
        this.random = new Random(DEFAULT_SEED);
    }

    /**
     * <p>Creates an Encoder and Decoder that coordinates embedding using STCs (see <em>{@link StcEncoder reference}</em>)
     * to minimize embedding distortion.</p>
     * <p>This StcEncoder {@link #embed(BitSet, BitSet, int) embeds} the complete stego vector (result of
     * {@link #viterbi_stc viterbi}), affecting every {@code Cover Element} (depending on {@link Embedder}
     * implementation), resulting in higher compression resistance. This approach could be part of a hybrid embedding
     * process, trying to balance detection and compression robustness.</p>
     * <p>This Encoder offers a sequential mode, allowing subsequent calls to {@link #encode} or {@link #decode}
     * without overwriting / rereading Cover Elements from the beginning. Sequential mode is used if the parameter
     * {@code sequential} is set to true. If sequential mode is active, this Encoder will always use
     * Bits(payload).length * 2 Cover Elements to embed the payload, leading to lower detection resistance.</p>
     * <p>This Encoder is compatible with: {@link LossLessStcEncoder}</p>
     * @param embedder Embedder to flip or embed Bits in Cover Elements
     * @param overlay Overlay returning Pixels as if part of a sequence
     * @param distortion DistortionFunction to calculate the distortion impact of embedding
     * @param sequential true if this encoder should use sequential mode
     * @param seed long to be set as initial seed to randomize encoding
     */
    public StcEncoder(Embedder<T> embedder, BuffImgOverlay<T> overlay, DistortionFunction<T> distortion,
                      boolean sequential, long seed) {
        super(embedder, overlay, distortion, sequential);
        this.random = new Random(seed);
    }

    /**
     * <p>Encodes the provided payload into the image represented by the overlay. This
     * Encoder uses STCs (see <em>{@link StcEncoder reference}</em>) to minimize encoding impact.</p>
     * <p>Due to STCs behaviour, all Cover Elements will be analyzed and a certain amount will be impacted,
     * according to {@link #embed}.</p>
     */
    @Override
    protected void encodeAlgorithm(byte[] payload) throws EmbedderInputException {
        int bitLen = payload.length * 8;
        int maxUnits = this.sequentialMode ? bitLen * 2 : maxCvrLen(bitLen, this.available());

        // Bits currently represented by all Cover Elements
        BitSet cvrRep = new BitSet(maxUnits);
        // Distortion values for each Cover Element
        double[] rho = new double[maxUnits];

        // Gather cvrRep and distortion values
        for (int i = 0; i < maxUnits; i++) {
            T cvrObj = this.overlay.get(i + this.sequencePosition);
            cvrRep.set(i, this.embedder.representsOne(cvrObj));
            T stegObj = this.embedder.flip(cvrObj);
            rho[i] = this.distortion.calculateDistortion(cvrObj, stegObj);
        }

        // Build H_hat and use viterbi algorithm to minimize embedding impact
        int[] hHat = build_H_hat(calculateK(maxUnits, bitLen), this.random);
        BitSet steg = viterbi_stc(cvrRep, maxUnits, BitSet.valueOf(payload), bitLen, rho, hHat);

        // Embed the result
        embed(steg, cvrRep, maxUnits);

        updateSequencePosition(this.sequencePosition + maxUnits);
    }

    /**
     * <p>Embeds all Bits of {@code steg} in all Cover Elements.</p>
     * <p>This procedure affects all Cover Elements and is therefore less detection resistant, but it
     * allows the {@link Embedder embedders} to enforce a minimum distance in the representation of Cover elements
     * to prevent Bit flips enabling higher compression resistance.</p>
     * <p>Even though all Cover Elements are affected, the biggest changes (Bit flips) are still minimized
     * by the {@link #viterbi_stc viterbi}-algorithm, making this a balanced embedding approach.</p>
     * @param steg Bits to be embedded into all Cover Elements
     * @param cvrRep Bits currently represented by all Cover Elements
     * @param cvrLength amount of used cover elements, because {@code steg.length()} is unreliable
     * @throws EmbedderInputException if the output of the Overlay doesn't match the Input the Embedder requires.
     */
    protected void embed(BitSet steg, BitSet cvrRep, int cvrLength) throws EmbedderInputException {
        for (int i = 0; i < cvrLength; i++) {
            T cvrElem = this.overlay.get(i + this.sequencePosition);
            this.overlay.set(this.embedder.embed(cvrElem, steg.get(i)), i + this.sequencePosition);
        }
    }

    @Override
    protected byte[] decodeAlgorithm(int bLength) throws EmbedderInputException {
        if (bLength == 0)
            return new byte[0];

        int bitLen = bLength * 8;
        int maxUnits = this.sequentialMode ? bitLen * 2 : maxCvrLen(bitLen, this.available());

        // Bits represented by cover
        BitSet cvrRep = new BitSet(maxUnits);

        // Get the Bits represented by all Cover Elements
        for (int i = 0; i < maxUnits; i++) {
            T cvrObj = this.overlay.get(i + this.sequencePosition);
            cvrRep.set(i, embedder.representsOne(cvrObj));
        }

        // build H_hat and parity matrix
        int[] hHat = build_H_hat(calculateK(maxUnits, bitLen), this.random);

        // Do matrix multiplication (improved by BitSet) to get message
        byte[] message = multiply(binRep(hHat), cvrRep, bitLen).toByteArray();

        // if last bytes are zero bytes (not returned by BitSet)
        if (message.length < bLength) {
            byte[] intermediate = new byte[bLength];
            System.arraycopy(message, 0, intermediate, 0, message.length);
            message = intermediate;
        }

        this.updateSequencePosition(maxUnits);

        return message;
    }

    /**
     * Provides the maximum utilizable cover length with respect to payload length in Bit, {@link #HAT_HEIGHT}
     * and the pseudorandom build of H_HAT.
     * @param bitLen length of the payload
     * @param available available cover elements
     * @return maximum amount of utilizable Cover Elements
     */
    private int maxCvrLen(int bitLen, int available) {
        // H_HATs columns must be different from another, odd, and start with a 1 bit, limiting the possibilities.
        // So the maximum utilizable cover length is k < ((2 ^ HAT_HEIGHT) / 4)
        int maxCvrLenPerBit = (int) (Math.pow(2, HAT_HEIGHT - 2) - 1);
        return Math.min(available*2, bitLen * maxCvrLenPerBit);
    }

    @Override
    public int available() {
        return (super.available() - this.sequencePosition) / 2;
    }

    /**
     * <p>The STC-adapted viterbi algorithm as defined in <em>{@link StcEncoder reference} p. 7</em>, adapted
     * to use BitSets to minimize resource consumption.</p>
     * <p>Necessary changes have been made, as the Pseudocode algorithm did not necessarily pick the minimum
     * impact path.</p>
     * @param cvr Bits represented by the Cover Elements before changes
     * @param cvrLen Amount of Cover Elements, as BitSets length calculation ignores trailing zeroes
     * @param message Message bits
     * @param msgLen Amount of Message Bits, as BitSets length calculation ignores trailing zeroes
     * @param rho Distortion values sorted chronologically to the Cover Elements
     * @param h_hat Submatrix as defined in <em>{@link StcEncoder reference} p. 6</em>
     * @return The Steganographic Elements to replace the Cover Elements with.
     */
    protected final BitSet viterbi_stc(BitSet cvr, int cvrLen, BitSet message, int msgLen, double[] rho, int[] h_hat) {
        if (msgLen > cvrLen)
            throw new IllegalArgumentException(
                    String.format("Message (%d) is longer than Cover (%d)", msgLen, cvrLen));

        int w = h_hat.length;

        int stateAmount = (int) Math.pow(2, HAT_HEIGHT);
        int halfStateAmount = stateAmount / 2;

        BitSet[] path = new BitSet[msgLen * w];
        for (int i = 0; i < path.length; i++) path[i] = new BitSet();

        // -------------------------------------
        // forward part of the Viterbi algorithm
        // -------------------------------------
        double[] wght = new double[stateAmount];
        Arrays.fill(wght, Double.POSITIVE_INFINITY);
        wght[0] = 0;

        int indx = 0;
        int indm;
        for (indm = 0; indm < msgLen; indm++) {
            for (int hh_col : h_hat) {
                double[] newwght = new double[wght.length];
                for (int k = 0; k < stateAmount; k++) {
                    double w0 = wght[k] + (cvr.get(indx) ? rho[indx] : 0);
                    double w1 = wght[k ^ hh_col] + (cvr.get(indx) ? 0 : rho[indx]);
                    path[indx].set(k, w1 < w0);
                    newwght[k] = Math.min(w0, w1);
                }
                indx++;
                wght = newwght;
            }

            // prune states
            for (int j = 0; j < halfStateAmount; j++) {
                wght[j] = wght[2 * j + (message.get(indm) ? 1 : 0)];
            }

            for (int j = halfStateAmount; j < stateAmount; j++) {
                wght[j] = Double.POSITIVE_INFINITY;
            }
        }

        BitSet y = new BitSet(cvrLen); // stc-encoded message to encode in stego-object
        int minInd = ArrayUtils.minInd(wght);

        // --------------------------------------
        // backward part of the Viterbi algorithm
        // --------------------------------------
        int state = minInd; // this part was wrong in pseudocode: // int state = 0
        indx--;
        for (--indm; indm >= 0; indm--) {
            state = 2 * state + (message.get(indm) ? 1 : 0); // This part was wrong in pseudocode: executed after for-loop
            for (int j = w-1; j >= 0; j--) {
                if (path[indx].get(state)) {
                    y.set(indx);
                    state ^= h_hat[j];
                }
                indx--;
            }
        }
        return y;
    }

    /**
     * Calculates k, used as the width of the matrix H_hat in stc algorithm.
     * @param cvrLength length of the cover elements
     * @param msgLength message length in bit
     * @return k as in {@code 1/k <= (msgLength / cvrLength) < 1/k+1} or {@code cvrLength / msgLength}
     */
    private int calculateK(int cvrLength, int msgLength) {
        // this way, alpha (= msgLength / cvrLength) lies between 1/k and 1/(k+1)
        return msgLength == 0 ? 0 : cvrLength / msgLength;
    }

    /**
     * <p>Returns the integer representation of a matrix in binary representation (see {@link #binRep})</p>
     * <p>The integers are returned as an array, each integer representing the binary values of each column.</p>
     * <p>The first row of {@code binRep} is treated as the respective least significant bits.</p>
     * @param binRep a binary matrix as described by {@link #binRep}.
     * @return an integer array representing the columns of binary values in {@code binRep}.
     */
    private int[] intRep(int[][] binRep) {
        int[] intRep = new int[binRep[0].length];
        int twoPower = 1;
        for (int[] bits : binRep) {
            for (int j = 0; j < intRep.length; j++) {
                intRep[j] += bits[j] * twoPower;
            }
            twoPower *= 2;
        }
        return intRep;
    }

    /**
     * <p>Returns the binary representation of a matrix in integer representation (see {@link #intRep})</p>
     * <p>The numbers are represented in columns, the first row representing the least significant Bit respectively.</p>
     * <p>The width of the resulting matrix is equal to {@code intRep.length}, the height is determined
     * by the maximum integer (as if by {@code h = floor(log2(max(intRep))) + 1})</p>
     * <p>The maximum integer can therefore not be smaller than 1. Integers are treated as absolute.</p>
     * @param intRep the integer representation of a binary matrix {@link #intRep} or any {@code int[]}
     *               #where {@code max >= 1}
     * @return a binary matrix representing the integers in its columns, the first row being the LSB
     */
    private int[][] binRep(int[] intRep) {
        int max = ArrayUtils.max(intRep);

        if (Math.abs(max) < 1)
            throw new IllegalArgumentException("Maximum integer value is smaller than 1");

        int maxBinLength = Double.valueOf(Math.log(max) / Math.log(2)).intValue() + 1;

        int[] intCp = new int[intRep.length];
        System.arraycopy(intRep, 0, intCp, 0, intRep.length);

        int[][] binRep = new int[maxBinLength][intCp.length];

        for (int i = 0; i < maxBinLength; i++) {
            for (int j = 0; j < intCp.length; j++) {
                binRep[i][j] = intCp[j] % 2;
                intCp[j] /= 2;
            }
        }
        return binRep;
    }

    /**
     * <p>WARNING: Legacy code! Using method {@link #buildParityMatrix} to build parameter {@code matrix}
     * can cause {@link OutOfMemoryError} with long messages. Kept as educational code.
     * Use {@link #multiply(int[][], BitSet, int)} instead.</p>
     * <p>Calculates a matrix x vector (Hy = m) multiplication using BitSets and returns the result (m).</p>
     * <p>Although this multplication saves resources, a dimension comparison
     * ({@code matrix[0].length == vector.length}) is not possible due to BitSets ignoring trailing
     * zeros in their length calculation.</p>
     * <p>Callers should be mindful of that fact and ensure correct dimensions beforehand.</p>
     * @param matrix Matrix to be multiplied by {@code vector}
     * @param vector vector to multiply the {@code matrix} with
     * @return the resulting vector of length {@code matrix.length}
     */
    private BitSet multiply(BitSet[] matrix, BitSet vector) {
        BitSet res = new BitSet(matrix.length);
        for (int i = 0; i < matrix.length; i++) {
            matrix[i].and(vector);
            res.set(i, matrix[i].cardinality() % 2 == 1);
        }
        return res;
    }

    /**
     * <p>Calculates a Hhat x vector (Hy = m) multiplication using BitSets and returns the result (m).</p>
     * <p>Although this multplication saves resources, a dimension comparison
     * ({@code Hhat[0].length == vector.length}) is not possible due to BitSets ignoring trailing
     * zeros in their length calculation.</p>
     * <p>Callers should be mindful of that fact and ensure correct dimensions beforehand.</p>
     * @param Hhat Matrix to be multiplied by {@code vector}
     * @param vector vector to multiply the {@code Hhat} with
     * @return the resulting vector of length {@code Hhat.length}
     */
    private BitSet multiply(int[][] Hhat, BitSet vector, int msgLength) {
        int h = Hhat.length;
        int w = Hhat[0].length;
        BitSet bits = new BitSet(vector.length());

        BitSet res = new BitSet(h);
        for (int i = 0; i < msgLength; i++) {
            bits.clear();

            for (int j = 0; j < h && (i-j) >= 0; j++) {
                for (int k = 0; k < w; k++) {
                    bits.set((i-j) * w + k, Hhat[j][k] > 0);
                }
            }

            bits.and(vector);
            res.set(i, bits.cardinality() % 2 == 1);
        }
        return res;
    }

    /**
     * <p>Constructs a matrix to be used as Submatrix (H_hat) as described in
     * <em>{@link StcEncoder reference} p. 8</em> in its integer representation.</p>
     * <p>The matrix is constructed randomly but keeps the most and least significant bit of each
     * integer as 1 and avoids duplicates.</p>
     * @param k treated as width of the matrix
     * @param random {@link Random} to randomize the matrix with. Seed should be set to ensure reliable outcome
     * @return an integer array representing a Submatrix (see above) in its integer representation
     */
    private int[] build_H_hat(int k, Random random) {
        if (k >= MAX_INT_NOTATION / 4)
            throw new IllegalArgumentException("k must not be greater than or equal to 2^(HAT_HEIGHT-2)");

        Set<Integer> intSet = new HashSet<>();
        int[] intRep = new int[k];
        for (int i = 0; i < k; i++) {
            // random value, but most and least significant bits set to 1
            int colVal = random.nextInt(MAX_INT_NOTATION) | (1 << HAT_HEIGHT-1) | 1;
            // avoid duplicates by searching for gap
            while (!intSet.add(colVal)) {
                // keep value odd and most significant bit as 1
                colVal = ((colVal + 2) % MAX_INT_NOTATION) | (1 << HAT_HEIGHT-1);
            }
            intRep[i] = colVal;
        }

        return intRep;
    }

    /**
     * <p>WARNING: Legacy code! Using this method can cause {@link OutOfMemoryError} with long messages.
     * Kept as educational code. If you use {@link #multiply(int[][], BitSet, int)}, you will not need
     * the full parity check matrix.</p>
     * <p>Builds and returns a parity matrix from a Submatrix {@code H_hat} (as could be constructed by
     * {@link #build_H_hat build_H_hat}) and the given dimensions, where
     * {@code cvrLen} will be the width and {@code msgLen} will be the height.</p>
     * <p>Construction follows the logic explained in <em>{@link StcEncoder reference} p. 6</em>.</p>
     * <p>The matrix will be represented as an array of {@link BitSet BitSets} to save resources when multiplying
     * with a vector. The major drawback is that the matrix' width is unreliable, as BitSets ignore trailing zeroes
     * in their calculation of length.</p>
     * @param H_hat Submatrix to build a Parity matrix out of (see above)
     * @param cvrLen width of the matrix (unreliable due to the use of {@link BitSet BitSets}). In STCs algorithm,
     *                 this should be equal to the amount of Cover Elements used in En- / Decoding, hence the name.
     * @param msgLen height of the matrix (or amount of {@link BitSet BitSets}). In STCs algorithm,
     *                 this should be equal to the encoded payload (or message) length, hence the name.
     * @return Parity matrix to decode the payload out of Cover Elements
     */
    private BitSet[] buildParityMatrix(int[][] H_hat, int cvrLen, int msgLen) {
        BitSet[] parMat = new BitSet[msgLen];
        for (int i = 0; i < parMat.length; i++)
            parMat[i] = new BitSet();

        int k = 0;
        for (int l = 0; l < cvrLen; l += H_hat[0].length) {
            for (int i = 0; i < H_hat[0].length && i+l < cvrLen; i++) {
                for (int j = k; j < parMat.length && j-k < H_hat.length; j++) { // y coordinate
                    parMat[j].set(i+l, H_hat[j-k][i] == 1);
                }
            }
            k++;
        }
        return parMat;
    }
}
