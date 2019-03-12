package com.hunterwb.basex;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class DecodeErrorTest {

    @Test
    void invalidCharacter() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> AsciiRadixCoder.of("01").decode("011100X01101"));
    }
}
