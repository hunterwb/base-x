package com.hunterwb.basex;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("deprecation")
public class Coders {

    private static final String BASE128_TABLE;
    static {
        byte[] base128 = new byte[128];
        for (int i = 0; i < 128; i++) {
            base128[i] = (byte) i;
        }
        BASE128_TABLE = new String(base128, 0);
    }

    private static final List<BaseXCoder> CODERS128 = new ArrayList<BaseXCoder>(BASE128_TABLE.length());
    static {
        for (int i = 2; i <= BASE128_TABLE.length(); i++) {
            CODERS128.add(BaseXCoder.of(BASE128_TABLE.substring(0, i)));
        }
    }

    public static List<BaseXCoder> provider128() {
        return CODERS128;
    }

    private static final String BASE36_TABLE = "0123456789abcdefghijklmnopqrstuvwxyz";

    private static final List<BaseXCoder> CODERS36 = new ArrayList<BaseXCoder>(BASE128_TABLE.length());
    static {
        for (int i = 2; i <= BASE36_TABLE.length(); i++) {
            CODERS36.add(BaseXCoder.of(BASE36_TABLE.substring(0, i)));
        }
    }

    public static List<BaseXCoder> provider36() {
        return CODERS36;
    }
}
