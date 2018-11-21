package com.hunterwb.basex;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.Iterator;

class JdkCompareTest {

    @ParameterizedTest
    @MethodSource("com.hunterwb.basex.Coders#provider36")
    void one(BaseXCoder coder) {
        Iterator<byte[]> in = Bytes.allLength1();
        while (in.hasNext()) {
            byte[] dec0 = in.next();
            String enc0 = coder.encodeToString(dec0);
            Assertions.assertEquals(Integer.toString(dec0[0] & 0xFF, oneBase), enc0);
            byte[] dec1 = coder.decode(enc0);
            Assertions.assertArrayEquals(dec0, dec1);
        }
        oneBase++;
    }
    private static int oneBase = 2;

    @ParameterizedTest
    @MethodSource("com.hunterwb.basex.Coders#provider36")
    void two(BaseXCoder coder) {
        Iterator<byte[]> in = Bytes.allLength2();
        while (in.hasNext()) {
            byte[] dec0 = in.next();
            if (dec0[0] == 0) continue;
            int asInt = ((dec0[0] & 0xFF) << 8) | (dec0[1] & 0xFF);
            String enc0 = coder.encodeToString(dec0);
            Assertions.assertEquals(Integer.toString(asInt, twoBase), enc0);
            byte[] dec1 = coder.decode(enc0);
            Assertions.assertArrayEquals(dec0, dec1);
        }
        twoBase++;
    }
    private static int twoBase = 2;

    @Disabled
    @ParameterizedTest
    @MethodSource("com.hunterwb.basex.Coders#provider36")
    void three(BaseXCoder coder) {
        Iterator<byte[]> in = Bytes.allLength3();
        while (in.hasNext()) {
            byte[] dec0 = in.next();
            if (dec0[0] == 0) continue;
            int asInt = ((dec0[0] & 0xFF) << 16) | ((dec0[1] & 0xFF) << 8) | (dec0[2] & 0xFF);
            String enc0 = coder.encodeToString(dec0);
            Assertions.assertEquals(Integer.toString(asInt, threeBase), enc0);
            byte[] dec1 = coder.decode(enc0);
            Assertions.assertArrayEquals(dec0, dec1);
        }
        threeBase++;
    }
    private static int threeBase = 2;
}
