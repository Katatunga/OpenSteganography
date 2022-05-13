/*
 * Copyright (c) 2020
 * Contributed by NAME HERE
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package steganography.util;

import java.io.*;
import java.nio.file.Files;
import java.util.BitSet;

public class ArrayUtils {

    /**
     * @return the maximum value in the provided array.
     * @param arr array to find the maximum value of
     */
    public static double max(double[] arr) {
        double max = 0;
        for (double d : arr) {
            max = Math.max(d, max);
        }
        return max;
    }

    /**
     * @return the maximum value in the provided array.
     * @param arr array to find the maximum value of
     */
    public static int max(int[] arr) {
        double max = 0;
        for (double d : arr) {
            max = Math.max(d, max);
        }
        return (int) max;
    }

    /**
     * @return the maximum value in the provided array.
     * @param arr array to find the maximum value of
     * @param <T> a comparable class
     */
    public static <T extends Comparable<T>> T max(T[] arr) {
        T max = arr[0];

        if (arr.length == 1)
            return max;

        for (int i = 1; i < arr.length; i++) {
            if (max.compareTo(arr[i]) < 0)
                max = arr[i];
        }
        return max;
    }

    /**
     * @return the index of the maximum value in the provided array.
     * @param arr array to find the index of the maximum value of
     */
    public static int maxInd(double[] arr) {
        double max = -Double.MAX_VALUE;
        int maxInd = -1;
        for (int i = 0; i < arr.length; i++) {
            if (arr[i] > max) {
                max = arr[i];
                maxInd = i;
            }
        }
        return maxInd;
    }

    /**
     * @return the minimum value in the provided array.
     * @param arr array to find the minimum value of
     */
    public static double min(double[] arr) {
        double min = 0;
        for (double d : arr) {
            min = Math.min(d, min);
        }
        return min;
    }

    /**
     * @return the index of the minimum value in the provided array.
     * @param arr array to find the index of the minimum value of
     */
    public static int minInd(double[] arr) {
        double min = Double.MAX_VALUE;
        int minInd = -1;
        for (int i = 0; i < arr.length; i++) {
            if (arr[i] < min) {
                min = arr[i];
                minInd = i;
            }
        }
        return minInd;
    }

    /**
     * Converts ints from provided array "from" to bytes in provided array "to"
     * up to the length of the smaller array. Function sets values in-place
     * but returns "to" for convenience.
     * @param from ints to be converted to bytes
     * @param to byte array that will contain the converted values
     * @return param "to" for convenience
     */
    public static byte[] ints2Bytes(int[] from, byte[] to) {
        int l = Math.min(from.length, to.length);
        for (int i = 0; i < l; i++) {
            to[i] = (byte) from[i];
        }
        return to;
    }

    /**
     * Converts bytes from provided array "from" to ints in provided array "to"
     * up to the length of the smaller array. Function sets values in-place
     * but returns "to" for convenience.
     * @param from bytes to be converted to ints
     * @param to int array that will contain the converted values
     * @return param "to" for convenience
     */
    public static int[] bytes2Ints(byte[] from, int[] to) {
        int l = Math.min(from.length, to.length);
        for (int i = 0; i < l; i++) {
            to[i] = Byte.toUnsignedInt(from[i]);
        }
        return to;
    }
}
