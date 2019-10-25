package com.hunterwb.basex;

import com.google.common.testing.EqualsTester;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

public class RadixCoderTest {

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
}
