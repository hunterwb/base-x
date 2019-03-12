package com.hunterwb.basex;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("deprecation")
public class Coders {

    private static final List<AsciiRadixCoder> ASCII36_CODERS = new ArrayList<AsciiRadixCoder>();
    static {
        String table = "0123456789abcdefghijklmnopqrstuvwxyz";
        for (int i = 2; i <= table.length(); i++) {
            ASCII36_CODERS.add(AsciiRadixCoder.of(table.substring(0, i)));
        }
    }

    public static List<AsciiRadixCoder> ascii36() {
        return ASCII36_CODERS;
    }

    public static AsciiRadixCoder withBase(int base) {
        byte[] cs = new byte[base];
        for (int i = 0; i < base; i++) {
            cs[i] = (byte) i;
        }
        return AsciiRadixCoder.of(new String(cs, 0));
    }

    private static final List<AsciiRadixCoder> ALL = new ArrayList<AsciiRadixCoder>();
    static {
        for (int i = 2; i <= 128; i++) {
            ALL.add(withBase(i));
        }
    }

    public static List<AsciiRadixCoder> all() {
        return ALL;
    }
}
