# base-x

[![Build Status](https://img.shields.io/circleci/project/github/hunterwb/base-x.svg)](https://circleci.com/gh/hunterwb/base-x)

Encodes a byte array by treating it as a single large unsigned
integer and performing long division to convert it to its representation in the new base.
Every leading 0 byte in the array (not significant when treated as a number)
is encoded as the first character of the new base's alphabet.
This allows conversion to any base but is not RFC3548 or standard base 16/32/64 compliant.

## Example

```java
AsciiRadixCoder coder = AsciiRadixCoder.of("123456789ABCDEFGHJKLMNPQRSTUVWXYZabcdefghijkmnopqrstuvwxyz"); // base 58

byte[] decoded = coder.decode("5Kd3NBUAdUnhyzenEwVLy9pBKxSwXvE9FMPyR4UKZvpe6E3AgLr");
// decoded : [-128, -19, -37, -36, 17, 104, -15, -38, -22, -37, -45, -28, 76, 30, 63, -113, 90, 40, 76, 32, 41, -9, -118, -46, 106, -7, -123, -125, -92, -103, -34, 91, 25, 19, -92, -8, 99]

String encoded = coder.encode(decoded);
// encoded : 5Kd3NBUAdUnhyzenEwVLy9pBKxSwXvE9FMPyR4UKZvpe6E3AgLr
```