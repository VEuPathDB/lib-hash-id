package org.veupathdb.lib.hash_id

import java.io.BufferedInputStream
import java.io.InputStream
import java.security.MessageDigest

/**
 * 128-bit ID Represented as a 32 digit hash string.
 *
 * This value is safe to use in [Sets][Set] and [Maps][Map].
 *
 * @author Elizabeth Harper
 * @since  v1.0.0
 */
class HashID {

  private val rawBytes: ByteArray

  /**
   * The URL-safe, stringified form of this [HashID].
   *
   * This value will be a 32 digit hex string.
   */
  val string
    get() = renderBytes(rawBytes)

  /**
   * A copy of the backing byte array for this [HashID].
   */
  val bytes
    get() = rawBytes.copyOf()

  /**
   * Constructs a new [HashID] backed by a copy of the given input byte array.
   *
   * The input byte array must have a length of exactly 16 bytes.
   *
   * @param bytes Byte array to back the new [HashID].
   *
   * @throws IllegalArgumentException If the input byte array does not contain
   * exactly 16 bytes.
   * @throws NullPointerException If the input byte array is `null`.
   */
  constructor(bytes: ByteArray) {
    if (bytes.size != 16)
      throw IllegalArgumentException()

    rawBytes = bytes.copyOf()
  }

  /**
   * Constructs a new [HashID] backed by a byte array parsed from the given hex
   * string.
   *
   * If the given string is not a valid base16 string, an exception will be
   * thrown.
   *
   * If the given string is not exactly 32 characters in length, an exception
   * will be thrown.
   *
   * @param stringValue A 32 digit hex string to back this [HashID].
   *
   * @throws IllegalArgumentException If the input value is not a valid hex
   * string of exactly 32 characters.
   * @throws NullPointerException If the input value is `null`.
   */
  constructor(stringValue: String) {
    if (stringValue.length != 32)
      throw IllegalArgumentException()

    rawBytes = parseBytes(stringValue)
  }

  /**
   * The URL-safe, stringified form of this [HashID] optionally in uppercase.
   *
   * This value will be a 32 digit hex string.
   *
   * @param lowercase If `true` (the default value), the letter characters in
   * the returned string will be lowercase.  If `false`, the letter characters
   * in the returned string will be uppercase.
   */
  fun getString(lowercase: Boolean) {
    renderBytes(rawBytes, lowercase)
  }

  /**
   * Returns the stringified form of this [HashID].
   *
   * @return Stringified form of this [HashID].
   */
  override fun toString() =
    renderBytes(rawBytes)

  override fun equals(other: Any?) =
    when (other) {
      is HashID -> rawBytes.contentEquals(other.rawBytes)
      else      -> false
    }

  override fun hashCode() = rawBytes.contentHashCode()

  companion object {
    /**
     * Creates a new [HashID] instance wrapping the MD5 hash of the given value.
     *
     * @return The new [HashID].
     */
    @JvmStatic
    fun ofMD5(value: String): HashID {
      val digest = MessageDigest.getInstance("MD5")
      digest.update(value.toByteArray())
      return HashID(digest.digest())
    }

    /**
     * Creates a new [HashID] instance wrapping the MD5 hash of the given value.
     *
     * @return The new [HashID].
     */
    @JvmStatic
    @JvmOverloads
    fun ofMD5(value: InputStream, close: Boolean = false): HashID {
      val digest = MessageDigest.getInstance("MD5")

      val stream = if (value is BufferedInputStream) value else BufferedInputStream(value)
      val buffer = ByteArray(8192)

      if (close) {
        stream.use {
          while (true) {
            val red = it.read(buffer)

            digest.update(buffer, 0, red)

            if (red < buffer.size) {
              break
            }
          }
        }
      } else {
        while (true) {
          val red = stream.read(buffer)

          digest.update(buffer, 0, red)

          if (red < buffer.size) {
            break
          }
        }
      }

      return HashID(digest.digest())
    }

    /**
     * Creates a new [HashID] instance wrapping the MD5 hash of the given value.
     *
     * @return The new [HashID].
     */
    @JvmStatic
    fun ofMD5(value: Any) = ofMD5(value.toString())
  }
}
