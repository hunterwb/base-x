package com.hunterwb.basex;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

class OnlyZerosTest {

    @ParameterizedTest
    @MethodSource("com.hunterwb.basex.Coders#all")
    void test(AsciiRadixCoder coder) {
        for (int i = 3; i <= 512; i++) {
            byte[] dec0 = new byte[i];
            String enc0 = coder.encode(dec0);
            byte[] dec1 = coder.decode(enc0);
            Assertions.assertArrayEquals(dec0, dec1);
        }
    }
}