package com.hunterwb.basex;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

class EqualsTest {

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
}
