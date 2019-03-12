package com.hunterwb.basex;

import java.util.ArrayList;
import java.util.List;

public class RadixCoders {

    private static final List<RadixCoder<byte[]>> BYTES = new ArrayList<>();
    static {
        for (int i = 2; i <= 255; i++) {
            BYTES.add(RadixCoder.of((byte) i));
        }
    }

    public static List<RadixCoder<byte[]>> bytes() {
        return BYTES;
    }
}
