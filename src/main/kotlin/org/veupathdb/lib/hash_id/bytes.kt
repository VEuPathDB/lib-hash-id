@file:Suppress("NOTHING_TO_INLINE")

package org.veupathdb.lib.hash_id

internal inline fun parseBytes(value: String) =
  when {
    value.length % 2 == 0 -> ByteArray(value.length / 2) { parsePair(value, it * 2) }
    else                  -> throw IllegalArgumentException()
  }

internal inline fun parsePair(value: String, index: Int) =
  ((parseChar(value[index]) shl 4) or parseChar(value[index + 1])).toByte()

internal inline fun parseChar(c: Char) =
  when (c) {
    in '0' .. '9' -> (c.code - 48)
    in 'a' .. 'f' -> (c.code - 87)
    in 'A' .. 'F' -> (c.code - 55)
    else          -> throw IllegalArgumentException()
  }

internal inline fun renderBytes(value: ByteArray, lowercase: Boolean = true): String {
  val raw = CharArray(value.size * 2)

  var rp = 0

  for (vp in 0..15) {
    when (value[vp]) {
      in 0..127 -> {
        raw[rp++] = renderByte(value[vp].toInt() / 16, lowercase)
        raw[rp++] = renderByte(value[vp].toInt() % 16, lowercase)
      }
      else      -> {
        raw[rp++] = renderByte(value[vp].toUByte().toInt() / 16, lowercase)
        raw[rp++] = renderByte(value[vp].toUByte().toInt() % 16, lowercase)
      }
    }

  }

  return raw.concatToString()
}

internal inline fun renderByte(b: Int, lowercase: Boolean = true) =
  if (lowercase)
    when {
      b < 0  -> throw IllegalArgumentException()
      b < 10 -> (b+48).toChar()
      b < 16 -> (b+87).toChar()
      else   -> throw IllegalArgumentException()
    }
  else
    when {
      b < 0  -> throw IllegalArgumentException()
      b < 10 -> (b+48).toChar()
      b < 16 -> (b+55).toChar()
      else   -> throw IllegalArgumentException()
    }
