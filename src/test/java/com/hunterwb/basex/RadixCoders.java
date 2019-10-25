package com.hunterwb.basex;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public final class RadixCoders {

    private static final List<RadixCoder<byte[]>> U8 = new ArrayList<>();
    static {
        for (int i = 2; i <= 256; i++) {
            U8.add(RadixCoder.u8(i));
        }
    }

    public static List<RadixCoder<byte[]>> u8() {
        return U8;
    }

    private static final List<RadixCoder<short[]>> U16 = new ArrayList<>();
    static {
        Random r = new Random(1);
        for (int i = 2; i <= 0x10000; i += 1 + r.nextInt(200)) {
            U16.add(RadixCoder.u16(i));
        }
    }

    public static List<RadixCoder<short[]>> u16() {
        return U16;
    }

    private static final List<RadixCoder<?>> ALL = new ArrayList<>();
    static {
        ALL.addAll(u8());
        ALL.addAll(u16());
    }

    public static List<RadixCoder<?>> all() {
        return ALL;
    }
}
