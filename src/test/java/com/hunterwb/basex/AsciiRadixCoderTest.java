package com.hunterwb.basex;

import com.google.common.testing.EqualsTester;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.Random;

public final class AsciiRadixCoderTest {

    @Test
    void equality() {
        new EqualsTester()
                .addEqualityGroup(AsciiRadixCoder.of("12"), AsciiRadixCoder.of("12"))
                .addEqualityGroup(AsciiRadixCoder.of("21"), AsciiRadixCoder.of("21"))
                .addEqualityGroup(AsciiRadixCoder.of("123"), AsciiRadixCoder.of("123"))
                .testEquals();
    }

    @Test
    void emptyConstructor() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> AsciiRadixCoder.of(""));
    }

    @Test
    void oneCharacterConstructor() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> AsciiRadixCoder.of("A"));
    }

    @Test
    void repeatedCharacterConstructor() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> AsciiRadixCoder.of("01234567890"));
    }

    @Test
    void invalidCharacterDecode() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> AsciiRadixCoder.of("01").decode("011100X01101"));
    }

    private void invert(AsciiRadixCoder coder, byte[] bytes) {
        Assertions.assertArrayEquals(bytes, coder.decode(coder.encode(bytes)));
    }

    @ParameterizedTest
    @MethodSource("com.hunterwb.basex.AsciiRadixCoders#all")
    void invertZeroFilled(AsciiRadixCoder coder) {
        for (int i = 0; i <= 65; i++) {
            invert(coder, new byte[i]);
        }
    }

    @Test
    void invertRandom() {
        Random r = new Random(1);
        for (AsciiRadixCoder coder : AsciiRadixCoders.all()) {
            for (int i = 0; i < 10; i++) {
                invert(coder, Bytes.random(r, 2 + r.nextInt(500)));
            }
        }
    }

    @ParameterizedTest
    @MethodSource("com.hunterwb.basex.AsciiRadixCoders#all")
    void invertAllLength1(AsciiRadixCoder coder) {
        for (byte[] b : Bytes.allLength1()) {
            invert(coder, b);
        }
    }

    @ParameterizedTest
    @MethodSource("com.hunterwb.basex.AsciiRadixCoders#all")
    void invertAllLength2(AsciiRadixCoder coder) {
        for (byte[] b : Bytes.allLength2()) {
            invert(coder, b);
        }
    }

    @ParameterizedTest
    @MethodSource("com.hunterwb.basex.AsciiRadixCoders#ascii36")
    void jdkOne(AsciiRadixCoder coder) {
        for (byte[] b : Bytes.allLength1()) {
            String enc0 = coder.encode(b);
            Assertions.assertEquals(Integer.toString(b[0] & 0xFF, coder.base()), enc0);
            byte[] dec1 = coder.decode(enc0);
            Assertions.assertArrayEquals(b, dec1);
        }
    }

    @ParameterizedTest
    @MethodSource("com.hunterwb.basex.AsciiRadixCoders#ascii36")
    void jdkTwo(AsciiRadixCoder coder) {
        for (byte[] b : Bytes.allLength2()) {
            if (b[0] == 0) continue;
            int asInt = ((b[0] & 0xFF) << 8) | (b[1] & 0xFF);
            String enc0 = coder.encode(b);
            Assertions.assertEquals(Integer.toString(asInt, coder.base()), enc0);
            byte[] dec1 = coder.decode(enc0);
            Assertions.assertArrayEquals(b, dec1);
        }
    }

    @Disabled
    @ParameterizedTest
    @MethodSource("com.hunterwb.basex.AsciiRadixCoders#ascii36")
    void jdkThree(AsciiRadixCoder coder) {
        for (byte[] b : Bytes.allLength3()) {
            if (b[0] == 0) continue;
            int asInt = ((b[0] & 0xFF) << 16) | ((b[1] & 0xFF) << 8) | (b[2] & 0xFF);
            String enc0 = coder.encode(b);
            Assertions.assertEquals(Integer.toString(asInt, coder.base()), enc0);
            byte[] dec1 = coder.decode(enc0);
            Assertions.assertArrayEquals(b, dec1);
        }
    }
}
