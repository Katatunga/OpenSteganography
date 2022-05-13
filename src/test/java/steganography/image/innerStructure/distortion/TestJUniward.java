package steganography.image.innerStructure.distortion;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import steganography.image.innerStructure.distortion.JUniward;
import steganography.image.operation.pixelTranslation.Rgb2YCbCr;
import steganography.transforms.Wavelet;

import java.util.Random;

public class TestJUniward {

    final double[][] rough = {
            {255,   0, 255,   0,   0, 255,   0,   0},
            {255,   0,   0, 255,   0, 255,   0,   0},
            {255,   0, 255,   0,   0, 255,   0,   0},
            {255, 255, 255, 255, 255, 255, 255, 255},
            {255,   0, 255,   0,   0, 255,   0,   0},
            {255,   0,   0, 255,   0, 255,   0,   0},
            {255,   0, 255,   0,   0, 255,   0,   0},
            {255,   0,   0, 255,   0, 255,   0,   0},
    };
    final double[][] ch_rough = {
            {255,   0, 255,   0,   0, 255,   0,   0},
            {255,   0,   0, 255,   0, 255,   0,   0},
            {255,   0, 255,   0,   0, 255,   0,   0},
            {255, 255, 255, 255, 255, 255, 255, 255},
            {255,   0, 255,   0,   0, 255,   0,   0},
            {255,   0,   0, 255,   0, 255,   0,   0},
            {255,   0, 255,   0,   0, 255,   0,   0},
            {255,   0,   0, 255,   0, 255,   0,   0},
    };
    final double[][] smooth = {
            {128, 128, 128, 128, 128, 128, 128, 128},
            {128, 128, 128, 128, 128, 128, 128, 128},
            {128, 128, 128, 128, 128, 128, 128, 128},
            {128, 128,   0,   0,   0,   0, 128, 128},
            {128, 128,   0,   0,   0,   0, 128, 128},
            {128, 128, 128, 128, 128, 128, 128, 128},
            {128, 128, 128, 128, 128, 128, 128, 128},
            {128, 128, 128, 128, 128, 128, 128, 128},
    };
    final double[][] ch_smooth = {
            {128, 128, 128, 128, 128, 128, 128, 128},
            {128, 128, 128, 128, 128, 128, 128, 128},
            {128, 128, 128, 128, 128, 128, 128, 128},
            {128, 128,   0,   0,   0,   0, 128, 128},
            {128, 128,   0,   0,   0,   0, 128, 128},
            {128, 128, 128, 128, 128, 128, 128, 128},
            {128, 128, 128, 128, 128, 128, 128, 128},
            {128, 128, 128, 128, 128, 128, 128, 128},
    };
    final double[][] same1 = {
            {128, 128, 128, 128, 128, 128, 128, 128},
            {128, 128, 128, 128, 128, 128, 128, 128},
            {128, 128, 128, 128, 128, 128, 128, 128},
            {128, 128, 128, 128, 128, 128, 128, 128},
            {128, 128, 128, 128, 128, 128, 128, 128},
            {128, 128, 128, 128, 128, 128, 128, 128},
            {128, 128, 128, 128, 128, 128, 128, 128},
            {128, 128, 128, 128, 128, 128, 128, 128},
    };
    final double[][] same2 = {
            {40, 40, 40, 40, 40, 40, 40, 40},
            {40, 40, 40, 40, 40, 40, 40, 40},
            {40, 40, 40, 40, 40, 40, 40, 40},
            {40, 40, 40, 40, 40, 40, 40, 40},
            {40, 40, 40, 40, 40, 40, 40, 40},
            {40, 40, 40, 40, 40, 40, 40, 40},
            {40, 40, 40, 40, 40, 40, 40, 40},
            {40, 40, 40, 40, 40, 40, 40, 40},
    };

