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
        double logByte = Math.log(1 << Byte.SIZE);
        encodeFactor = logByte / logBase;
        decodeFactor = logBase / logByte;
    }

    public static RadixCoder<byte[]> of(byte base) {
        return new Bytes(base);
    }

    public static RadixCoder<short[]> of(short base) {
        return new Shorts(base);
    }

    public final int base() {
        return base;
    }

    public abstract A encode(byte[] src);

    public abstract byte[] decode(A src);

    @Override
    public final int hashCode() {
        return base;
    }

    static final class Bytes extends RadixCoder<byte[]> {

        Bytes(byte base) {
            super(base & 0xFF);
        }

        @Override
        public byte[] encode(byte[] src) {
            if (src.length == 0) return new byte[0];
            int zeroCount = leadingZeros(src);
            if (zeroCount == src.length) return new byte[src.length];
            int capacity = zeroCount + ceilMultiply(src.length - zeroCount, encodeFactor);
            byte[] dst = new byte[capacity];
            int i = capacity - 2;
            for (int b = zeroCount; b < src.length; b++) {
                int carry = src[b] & 0xFF;
                for (int j = capacity - 1; j > i; j--) {
                    carry += (dst[j] & 0xFF) << Byte.SIZE;
                    dst[j] = (byte) (carry % base);
                    carry /= base;
                }
                while (carry > 0) {
                    dst[i--] = (byte) (carry % base);
                    carry /= base;
                }
            }
            return drop(dst, i - zeroCount + 1);
        }

        @Override
        public byte[] decode(byte[] src) {
            if (src.length == 0) return new byte[0];
            int zeroCount = leadingZeros(src);
            if (zeroCount == src.length) return new byte[src.length];
            int capacity = zeroCount + ceilMultiply(src.length - zeroCount, decodeFactor);
            byte[] dst = new byte[capacity];
            int i = capacity - 2;
            for (int b = zeroCount; b < src.length; b++) {
                int carry = src[b] & 0xFF;
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
            return drop(dst, i - zeroCount + 1);
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof Bytes)) return false;
            Bytes other = (Bytes) o;
            return base == other.base;
        }

        @Override
        public String toString() {
            return "RadixCoder.Bytes(" + base + ')';
        }
    }

    static final class Shorts extends RadixCoder<short[]> {

        Shorts(short base) {
            super(base & 0xFFFF);
        }

        @Override
        public short[] encode(byte[] src) {
            if (src.length == 0) return new short[0];
            int zeroCount = leadingZeros(src);
            if (zeroCount == src.length) return new short[src.length];
            int capacity = zeroCount + ceilMultiply(src.length - zeroCount, encodeFactor);
            short[] dst = new short[capacity];
            int i = capacity - 2;
            for (int b = zeroCount; b < src.length; b++) {
                int carry = src[b] & 0xFF;
                for (int j = capacity - 1; j > i; j--) {
                    carry += (dst[j] & 0xFFFF) << Byte.SIZE;
                    dst[j] = (short) (carry % base);
                    carry /= base;
                }
                while (carry > 0) {
                    dst[i--] = (short) (carry % base);
                    carry /= base;
                }
            }
            return drop(dst, i - zeroCount + 1);
        }

        @Override
        public byte[] decode(short[] src) {
            if (src.length == 0) return new byte[0];
            int zeroCount = leadingZeros(src);
            if (zeroCount == src.length) return new byte[src.length];
            int capacity = zeroCount + ceilMultiply(src.length - zeroCount, decodeFactor);
            byte[] dst = new byte[capacity];
            int i = capacity - 2;
            for (int b = zeroCount; b < src.length; b ++) {
                int carry = src[b] & 0xFFFF;
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
            return drop(dst, i - zeroCount + 1);
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof Shorts)) return false;
            Shorts other = (Shorts) o;
            return base == other.base;
        }

        @Override
        public String toString() {
            return "RadixCoder.Shorts(" + base + ')';
        }
    }

    static int leadingZeros(byte[] a) {
        int zc = 0;
        for (; zc < a.length && a[zc] == 0; zc++);
        return zc;
    }

    static int leadingZeros(short[] a) {
        int zc = 0;
        for (; zc < a.length && a[zc] == 0; zc++);
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
