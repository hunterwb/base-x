package com.hunterwb.basex;

import com.google.common.testing.EqualsTester;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.Random;

public final class RadixCoderTest {

    @Test
    void equality() {
        new EqualsTester()
                .addEqualityGroup(RadixCoder.u8(2), RadixCoder.u8(2))
                .addEqualityGroup(RadixCoder.u8(100), RadixCoder.u8(100))
                .addEqualityGroup(RadixCoder.u16(2), RadixCoder.u16(2))
                .addEqualityGroup(RadixCoder.u16(10000), RadixCoder.u16(10000))
                .testEquals();
    }

    @Test
    void zeroConstructor() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> RadixCoder.u8(0));
        Assertions.assertThrows(IllegalArgumentException.class, () -> RadixCoder.u16(0));
    }

    @Test
    void oneConstructor() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> RadixCoder.u8(1));
        Assertions.assertThrows(IllegalArgumentException.class, () -> RadixCoder.u16(1));
    }

    @Test
    void tooBigConstructor() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> RadixCoder.u8(0x101));
        Assertions.assertThrows(IllegalArgumentException.class, () -> RadixCoder.u16(0x10001));
    }

    @Test
    void decodeOutOfRange() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> RadixCoder.u8(64).decode(new byte[]{64}));
        Assertions.assertThrows(IllegalArgumentException.class, () -> RadixCoder.u8(200).decode(new byte[]{-1}));
        Assertions.assertThrows(IllegalArgumentException.class, () -> RadixCoder.u16(64).decode(new short[]{64}));
        Assertions.assertThrows(IllegalArgumentException.class, () -> RadixCoder.u16(30000).decode(new short[]{-1}));
    }

    private <N> void invert(RadixCoder<N> coder, byte[] bytes) {
        Assertions.assertArrayEquals(bytes, coder.decode(coder.encode(bytes)));
    }

    @ParameterizedTest
    @MethodSource("com.hunterwb.basex.RadixCoders#all")
    void invertZeroFilled(RadixCoder<?> coder) {
        for (int i = 0; i <= 65; i++) {
            invert(coder, new byte[i]);
        }
    }

    @Test
    void invertRandom() {
        Random r = new Random(1);
        for (RadixCoder<?> coder : RadixCoders.all()) {
            for (int i = 0; i < 5; i++) {
                invert(coder, Bytes.random(r, 2 + r.nextInt(300)));
            }
        }
    }

    @ParameterizedTest
    @MethodSource("com.hunterwb.basex.RadixCoders#all")
    void invertAllLength1(RadixCoder<?> coder) {
        for (byte[] b : Bytes.allLength1()) {
            invert(coder, b);
        }
    }

    @ParameterizedTest
    @MethodSource("com.hunterwb.basex.RadixCoders#all")
    void invertAllLength2(RadixCoder<?> coder) {
        for (byte[] b : Bytes.allLength2()) {
            invert(coder, b);
        }
    }
}
