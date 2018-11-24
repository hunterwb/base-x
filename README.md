# base-x

## Example

```java
RadixCoder coder = RadixCoder.of("123456789ABCDEFGHJKLMNPQRSTUVWXYZabcdefghijkmnopqrstuvwxyz");

byte[] decoded = coder.decode("5Kd3NBUAdUnhyzenEwVLy9pBKxSwXvE9FMPyR4UKZvpe6E3AgLr");
// decoded : [-128, -19, -37, -36, 17, 104, -15, -38, -22, -37, -45, -28, 76, 30, 63, -113, 90, 40, 76, 32, 41, -9, -118, -46, 106, -7, -123, -125, -92, -103, -34, 91, 25, 19, -92, -8, 99]

String encoded = coder.encode(decoded);
// encoded : 5Kd3NBUAdUnhyzenEwVLy9pBKxSwXvE9FMPyR4UKZvpe6E3AgLr
```