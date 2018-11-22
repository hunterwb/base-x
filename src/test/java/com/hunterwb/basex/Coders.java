package com.hunterwb.basex;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("deprecation")
public class Coders {

    private static final String ASCII128_TABLE;
    static {
        byte[] base128 = new byte[128];
        for (int i = 0; i < 128; i++) {
            base128[i] = (byte) i;
        }
        ASCII128_TABLE = new String(base128, 0);
    }

    private static final List<RadixCoder> ASCII128_CODERS = new ArrayList<RadixCoder>(ASCII128_TABLE.length());
    static {
        for (int i = 2; i <= ASCII128_TABLE.length(); i++) {
            ASCII128_CODERS.add(RadixCoder.of(ASCII128_TABLE.substring(0, i)));
        }
    }

    public static List<RadixCoder> ascii128() {
        return ASCII128_CODERS;
    }

    private static final String ASCII36_TABLE = "0123456789abcdefghijklmnopqrstuvwxyz";

    private static final List<RadixCoder> ASCII36_CODERS = new ArrayList<RadixCoder>(ASCII128_TABLE.length());
    static {
        for (int i = 2; i <= ASCII36_TABLE.length(); i++) {
            ASCII36_CODERS.add(RadixCoder.of(ASCII36_TABLE.substring(0, i)));
        }
    }

    public static List<RadixCoder> ascii36() {
        return ASCII36_CODERS;
    }
}
