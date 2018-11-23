package com.hunterwb.basex;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("deprecation")
public class Coders {

    private static final List<RadixCoder> ASCII128_CODERS = new ArrayList<RadixCoder>();
    static {
        byte[] base128 = new byte[128];
        for (int i = 0; i < 128; i++) {
            base128[i] = (byte) i;
        }
        String table = new String(base128, 0);
        for (int i = 2; i <= table.length(); i++) {
            ASCII128_CODERS.add(RadixCoder.of(table.substring(0, i)));
        }
    }

    public static List<RadixCoder> ascii128() {
        return ASCII128_CODERS;
    }

    private static final List<RadixCoder> ASCII36_CODERS = new ArrayList<RadixCoder>();
    static {
        String table = "0123456789abcdefghijklmnopqrstuvwxyz";
        for (int i = 2; i <= table.length(); i++) {
            ASCII36_CODERS.add(RadixCoder.of(table.substring(0, i)));
        }
    }

    public static List<RadixCoder> ascii36() {
        return ASCII36_CODERS;
    }

    static RadixCoder withBase(int base) {
        int[] cps = new int[base];
        int cp = 0x4e00;
        for (int i = 0; i < base; i++) {
            cps[i] = cp++;
        }
        String table = new String(cps, 0, cps.length);
        return RadixCoder.of(table);
    }

    private static final List<RadixCoder> BIG_CODERS = new ArrayList<RadixCoder>();
    static {
        int[] bases = new int[] { 127,128,200,255,256,257,500,510,511,512,512,999,1000,1001,1023,1024,1025,2000,3000,4000,5000 };
        for (int base : bases) {
            BIG_CODERS.add(withBase(base));
        }
    }

    public static List<RadixCoder> big() {
        return BIG_CODERS;
    }

    private static List<RadixCoder> ALL = new ArrayList<RadixCoder>(ASCII128_CODERS);
    static {
        ALL.addAll(BIG_CODERS);
    }

    public static List<RadixCoder> all() {
        return ALL;
    }
}
