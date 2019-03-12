package com.hunterwb.basex;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.Iterator;
import java.util.Random;

public class AsciiRadixCoderTest {

    @ParameterizedTest
    @MethodSource("com.hunterwb.basex.AsciiRadixCoders#all")
    void encodeZeros(AsciiRadixCoder coder) {
        for (int i = 3; i <= 512; i++) {
            byte[] dec0 = new byte[i];
            String enc0 = coder.encode(dec0);
            byte[] dec1 = coder.decode(enc0);
            Assertions.assertArrayEquals(dec0, dec1);
        }
    }

    @ParameterizedTest
    @ValueSource(strings = {"01","012","0123","ABC","0123456789"})
    void equals(String table) {
        AsciiRadixCoder a = AsciiRadixCoder.of(table);
        AsciiRadixCoder b = AsciiRadixCoder.of(table);
        Assertions.assertEquals(a, b);
        Assertions.assertEquals(a.hashCode(), b.hashCode());
    }

    @ParameterizedTest
    @CsvSource({
            "01, 012",
            "012, 0123",
            "ABC, 012",
            "01, 10"
    })
    void notEquals(String table1, String table2) {
        AsciiRadixCoder a = AsciiRadixCoder.of(table1);
        AsciiRadixCoder b = AsciiRadixCoder.of(table2);
        Assertions.assertNotEquals(a, b);
    }

    @Test
    void invertRandom() {
        Random random = new Random(4);
        for (AsciiRadixCoder coder : AsciiRadixCoders.all()) {
            for (int i = 0; i < 10; i++) {
                byte[] dec0 = new byte[2 + random.nextInt(500)];
                random.nextBytes(dec0);
                invert(coder, dec0);
            }
        }
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

    @ParameterizedTest
    @MethodSource("com.hunterwb.basex.AsciiRadixCoders#all")
    void invertZero(AsciiRadixCoder coder) {
        invert(coder, Bytes.EMPTY);
    }

    @ParameterizedTest
    @MethodSource("com.hunterwb.basex.AsciiRadixCoders#all")
    void invertOne(AsciiRadixCoder coder) {
        Iterator<byte[]> in = Bytes.allLength1();
        while (in.hasNext()) {
            invert(coder, in.next());
        }
    }

    @ParameterizedTest
    @MethodSource("com.hunterwb.basex.AsciiRadixCoders#all")
    void invertTwo(AsciiRadixCoder coder) {
        Iterator<byte[]> in = Bytes.allLength2();
        while (in.hasNext()) {
            invert(coder, in.next());
        }
    }

    @Disabled
    @ParameterizedTest
    @MethodSource("com.hunterwb.basex.AsciiRadixCoders#all")
    void invertThree(AsciiRadixCoder coder) {
        Iterator<byte[]> in = Bytes.allLength3();
        while (in.hasNext()) {
            invert(coder, in.next());
        }
    }

    private void invert(AsciiRadixCoder coder, byte[] dec0) {
        String enc0 = coder.encode(dec0);
        byte[] dec1 = coder.decode(enc0);
        Assertions.assertArrayEquals(dec0, dec1);
    }

    @ParameterizedTest
    @MethodSource("com.hunterwb.basex.AsciiRadixCoders#ascii36")
    void jdkOne(AsciiRadixCoder coder) {
        Iterator<byte[]> in = Bytes.allLength1();
        while (in.hasNext()) {
            byte[] dec0 = in.next();
            String enc0 = coder.encode(dec0);
            Assertions.assertEquals(Integer.toString(dec0[0] & 0xFF, coder.base()), enc0);
            byte[] dec1 = coder.decode(enc0);
            Assertions.assertArrayEquals(dec0, dec1);
        }
    }

    @ParameterizedTest
    @MethodSource("com.hunterwb.basex.AsciiRadixCoders#ascii36")
    void jdkTwo(AsciiRadixCoder coder) {
        Iterator<byte[]> in = Bytes.allLength2();
        while (in.hasNext()) {
            byte[] dec0 = in.next();
            if (dec0[0] == 0) continue;
            int asInt = ((dec0[0] & 0xFF) << 8) | (dec0[1] & 0xFF);
            String enc0 = coder.encode(dec0);
            Assertions.assertEquals(Integer.toString(asInt, coder.base()), enc0);
            byte[] dec1 = coder.decode(enc0);
            Assertions.assertArrayEquals(dec0, dec1);
        }
    }

    @Disabled
    @ParameterizedTest
    @MethodSource("com.hunterwb.basex.AsciiRadixCoders#ascii36")
    void jdkThree(AsciiRadixCoder coder) {
        Iterator<byte[]> in = Bytes.allLength3();
        while (in.hasNext()) {
            byte[] dec0 = in.next();
            if (dec0[0] == 0) continue;
            int asInt = ((dec0[0] & 0xFF) << 16) | ((dec0[1] & 0xFF) << 8) | (dec0[2] & 0xFF);
            String enc0 = coder.encode(dec0);
            Assertions.assertEquals(Integer.toString(asInt, coder.base()), enc0);
            byte[] dec1 = coder.decode(enc0);
            Assertions.assertArrayEquals(dec0, dec1);
        }
    }
}
