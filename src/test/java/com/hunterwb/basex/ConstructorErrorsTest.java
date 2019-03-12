package com.hunterwb.basex;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class ConstructorErrorsTest {

    @Test
    void empty() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> AsciiRadixCoder.of(""));
    }

    @Test
    void oneCharacter() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> AsciiRadixCoder.of("A"));
    }

    @Test
    void repeatedCharacter() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> AsciiRadixCoder.of("01234567890"));
    }
}
