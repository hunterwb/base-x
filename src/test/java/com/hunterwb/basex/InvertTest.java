package com.hunterwb.basex;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.Iterator;

class InvertTest {

    @ParameterizedTest
    @MethodSource("com.hunterwb.basex.Coders#all")
    void zero(RadixCoder coder) {
        invert(coder, Bytes.EMPTY);
    }

    @ParameterizedTest
    @MethodSource("com.hunterwb.basex.Coders#all")
    void one(RadixCoder coder) {
        Iterator<byte[]> in = Bytes.allLength1();
        while (in.hasNext()) {
            invert(coder, in.next());
        }
    }

    @ParameterizedTest
    @MethodSource("com.hunterwb.basex.Coders#all")
    void two(RadixCoder coder) {
        Iterator<byte[]> in = Bytes.allLength2();
        while (in.hasNext()) {
            invert(coder, in.next());
        }
    }

    @Disabled
    @ParameterizedTest
    @MethodSource("com.hunterwb.basex.Coders#big")
    void three(RadixCoder coder) {
        Iterator<byte[]> in = Bytes.allLength3();
        while (in.hasNext()) {
            invert(coder, in.next());
        }
    }

    private void invert(RadixCoder coder, byte[] dec0) {
        String enc0 = coder.encode(dec0);
        byte[] dec1 = coder.decode(enc0);
        Assertions.assertArrayEquals(dec0, dec1);
    }
}
