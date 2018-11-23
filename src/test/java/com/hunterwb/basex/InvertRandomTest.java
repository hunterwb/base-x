package com.hunterwb.basex;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.Random;

class InvertRandomTest {

    private static final Random random = new Random(3);

    @ParameterizedTest
    @MethodSource("com.hunterwb.basex.Coders#all")
    void test(RadixCoder coder) {
        byte[] dec0 = new byte[100];
        for (int i = 0; i < 100; i++) {
            random.nextBytes(dec0);
            String enc0 = coder.encode(dec0);
            byte[] dec1 = coder.decode(enc0);
            Assertions.assertArrayEquals(dec0, dec1);
        }
    }
}
