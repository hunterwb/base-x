package com.hunterwb.basex;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.Random;

public class InvertRandomTest {

    private static final Random random = new Random(3);

    @ParameterizedTest
    @MethodSource("com.hunterwb.basex.Coders#provider128")
    void test(BaseXCoder coder) {
        for (int i = 0; i < 50000; i++) {
            byte[] dec0 = new byte[5];
            random.nextBytes(dec0);
            byte[] enc0 = coder.encode(dec0);
            byte[] dec1 = coder.decode(enc0);
            Assertions.assertArrayEquals(dec0, dec1);
        }
    }
}
