package org.veupathdb.lib.hash_id

import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.io.BufferedInputStream
import java.io.InputStream
import java.math.BigInteger

@DisplayName("HashID")
internal class HashIDTest {
  val hexStringsLower: Array<String> = arrayOf(
    "5ebdeac01df23c2610ada60e53f60769",
    "bddd92f99d5b19aa447e42d95726b3d0",
    "0bf105f7d71a26d15696cad4d218aafe",
    "8ef8d4496ceefe08eb3dd4c11a5280e0",
    "4ac96d3756a49de6129a394b7fea3c0e",
    "3bbfaace3b2fc985b4ee7bf0524fe938",
    "49146cc37420d29f4f91f54c83b9e39c",
    "52a004759bc7025202824355babc36e0",
    "e6828c459f561bcbc28c8fe98bcf095b",
    "71238ca5e7ae25a2fa6fa66887071d8a",
  )

  val hexStringsUpper: Array<String> = arrayOf(
    "5EBDEAC01DF23C2610ADA60E53F60769",
    "BDDD92F99D5B19AA447E42D95726B3D0",
    "0BF105F7D71A26D15696CAD4D218AAFE",
    "8EF8D4496CEEFE08EB3DD4C11A5280E0",
    "4AC96D3756A49DE6129A394B7FEA3C0E",
    "3BBFAACE3B2FC985B4EE7BF0524FE938",
    "49146CC37420D29F4F91F54C83B9E39C",
    "52A004759BC7025202824355BABC36E0",
    "E6828C459F561BCBC28C8FE98BCF095B",
    "71238CA5E7AE25A2FA6FA66887071D8A",
  )

  val testBytes: Array<ByteArray> = Array(hexStringsUpper.size) {
    val tmp = BigInteger(hexStringsUpper[it], 16).toByteArray()
    if (tmp.size == 17)
      tmp.copyOfRange(1, 17)
    else
      tmp
  }

  @Nested
  @DisplayName("init(String)")
  internal inner class StringConstructor {

    @Test
    @DisplayName("Correctly parses the input string into a byte array.")
    fun test1() {
      for (i in hexStringsLower.indices) {
        assertArrayEquals(testBytes[i], HashID(hexStringsLower[i]).bytes)
      }
    }

    @Nested
    @DisplayName("throws an IllegalArgumentException when")
    internal inner class ThrowsIAE {

      @Test
      @DisplayName("the input string length is not 32")
      fun `throws-iae-on-input-length-not-32`() {
        assertThrows<IllegalArgumentException> { HashID("FFFF") }
      }

      @Test
      @DisplayName("the input string is not a valid hex string")
      fun `throws-iae-on-non-hex-input`() {
        assertThrows<IllegalArgumentException> { HashID("Apples are the best kind of food") }
      }
    }
  }

  @Nested
  @DisplayName("init(ByteArray)")
  internal inner class ByteArrayConstructor {

    @Nested
    @DisplayName("returns a new HashID instance when")
    internal inner class ReturnsNewInstance {

      @Test
      @DisplayName("the input is a non-null array of exactly 16 bytes")
      fun `returns-new-instance`() {
        assertDoesNotThrow {
          hexStringsUpper.indices.forEach { assertEquals(hexStringsLower[it], HashID(testBytes[it]).string) }
        }
      }
    }
  }

  @Nested
  @DisplayName("::ofMD5(String)")
  internal inner class StringMD5 {

    @Test
    @DisplayName("returns a HashID wrapping the correct MD5 of the given string")
    fun test1() {
      val input = "I'm a banana"
      val md5   = "0af797fcfb5878029a003b65960d1d30"

      assertEquals(md5, HashID.ofMD5(input).toString())
    }
  }

  @Nested
  @DisplayName("::ofMD5(InputStream)")
  internal inner class InputStreamMD5 {

    @Test
    @DisplayName("returns a HashID wrapping the correct MD5 of the contents of the given input stream")
    fun test1() {
      val input  = "Bugger all".byteInputStream()
      val output = "7478d5b72648205d8585020deeb4b06e"

      assertEquals(output, HashID.ofMD5(input).string)
    }

    @Test
    @DisplayName("closes the input stream when close is set to true")
    fun test2() {
      val stream = "Bugger all".byteInputStream()
      val input  = mockk<BufferedInputStream> {
        every { close() } answers {}
        every { read(any()) } answers {
          val buf = arg(0) as ByteArray
          buf[0] = 66  // B
          buf[1] = 117 // u
          buf[2] = 103 // g
          buf[3] = 103 // g
          buf[4] = 101 // e
          buf[5] = 114 // r
          buf[6] = 32  //
          buf[7] = 97  // a
          buf[8] = 108 // l
          buf[9] = 108 // l

          10
        }
      }
      val output = "7478d5b72648205d8585020deeb4b06e"

      assertEquals(output, HashID.ofMD5(input, true).string)

      verify(atLeast = 1, atMost = 1) { input.close() }
    }
  }

  @Nested
  @DisplayName("::ofMD5(Any)")
  internal inner class ObjectMD5 {
    private inner class Derp {
      override fun toString() = "You plonker"
    }

    @Test
    @DisplayName("returns a HashID wrapping the correct MD5 of the toString value of the given object")
    fun test1() {
      val input = Derp()
      val output = "36db6453f801a4e5bc13e138e7f0ac9e"

      assertEquals(output, HashID.ofMD5(input).string)
    }
  }
}