    @Test
    void test_Juniward() {
        final Random random = new Random();

        for (int i = 0; i < 100; i++) {

            int ry = random.nextInt(8);
            int rx = random.nextInt(8);
            int ch = random.nextInt(20);

            ch_rough[ry][rx] += rough[ry][rx] < ch ? ch : -ch;
            ch_smooth[ry][rx] += smooth[ry][rx] < ch ? ch : -ch;

            JUniward juniw = new JUniward(Rgb2YCbCr::new, new Wavelet());
            double juni1 = juniw.jUniward(rough, ch_rough);
            double juni2 = juniw.jUniward(smooth, ch_smooth);

            Assertions.assertTrue(juni1 < juni2);
        }
    }

    @Test
    void test_Juniward_same() {
        final Random random = new Random();

        for (int i = 0; i < 100; i++) {

            int ry = random.nextInt(8);
            int rx = random.nextInt(8);
            int ch = random.nextInt(40)-20;

            double[][] same1_ch = deepCopy(same1);
            double[][] same2_ch = deepCopy(same2);

            same1_ch[ry][rx] += ch;
            same2_ch[ry][rx] += ch;

            JUniward juniw = new JUniward(Rgb2YCbCr::new, new Wavelet());
            double juni1 = juniw.jUniward(same1, same1_ch);
            double juni2 = juniw.jUniward(same2, same2_ch);

            Assertions.assertEquals(juni1, juni2, 0.1d);
        }
    }

    @Test
    void test_Juniward_same_strangeValues() {
        final Random random = new Random();

        for (int i = 0; i < 100; i++) {

            int ry = random.nextInt(8);
            int rx = random.nextInt(8);
            int ch = random.nextBoolean() ? 192 : -192;

            double[][] same1_ch = deepCopy(same1);
            double[][] same2_ch = deepCopy(same2);

            same1_ch[ry][rx] += ch;
            same2_ch[ry][rx] += ch;

            JUniward juniw = new JUniward(Rgb2YCbCr::new, new Wavelet());
            double juni1 = juniw.jUniward(same1, same1_ch);
            double juni2 = juniw.jUniward(same2, same2_ch);

            Assertions.assertEquals(juni1, juni2, 0.1d);
        }
    }

    @Test
    void test_Juniward_random() {
        final Random random = new Random();
        double[] result = new double[]{0, 0};
        for (int i = 0; i < 100; i++) {

            double[][] smooth = random2DArray(8, 8, 0, 64);
            double[][] smooth_ch = deepCopy(smooth);
            double[][] rough = random2DArray(8, 8, 0, 255);
            double[][] rough_ch = deepCopy(rough);

            int ry = random.nextInt(8);
            int rx = random.nextInt(8);
            int ch = random.nextInt(20);

            smooth_ch[ry][rx] += smooth[ry][rx] < ch ? ch : -ch;
            rough_ch[ry][rx] += rough[ry][rx] < ch ? ch : -ch;

            JUniward juniw = new JUniward(Rgb2YCbCr::new, new Wavelet());
            result[0] += juniw.jUniward(rough, rough_ch);
            result[1] += juniw.jUniward(smooth, smooth_ch);
        }

        Assertions.assertTrue(result[0] < result[1], "May randomly fail, but very seldom.");
    }

    private double[][] deepCopy(double[][] arr) {
        double[][] cp = new double[arr.length][arr[0].length];
        for (int i = 0; i < arr.length; i++) {
            System.arraycopy(arr[i], 0, cp[i], 0, arr[0].length);
        }
        return cp;
    }

    private double[][] random2DArray(int length1, int length2, int lowerBound, int upperBound) {
        Random random = new Random();
        double[][] ret = new double[length1][length2];
        for (int i = 0; i < length1; i++) {
            ret[i] = randomArray(length2, lowerBound, upperBound);
        }
        return ret;
    }

    private double[] randomArray(int length, int lowerBound, int upperBound) {
        Random random = new Random();
        double[] ret = new double[length];
        for (int i = 0; i < length; i++) {
            ret[i] = random.nextInt(upperBound - lowerBound) + lowerBound;
        }
        return ret;
    }
}
