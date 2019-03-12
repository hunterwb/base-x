package com.hunterwb.basex;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Random;

class InvertRandomTest {

    @Test
    void test() {
        Random random = new Random(4);
        for (AsciiRadixCoder coder : Coders.all()) {
            for (int i = 0; i < 10; i++) {
                byte[] dec0 = new byte[2 + random.nextInt(500)];
                random.nextBytes(dec0);
                String enc0 = coder.encode(dec0);
                byte[] dec1 = coder.decode(enc0);
                Assertions.assertArrayEquals(dec0, dec1);
            }
        }
    }
}
