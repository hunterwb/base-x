package com.hunterwb.basex;

import java.util.Arrays;

public final class RadixCoder {

    private final int base;

    private final int[] enc;

    private final int[] decKeys;

    private final int[] decValues;

    private final double encodeFactor;

    private final double decodeFactor;

    public static RadixCoder of(String table) {
        return new RadixCoder(table);
    }

    private RadixCoder(String table) {
        enc = codePointArray(table);
        base = enc.length;
        if (base < 2) throw new IllegalArgumentException("Table must contain at least 2 characters");

        decKeys = enc.clone();
        Arrays.sort(decKeys);

        decValues = new int[base];
        Arrays.fill(decValues, -1);
        for (int i = 0; i < base; i++) {
            int codePoint = enc[i];
            int key = Arrays.binarySearch(decKeys, codePoint);
            if (decValues[key] != -1) throw new IllegalArgumentException("Character repeated in table: " + codePoint);
            decValues[key] = i;
        }

        double logBase = Math.log(base);
        double logByte = Math.log(1 << Byte.SIZE);
        encodeFactor = logByte / logBase;
        decodeFactor = logBase / logByte;
    }

    public int base() {
        return base;
    }

    public String table() {
        return new String(enc, 0, enc.length);
    }

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
                carry >>>= Byte.SIZE;
            }
            while (carry > 0) {
                dst[i--] = (byte) carry;
                carry >>>= Byte.SIZE;
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

    private int dec(int codePoint) {
        int decKey = Arrays.binarySearch(decKeys, codePoint);
        if (decKey == -1) throw new IllegalArgumentException();
        return decValues[decKey];
    }

    private static int leadCount(String s, int codePoint) {
        int offset = 0;
        while (offset < s.length()) {
            int cp = s.codePointAt(offset);
            if (cp != codePoint) break;
            offset += Character.charCount(cp);
        }
        return offset;
    }

    private static String repeat(int codePoint, int count) {
        int[] cps = new int[count];
        Arrays.fill(cps, codePoint);
        return new String(cps, 0, cps.length);
    }

    private static int ceilMultiply(int a, double factor) {
        return (int) Math.ceil(a * factor);
    }

    private static byte[] drop(byte[] bs, int count) {
        return count == 0 ? bs : Arrays.copyOfRange(bs, count, bs.length);
    }

    private static int leadCount(byte[] bs, byte value) {
        int count = 0;
        for (; count < bs.length && bs[count] == value; count++);
        return count;
    }

    private static int[] codePointArray(String s) {
        int count = s.codePointCount(0, s.length());
        int[] cps = new int[count];
        int offset = 0;
        for (int i = 0; i < count; i++) {
            int codePoint = s.codePointAt(offset);
            cps[i] = codePoint;
            offset += Character.charCount(codePoint);
        }
        return cps;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof RadixCoder)) return false;
        RadixCoder other = (RadixCoder) o;
        return Arrays.equals(enc, other.enc);
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(enc);
    }

    @Override
    public String toString() {
        return "RadixCoder(" + table() + ')';
    }
}
