package com.hunterwb.basex;

import java.util.Arrays;

public abstract class RadixCoder<T> {

    private final int base;

    private final double encodeFactor;

    private final double decodeFactor;

    public RadixCoder(int base) {
        if (base < 2) throw new IllegalArgumentException("base must be >= 2");
        this.base = base;
        double logBase = Math.log(base);
        double logByte = Math.log(1 << Byte.SIZE);
        encodeFactor = logByte / logBase;
        decodeFactor = logBase / logByte;
    }

    public final int base() {
        return base;
    }

    abstract protected int[] toDigits(T t);

    abstract protected T fromDigits(int[] digits);

    public final T encode(byte[] src) {
        return fromDigits(encodeToDigits(src));
    }

    public final byte[] decode(T src) {
        return decodeFromDigits(toDigits(src));
    }

    private int[] encodeToDigits(byte[] src) {
        if (src.length == 0) return new int[0];

        int zeroCount = 0;
        for (; zeroCount < src.length && src[zeroCount] == (byte) 0; zeroCount++);
        if (zeroCount == src.length) return new int[src.length];

        int capacity = zeroCount + ceilMultiply(src.length - zeroCount, encodeFactor);
        int[] dst = new int[capacity];

        int i = capacity - 2;
        for (int b = zeroCount; b < src.length; b++) {
            int carry = src[b] & 0xFF;
            for (int j = capacity - 1; j > i; j--) {
                carry += dst[j] << Byte.SIZE;
                dst[j] = carry % base;
                carry /= base;
            }
            while (carry > 0) {
                dst[i--] = carry % base;
                carry /= base;
            }
        }

        int start = i - zeroCount + 1;
        return start == 0 ? dst : Arrays.copyOfRange(dst, start, dst.length);
    }

    private byte[] decodeFromDigits(int[] src) {
        if (src.length == 0) return new byte[0];

        int zeroCount = 0;
        for (; zeroCount < src.length && src[zeroCount] == 0; zeroCount++);
        if (zeroCount == src.length) return new byte[src.length];

        int capacity = zeroCount + ceilMultiply(src.length - zeroCount, decodeFactor);
        byte[] dst = new byte[capacity];

        int i = capacity - 2;
        for (int b = zeroCount; b < src.length; b ++) {
            int carry = src[b];
            for (int j = capacity - 1; j > i; j--) {
                carry += (dst[j] & 0xFF) * base;
                dst[j] = (byte) carry;
                carry >>>= Byte.SIZE;
            }
            while (carry > 0) {
                dst[i--] = (byte) carry;
                carry >>>= Byte.SIZE;
            }
        }

        int start = i - zeroCount + 1;
        return start == 0 ? dst : Arrays.copyOfRange(dst, start, dst.length);
    }

    private static int ceilMultiply(int a, double factor) {
        return (int) Math.ceil(a * factor);
    }

    public static RadixCoder<String> of(String codePointAlphabet) {
        return new CodePointRadixCoder(codePointAlphabet);
    }
}
