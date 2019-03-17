package com.hunterwb.basex;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

public class RadixCoderTest {

    @Test
    void zeroConstructor() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> RadixCoder.bytes(0));
        Assertions.assertThrows(IllegalArgumentException.class, () -> RadixCoder.shorts(0));
    }

    @Test
    void oneConstructor() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> RadixCoder.bytes(1));
        Assertions.assertThrows(IllegalArgumentException.class, () -> RadixCoder.shorts(1));
    }

    @Test
    void tooBigConstructor() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> RadixCoder.bytes(0x101));
        Assertions.assertThrows(IllegalArgumentException.class, () -> RadixCoder.shorts(0x10001));
    }

    @ParameterizedTest
    @MethodSource("com.hunterwb.basex.RadixCoders#bytes")
    void encodeZerosBytes(RadixCoder<byte[]> coder) {
        for (int i = 3; i <= 512; i++) {
            byte[] dec0 = new byte[i];
            byte[] enc0 = coder.encode(dec0);
            byte[] dec1 = coder.decode(enc0);
            Assertions.assertArrayEquals(dec0, dec1);
        }
    }

    @ParameterizedTest
    @ValueSource(ints = {2, 3, 200, 255})
    void equalsBytes(int base) {
        RadixCoder a = RadixCoder.bytes(base);
        RadixCoder b = RadixCoder.bytes(base);
        Assertions.assertEquals(a, b);
        Assertions.assertEquals(a.hashCode(), b.hashCode());
    }

    @ParameterizedTest
    @ValueSource(ints = {2, 3, 2000, 50000})
    void equalsShorts(int base) {
        RadixCoder a = RadixCoder.shorts(base);
        RadixCoder b = RadixCoder.shorts(base);
        Assertions.assertEquals(a, b);
        Assertions.assertEquals(a.hashCode(), b.hashCode());
    }

    @ParameterizedTest
    @CsvSource({
            "2, 3",
            "50, 100",
            "71, 72",
            "2, 254"
    })
    void notEqualsBytes(int base1, int base2) {
        RadixCoder a = RadixCoder.bytes(base1);
        RadixCoder b = RadixCoder.bytes(base2);
        Assertions.assertNotEquals(a, b);
    }

    @ParameterizedTest
    @CsvSource({
            "2, 3",
            "50, 100",
            "2121, 2122",
            "2, 30000"
    })
    void notEqualsShorts(int base1, int base2) {
        RadixCoder a = RadixCoder.shorts(base1);
        RadixCoder b = RadixCoder.shorts(base2);
        Assertions.assertNotEquals(a, b);
    }
}
