package com.hunterwb.basex;

import java.nio.ByteBuffer;
import java.util.Iterator;

public class Bytes {

    public static final byte[] EMPTY = new byte[0];

    public static Iterator<byte[]> allLength1() {
        return new Iterator<byte[]>() {

            private final ByteBuffer buf = ByteBuffer.allocate(1).put(0, Byte.MIN_VALUE);

            @Override
            public boolean hasNext() {
                return buf.get(0) != Byte.MAX_VALUE;
            }

            @Override
            public byte[] next() {
                return buf.put(0, (byte) (buf.get(0) + 1)).array();
            }

            @Override public void remove() {}
        };
    }

    public static Iterator<byte[]> allLength2() {
        return new Iterator<byte[]>() {

            private final ByteBuffer buf = ByteBuffer.allocate(2).putShort(0, Short.MIN_VALUE);

            @Override
            public boolean hasNext() {
                return buf.getShort(0) != Short.MAX_VALUE;
            }

            @Override
            public byte[] next() {
                return buf.putShort(0, (short) (buf.getShort(0) + 1)).array();
            }

            @Override public void remove() {}
        };
    }

    public static Iterator<byte[]> allLength3() {
        return new Iterator<byte[]>() {

            private final ByteBuffer buf = ByteBuffer.allocate(3);
            {
                putMedium(0x800000);
            }

            private int getMedium() {
                return (buf.getShort(0) << 8) | (buf.get(2) & 0xFF);
            }

            private ByteBuffer putMedium(int value) {
                return buf.putShort(0, (short) (value >> 8)).put(2, (byte) value);
            }

            @Override
            public boolean hasNext() {
                return getMedium() != 0x7FFFFF;
            }

            @Override
            public byte[] next() {
                return putMedium(getMedium() + 1).array();
            }

            @Override public void remove() {}
        };
    }
}
