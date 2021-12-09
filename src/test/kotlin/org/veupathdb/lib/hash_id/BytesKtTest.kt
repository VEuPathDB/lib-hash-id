package org.veupathdb.lib.hash_id

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

@DisplayName("ByteUtils")
internal class BytesKtTest {

  @Nested
  @DisplayName("::parseBytes(String)")
  internal inner class ParseBytes {

    @Test
    @DisplayName("correctly parses a valid hex string input")
    fun `correctly-parses-hex-string`() {
      val input = "000102030405080F101F203F407F80FF"

      assertArrayEquals(
        byteArrayOf(0, 1, 2, 3, 4,5,8,15,16,31,32,63,64,127,-128,-1),
        parseBytes(input)
      )
    }

    @Test
    @DisplayName("throws an IllegalArgumentException when the input string has an odd number of characters")
    fun `throws-on-odd-number-of-chars`() {
      val input = "000102030405080F101F203F407F80F"

      assertThrows<IllegalArgumentException> { parseBytes(input) }
    }
  }

  @Nested
  @DisplayName("::parsePair(String, Int)")
  internal inner class ParsePair {

    @Test
    @DisplayName("correctly parses the pairs of input characters into a byte value")
    fun `correctly-parses-pairs`() {
      val input = "000102030405080F101F203F407F80FF"

      assertEquals(0, parsePair(input, 0))
      assertEquals(1, parsePair(input, 2))
      assertEquals(2, parsePair(input, 4))
      assertEquals(3, parsePair(input, 6))
      assertEquals(4, parsePair(input, 8))
      assertEquals(5, parsePair(input, 10))
      assertEquals(8, parsePair(input, 12))
      assertEquals(15, parsePair(input, 14))
      assertEquals(16, parsePair(input, 16))
      assertEquals(31, parsePair(input, 18))
      assertEquals(32, parsePair(input, 20))
      assertEquals(63, parsePair(input, 22))
      assertEquals(64, parsePair(input, 24))
      assertEquals(127, parsePair(input, 26))
      assertEquals(-128, parsePair(input, 28))
      assertEquals(-1, parsePair(input, 30))
    }

  }

  @Nested
  @DisplayName("::parseChar(Char)")
  internal inner class ParseChar {
    @Test
    @DisplayName("correctly parses single hex characters into int values")
    fun `correctly-parses-char`() {
      val io = mapOf(
        Pair('0', 0),  Pair('1', 1),  Pair('2', 2),  Pair('3', 3),  Pair('4', 4),
        Pair('5', 5),  Pair('6', 6),  Pair('7', 7),  Pair('8', 8),  Pair('9', 9),
        Pair('A', 10), Pair('B', 11), Pair('C', 12), Pair('D', 13), Pair('E', 14),
        Pair('F', 15), Pair('a', 10), Pair('b', 11), Pair('c', 12), Pair('d', 13),
        Pair('e', 14), Pair('f', 15),
      )

      for ((i, o) in io) {
        assertEquals(o, parseChar(i))
      }
    }

    @Test
    @DisplayName("throws an IllegalArgumentException if the char is not a valid hex digit")
    fun `throws-on-invalid-char`() {
      assertThrows<IllegalArgumentException> { parseChar('G') }
    }
  }

  @Nested
  @DisplayName("::renderBytes(UByteArray)")
  internal inner class RenderBytes {
    @Test
    @DisplayName("correctly renders the given byte array as a string.")
    fun `correctly-renders-string`() {
      val input = byteArrayOf(0, 1, 2, 3, 4, 5, 8, 15, 16, 31, 32, 63, 64, 127, -128, -1)

      assertEquals("000102030405080f101f203f407f80ff", renderBytes(input))
    }
  }

  @Nested
  @DisplayName("::renderByte(Int)")
  internal inner class RenderByte {

    @Test
    @DisplayName("returns the correct alphanumeric character given an input in the set [0..16).")
    fun `returns-correct-char`() {
      val io = mapOf(
        Pair(0, '0'),
        Pair(1, '1'),
        Pair(2, '2'),
        Pair(3, '3'),
        Pair(4, '4'),
        Pair(5, '5'),
        Pair(6, '6'),
        Pair(7, '7'),
        Pair(8, '8'),
        Pair(9, '9'),
        Pair(10, 'a'),
        Pair(11, 'b'),
        Pair(12, 'c'),
        Pair(13, 'd'),
        Pair(14, 'e'),
        Pair(15, 'f'),
      )

      for ((i, o) in io) {
        assertEquals(o, renderByte(i))
      }
    }

    @Test
    @DisplayName("throws an IllegalArgumentException for inputs outside of the set [0..16).")
    fun throws() {
      assertThrows(IllegalArgumentException::class.java) { renderByte(-1) }
      assertThrows(IllegalArgumentException::class.java) { renderByte(16) }
    }
  }
}