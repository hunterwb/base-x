package com.hunterwb.basex;

import java.util.Arrays;

public abstract class RadixCoder {

    public static RadixCoder of(String table) {
        return isAscii(table) ? new Ascii(table) : new Unicode(table);
    }

    abstract int base();

    abstract String encode(byte[] src);

    abstract byte[] decode(String str);

    static final class Ascii extends RadixCoder {

        private final int base;

        private final byte[] enc;

        private final byte[] dec;

        private final double decodeFactor;

        private final double encodeFactor;

        Ascii(String table) {
            base = table.length();
            if (base < 2) throw new IllegalArgumentException("Table must contain at least 2 characters");

            enc = new byte[base];
            dec = new byte[1 << 7];
            Arrays.fill(dec, (byte) -1);
            for (int i = 0; i < base; i++) {
                char c = table.charAt(i);
                if (dec[c] != -1) throw new IllegalArgumentException("Character repeated in table: " + c);
                enc[i] = (byte) c;
                dec[c] = (byte) i;
            }

            double logBase = Math.log(base);
            double logByte = Math.log(1 << Byte.SIZE);
            decodeFactor = logBase / logByte;
            encodeFactor = logByte / logBase;
        }

        @Override
        int base() {
            return base;
        }

        @Override
        public String encode(byte[] src) {
            if (src.length == 0) return "";

            int zeroCount = leadCount(src, (byte) 0);
            if (zeroCount == src.length) return repeat(enc[0], zeroCount);

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
            return encToString(dst, i - zeroCount + 1);
        }

        @Override
        public byte[] decode(String src) {
            if (src.isEmpty()) return new byte[0];
            if (!isAscii(src)) throw new IllegalArgumentException();

            int zeroCount = leadCount(src, enc[0]);
            if (zeroCount == src.length()) return new byte[src.length()];

            int capacity = zeroCount + ceilMultiply(src.length() - zeroCount, decodeFactor);
            byte[] dst = new byte[capacity];

            int i = capacity - 2;
            for (int b = zeroCount; b < src.length(); b++) {
                int carry = dec((byte) src.charAt(b)) & 0xFF;
                for (int j = capacity - 1; j > i; j--) {
                    carry += (dst[j] & 0xFF) * base;
                    dst[j] = (byte) carry;
                    carry >>= Byte.SIZE;
                }
                while (carry > 0) {
                    dst[i--] = (byte) carry;
                    carry >>= Byte.SIZE;
                }
            }
            return drop(dst, i - zeroCount + 1);
        }

        @SuppressWarnings("deprecation")
        private String encToString(byte[] bs, int start) {
            for (int i = start; i < bs.length; i++) {
                bs[i] = enc[bs[i]];
            }
            return new String(bs, 0, start, bs.length - start);
        }

        private byte dec(byte b) {
            byte v = dec[b];
            if (v == -1) throw new IllegalArgumentException("Illegal character: " + (b & 0xFF));
            return v;
        }

        private static int leadCount(String s, byte b) {
            int count = 0;
            for (; count < s.length() && (byte) s.charAt(count) == b; count++);
            return count;
        }

        @SuppressWarnings("deprecation")
        private static String repeat(byte value, int count) {
            byte[] bs = new byte[count];
            Arrays.fill(bs, value);
            return new String(bs, 0);
        }
    }

    static final class Unicode extends RadixCoder {

        private final int base;

        private final int[] enc;

        private final int[] decKeys;

        private final int[] decValues;

        private final double decodeFactor;

        private final double encodeFactor;

