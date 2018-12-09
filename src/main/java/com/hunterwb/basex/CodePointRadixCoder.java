package com.hunterwb.basex;

import java.util.Arrays;

final class CodePointRadixCoder extends RadixCoder<String> {

    private final int[] codePoints;

    private final int[] invKeys;

    private final int[] invValues;

    CodePointRadixCoder(String alphabet) {
        this(codePointArray(alphabet));
    }

    private CodePointRadixCoder(int[] codePoints) {
        super(codePoints.length);
        this.codePoints = codePoints;

        invKeys = codePoints.clone();
        Arrays.sort(invKeys);

        invValues = new int[codePoints.length];
        Arrays.fill(invValues, -1);
        for (int i = 0; i < codePoints.length; i++) {
            int cp = codePoints[i];
            int key = Arrays.binarySearch(invKeys, cp);
            if (invValues[key] != -1) throw new IllegalArgumentException("Repeated code point: " + cp);
            invValues[key] = i;
        }
    }

    private static int[] codePointArray(String s) {
        int count = s.codePointCount(0, s.length());
        int[] cps = new int[count];
        for (int i = 0, offset = 0, cp; i < count; i++, offset += Character.charCount(cp)) {
            cps[i] = cp = s.codePointAt(offset);
        }
        return cps;
    }

    @Override
    protected int[] toDigits(String s) {
        int count = s.codePointCount(0, s.length());
        int[] digits = new int[count];
        for (int i = 0, offset = 0, cp; i < count; i++, offset += Character.charCount(cp)) {
            cp = s.codePointAt(offset);
            int key = Arrays.binarySearch(invKeys, cp);
            if (key < 0) throw new IllegalArgumentException("Illegal code point: " + cp);
            digits[i] = invValues[key];
        }
        return digits;
    }

    @Override
    protected String fromDigits(int[] digits) {
        for (int i = 0; i < digits.length; i++) {
            digits[i] = codePoints[digits[i]];
        }
        return new String(digits, 0, digits.length);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof CodePointRadixCoder)) return false;
        CodePointRadixCoder other = (CodePointRadixCoder) o;
        return Arrays.equals(codePoints, other.codePoints);
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(codePoints);
    }

    @Override
    public String toString() {
        return "CodePointRadixCoder(" + new String(codePoints, 0, codePoints.length) + ')';
    }
}
