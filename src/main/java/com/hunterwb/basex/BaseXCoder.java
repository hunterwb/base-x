package com.hunterwb.basex;

import java.nio.charset.Charset;
import java.util.Arrays;

public final class BaseXCoder {

    private static final Charset ISO_8859_1 = Charset.forName("ISO-8859-1");

    private final int base;

    private final byte[] enc;

    private final byte[] dec;

    private final double decodeFactor;

    private final double encodeFactor;

    public static BaseXCoder of(String table) {
        return new BaseXCoder(table);
    }

    private BaseXCoder(String table) {
        if (table.length() < 2) throw new IllegalArgumentException("Table must contain at least 2 characters");
        enc = new byte[table.length()];
        dec = new byte[1 << 7];
        Arrays.fill(dec, (byte) -1);
        for (int i = 0; i < enc.length; i++) {
            char c = table.charAt(i);
            if (c > 0x7F) throw new IllegalArgumentException("Non ASCII character in table: " + c);
            if (dec[c] != -1) throw new IllegalArgumentException("Character repeated in table: " + c);
            enc[i] = (byte) c;
            dec[c] = (byte) i;
        }
        base = enc.length;
        double logBase = Math.log(base);
        double logByte = Math.log(1 << Byte.SIZE);
        decodeFactor = logBase / logByte;
        encodeFactor = logByte / logBase;
    }

    public byte[] encode(byte[] src) {
        if (src.length == 0) return src;

        int zeroCount = leadCount(src, (byte) 0);
        if (zeroCount == src.length) return filledArray(zeroCount, enc[0]);

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
        return encAll(drop(dst, i - zeroCount + 1));
    }

    @SuppressWarnings("deprecation")
    public String encodeToString(byte[] src) {
        return new String(encode(src), 0);
    }

    public byte[] decode(byte[] src) {
        if (src.length == 0) return src;

        int zeroCount = leadCount(src, enc[0]);
        if (zeroCount == src.length) return new byte[src.length];

        int capacity = zeroCount + ceilMultiply(src.length - zeroCount, decodeFactor);
        byte[] dst = new byte[capacity];

        int i = capacity - 2;
        for (int b = zeroCount; b < src.length; b++) {
            int carry = dec(src[b]) & 0xFF;
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

    public byte[] decode(String s) {
        return decode(s.getBytes(ISO_8859_1));
    }

    private byte[] encAll(byte[] bs) {
        for (int i = 0; i < bs.length; i++) {
            bs[i] = enc[bs[i]];
        }
        return bs;
    }

    private byte dec(byte b) {
        byte v = dec[b];
        if (v == -1) throw new IllegalArgumentException("Illegal character: " + (b & 0xFF));
        return v;
    }

    private static int leadCount(byte[] bs, byte b) {
        int count = 0;
        for (; count < bs.length && bs[count] == b; count++);
        return count;
    }

    private static byte[] drop(byte[] bs, int n) {
        return n == 0 ? bs : Arrays.copyOfRange(bs, n, bs.length);
    }

    private static byte[] filledArray(int len, byte value) {
        byte[] bs = new byte[len];
        Arrays.fill(bs, value);
        return bs;
    }

    private static int ceilMultiply(int a, double factor) {
        return (int) Math.ceil(a * factor);
    }
}