        private Unicode(String table) {
            base = table.codePointCount(0, table.length());
            if (base < 2) throw new IllegalArgumentException("Table must contain at least 2 characters");

            enc = new int[base];
            int offset = 0;
            for (int i = 0; i < base; i++) {
                int codePoint = table.codePointAt(offset);
                enc[i] = codePoint;
                offset += Character.charCount(codePoint);
            }

            decKeys = enc.clone();
            Arrays.sort(decKeys);

            decValues = new int[base];
            for (int i = 0; i < base; i++) {
                int codePoint = enc[i];
                int key = Arrays.binarySearch(decKeys, codePoint);
                decValues[key] = i;
            }

            double logBase = Math.log(base);
            double logByte = Math.log(1 << Byte.SIZE);
            decodeFactor = logBase / logByte;
            encodeFactor = logByte / logBase;
        }

        @Override
        int base() {
            return base;
        }

        @Override
        public String encode(byte[] src) {
            if (src.length == 0) return "";

            int zeroCount = leadCount(src, (byte) 0);
            if (zeroCount == src.length) return repeat(enc[0], zeroCount);

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
            return encToString(dst, i - zeroCount + 1);
        }

        @Override
        public byte[] decode(String src) {
            if (src.isEmpty()) return new byte[0];

            int srcLength = src.codePointCount(0, src.length());
            int zeroOffset = leadCount(src, enc[0]);
            int zeroCount = zeroOffset / Character.charCount(enc[0]);
            if (zeroCount == srcLength) return new byte[srcLength];

            int capacity = zeroCount + ceilMultiply(srcLength - zeroCount, decodeFactor);
            byte[] dst = new byte[capacity];

            int i = capacity - 2;
            for (int b = zeroOffset; b < src.length();) {
                int codePoint = src.codePointAt(b);
                b += Character.charCount(codePoint);
                int carry = dec(codePoint);
                for (int j = capacity - 1; j > i; j--) {
                    carry += (dst[j] & 0xFF) * base;
                    dst[j] = (byte) carry;
                    carry >>= Byte.SIZE;
                }
                while (carry > 0) {
                    dst[i--] = (byte) carry;
                    carry >>= Byte.SIZE;
                }
            }
            return drop(dst, i - zeroCount + 1);
        }

        private String encToString(int[] bs, int start) {
            for (int i = start; i < bs.length; i++) {
                bs[i] = enc[bs[i]];
            }
            return new String(bs, start, bs.length - start);
        }

        private int dec(int codepoint) {
            int decKey = Arrays.binarySearch(decKeys, codepoint);
            if (decKey == -1) throw new IllegalArgumentException();
            return decValues[decKey];
        }

        private static int leadCount(String s, int b) {
            int offset = 0;
            while (offset < s.length()) {
                int codePoint = s.codePointAt(offset);
                if (codePoint != b) break;
                offset += Character.charCount(codePoint);
            }
            return offset;
        }

        private static String repeat(int codePoint, int count) {
            int[] bs = new int[count];
            Arrays.fill(bs, codePoint);
            return new String(bs, 0, bs.length);
        }
    }

    static boolean isAscii(String s) {
        for (int i = 0; i < s.length(); i++) {
            if (!isAscii(s.charAt(i))) return false;
        }
        return true;
    }

    static boolean isAscii(char c) {
        return c >>> 7 == 0;
    }

    static int ceilMultiply(int a, double factor) {
        return (int) Math.ceil(a * factor);
    }

    static byte[] drop(byte[] bs, int count) {
        return count == 0 ? bs : Arrays.copyOfRange(bs, count, bs.length);
    }

    static int leadCount(byte[] bs, byte value) {
        int count = 0;
        for (; count < bs.length && bs[count] == value; count++);
        return count;
    }

    public static void main(String[] args) {
        String table = "\uD83D\uDC7D\uD83D\uDE00";
        System.out.println(table);
        RadixCoder coder = RadixCoder.of(table);

        byte[] dec0 = new byte[] {0,0,4};
        System.out.println(Arrays.toString(dec0));
        String enc = coder.encode(dec0);
        System.out.println(enc);

        byte[] dec1 = coder.decode(enc);
        System.out.println(Arrays.toString(dec1));
    }
}
