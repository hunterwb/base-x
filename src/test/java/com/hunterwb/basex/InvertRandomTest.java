package com.hunterwb.basex;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Random;

class InvertRandomTest {

    @Test
    void small() {
        Random random = new Random(4);
        for (int i = 0; i < 100000; i++) {
            RadixCoder<String> coder = Coders.withBase(2 + random.nextInt(256));
            byte[] dec0 = new byte[2 + random.nextInt(64)];
            random.nextBytes(dec0);
            String enc0 = coder.encode(dec0);
            byte[] dec1 = coder.decode(enc0);
            Assertions.assertArrayEquals(dec0, dec1);
        }
    }

    @Test
    void big() {
        Random random = new Random(5);
        for (int i = 0; i < 200; i++) {
            RadixCoder<String> coder = Coders.withBase(2 + random.nextInt(256));
            byte[] dec0 = new byte[64 + random.nextInt(2000)];
            random.nextBytes(dec0);
            String enc0 = coder.encode(dec0);
            byte[] dec1 = coder.decode(enc0);
            Assertions.assertArrayEquals(dec0, dec1);
        }
    }
}
