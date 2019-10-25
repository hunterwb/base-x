package com.hunterwb.basex;

import java.util.Arrays;

public abstract class RadixCoder<N> {

    public static final int BASE_MIN = 2;

    public static final int BASE_MAX_U8 = 0x100;

    public static final int BASE_MAX_U16 = 0x10000;

    protected final int base;

    protected final double encodeFactor;

    protected final double decodeFactor;

    RadixCoder(int base) {
        if (base < BASE_MIN) throw new IllegalArgumentException("base must be >= " + BASE_MIN);
        this.base = base;
        double logBase = Math.log(base);
        double logByte = Math.log(BASE_MAX_U8);
        encodeFactor = logByte / logBase;
        decodeFactor = logBase / logByte;
    }

    /**
     * @throws IllegalArgumentException if {@code base} is less than {@value #BASE_MIN} or greater than {@value #BASE_MAX_U8}
     */
    public static RadixCoder<byte[]> u8(int base) {
        return new U8(base);
    }

    /**
     * @throws IllegalArgumentException if {@code base} is less than {@value #BASE_MIN} or greater than {@value #BASE_MAX_U16}
     */
    public static RadixCoder<short[]> u16(int base) {
        return new U16(base);
    }

    public final int base() {
        return base;
    }

    public abstract N encode(byte[] bytes);

    /**
     * @throws IllegalArgumentException if {@code n} contains any digits greater than or equal to {@link #base()}
     */
    public abstract byte[] decode(N n);

    @Override public final int hashCode() {
        return base;
    }

    @Override public final boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        return base == ((RadixCoder) obj).base;
    }

    @Override public final String toString() {
        return getClass().getName() + '(' + base + ')';
    }

    final void checkBaseMax(int max) {
        if (base > max) throw new IllegalArgumentException("base must be <= " + max);
    }

    final void checkDigitBase(int digit) {
        if (digit >= base) throw new IllegalArgumentException("digit must be < " + base);
    }

    static final class U8 extends RadixCoder<byte[]> {

        U8(int base) {
            super(base);
            checkBaseMax(BASE_MAX_U8);
        }

        @Override public byte[] encode(byte[] bytes) {
            int zeroCount = leadingZeros(bytes);
            if (zeroCount == bytes.length) return new byte[bytes.length];
            int capacity = zeroCount + ceilMultiply(bytes.length - zeroCount, encodeFactor);
            byte[] dst = new byte[capacity];
            int j = capacity - 2;
            for (int i = zeroCount; i < bytes.length; i++) {
                int carry = bytes[i] & 0xFF;
                for (int k = capacity - 1; k > j; k--) {
                    carry += (dst[k] & 0xFF) << Byte.SIZE;
                    dst[k] = (byte) (carry % base);
                    carry /= base;
                }
                while (carry > 0) {
                    dst[j--] = (byte) (carry % base);
                    carry /= base;
                }
            }
            return drop(dst, j - zeroCount + 1);
        }

        @Override public byte[] decode(byte[] n) {
            int zeroCount = leadingZeros(n);
            if (zeroCount == n.length) return new byte[n.length];
            int capacity = zeroCount + ceilMultiply(n.length - zeroCount, decodeFactor);
            byte[] dst = new byte[capacity];
            int j = capacity - 2;
            for (int i = zeroCount; i < n.length; i++) {
                int carry = n[i] & 0xFF;
                checkDigitBase(carry);
                for (int k = capacity - 1; k > j; k--) {
                    carry += (dst[k] & 0xFF) * base;
                    dst[k] = (byte) carry;
                    carry >>>= Byte.SIZE;
                }
                while (carry > 0) {
                    dst[j--] = (byte) carry;
                    carry >>>= Byte.SIZE;
                }
            }
            return drop(dst, j - zeroCount + 1);
        }
    }

    static final class U16 extends RadixCoder<short[]> {

        U16(int base) {
            super(base);
            checkBaseMax(BASE_MAX_U16);
        }

        @Override public short[] encode(byte[] bytes) {
            int zeroCount = leadingZeros(bytes);
            if (zeroCount == bytes.length) return new short[bytes.length];
            int capacity = zeroCount + ceilMultiply(bytes.length - zeroCount, encodeFactor);
            short[] dst = new short[capacity];
            int j = capacity - 2;
            for (int i = zeroCount; i < bytes.length; i++) {
                int carry = bytes[i] & 0xFF;
                for (int k = capacity - 1; k > j; k--) {
                    carry += (dst[k] & 0xFFFF) << Byte.SIZE;
                    dst[k] = (short) (carry % base);
                    carry /= base;
                }
                while (carry > 0) {
                    dst[j--] = (short) (carry % base);
                    carry /= base;
                }
            }
            return drop(dst, j - zeroCount + 1);
        }

        @Override public byte[] decode(short[] n) {
            int zeroCount = leadingZeros(n);
            if (zeroCount == n.length) return new byte[n.length];
            int capacity = zeroCount + ceilMultiply(n.length - zeroCount, decodeFactor);
            byte[] dst = new byte[capacity];
            int j = capacity - 2;
            for (int i = zeroCount; i < n.length; i++) {
                int carry = n[i] & 0xFFFF;
                checkDigitBase(carry);
                for (int k = capacity - 1; k > j; k--) {
                    carry += (dst[k] & 0xFF) * base;
                    dst[k] = (byte) carry;
                    carry >>>= Byte.SIZE;
                }
                while (carry > 0) {
                    dst[j--] = (byte) carry;
                    carry >>>= Byte.SIZE;
                }
            }
            return drop(dst, j - zeroCount + 1);
        }
    }

    static int leadingZeros(byte[] a) {
        int zc = 0;
        while (zc < a.length && a[zc] == 0) zc++;
        return zc;
    }

    static int leadingZeros(short[] a) {
        int zc = 0;
        while (zc < a.length && a[zc] == 0) zc++;
        return zc;
    }

    static byte[] drop(byte[] a, int count) {
        return count == 0 ? a : Arrays.copyOfRange(a, count, a.length);
    }

    static short[] drop(short[] a, int count) {
        return count == 0 ? a : Arrays.copyOfRange(a, count, a.length);
    }

    static int ceilMultiply(int n, double f) {
        return (int) Math.ceil(n * f);
    }
}
