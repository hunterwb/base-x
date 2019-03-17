package com.hunterwb.basex;

import java.util.Arrays;

@SuppressWarnings("deprecation")
public final class AsciiRadixCoder {

    private final byte[] chars;

    private final byte[] digits;

    private final RadixCoder<byte[]> byteCoder;

    private AsciiRadixCoder(String alphabet) {
        chars = new byte[alphabet.length()];
        digits = new byte[1 << 7];
        Arrays.fill(digits, (byte) -1);
        for (int i = 0; i < chars.length; i++) {
            byte c = checkAscii(alphabet.charAt(i));
            chars[i] = c;
            if (digits[c] != -1) throw new IllegalArgumentException("char '" + (char) c + "' is repeated in alphabet");
            digits[c] = (byte) i;
        }
        byteCoder = RadixCoder.bytes(chars.length);
    }

    public static AsciiRadixCoder of(String alphabet) {
        return new AsciiRadixCoder(alphabet);
    }

    public int base() {
        return chars.length;
    }

    public String alphabet() {
        return new String(chars, 0);
    }

    public String encode(byte[] src) {
        byte[] bs = byteCoder.encode(src);
        for (int i = 0; i < bs.length; i++) {
            bs[i] = chars[bs[i]];
        }
        return new String(bs, 0);
    }

    public byte[] decode(String src) {
        byte[] bs = new byte[src.length()];
        for (int i = 0; i < bs.length; i++) {
            byte c = checkAscii(src.charAt(i));
            byte digit = digits[c];
            if (digit == -1) throw new IllegalArgumentException("char '" + (char) c + "' is not present in alphabet");
            bs[i] = digit;
        }
        return byteCoder.decode(bs);
    }

    private static byte checkAscii(char c) {
        if (c >= 1 << 7) throw new IllegalArgumentException("char '" + c + "' is not ascii");
        return (byte) c;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof AsciiRadixCoder)) return false;
        AsciiRadixCoder other = (AsciiRadixCoder) o;
        return Arrays.equals(chars, other.chars);
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(chars);
    }

    @Override
    public String toString() {
        return "AsciiRadixCoder(" + alphabet() + ')';
    }
}
