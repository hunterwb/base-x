package com.hunterwb.basex;

import java.util.Arrays;

public abstract class RadixCoder<A> {

    protected final int base;

    protected final double encodeFactor;

    protected final double decodeFactor;

    RadixCoder(int base) {
        if (base < 2) throw new IllegalArgumentException("base must be >= 2");
        this.base = base;
        double logBase = Math.log(base);
        double logByte = Math.log(0x100);
        encodeFactor = logByte / logBase;
        decodeFactor = logBase / logByte;
    }

    public static RadixCoder<byte[]> u8(int base) {
        return new U8(base);
    }

    public static RadixCoder<short[]> u16(int base) {
        return new U16(base);
    }

    public final int base() {
        return base;
    }

    public abstract A encode(byte[] src);

    public abstract byte[] decode(A src);

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

    static final class U8 extends RadixCoder<byte[]> {

        U8(int base) {
            super(base);
            if (base > 0x100) throw new IllegalArgumentException("base must be <= 0x100)");
        }

        @Override public byte[] encode(byte[] src) {
            int zeroCount = leadingZeros(src);
            if (zeroCount == src.length) return new byte[src.length];
            int capacity = zeroCount + ceilMultiply(src.length - zeroCount, encodeFactor);
            byte[] dst = new byte[capacity];
            int j = capacity - 2;
            for (int i = zeroCount; i < src.length; i++) {
                int carry = src[i] & 0xFF;
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

        @Override public byte[] decode(byte[] src) {
            int zeroCount = leadingZeros(src);
            if (zeroCount == src.length) return new byte[src.length];
            int capacity = zeroCount + ceilMultiply(src.length - zeroCount, decodeFactor);
            byte[] dst = new byte[capacity];
            int j = capacity - 2;
            for (int i = zeroCount; i < src.length; i++) {
                int carry = src[i] & 0xFF;
                if (carry >= base) throw new IllegalArgumentException("elements must be < " + base);
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
            if (base > 0x10000) throw new IllegalArgumentException("base must be <= 0x10000)");
        }

        @Override public short[] encode(byte[] src) {
            int zeroCount = leadingZeros(src);
            if (zeroCount == src.length) return new short[src.length];
            int capacity = zeroCount + ceilMultiply(src.length - zeroCount, encodeFactor);
            short[] dst = new short[capacity];
            int j = capacity - 2;
            for (int i = zeroCount; i < src.length; i++) {
                int carry = src[i] & 0xFF;
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

        @Override public byte[] decode(short[] src) {
            int zeroCount = leadingZeros(src);
            if (zeroCount == src.length) return new byte[src.length];
            int capacity = zeroCount + ceilMultiply(src.length - zeroCount, decodeFactor);
            byte[] dst = new byte[capacity];
            int j = capacity - 2;
            for (int i = zeroCount; i < src.length; i++) {
                int carry = src[i] & 0xFFFF;
                if (carry >= base) throw new IllegalArgumentException("elements must be < " + base);
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
