package org.veupathdb.lib.hash_id

import java.io.InputStream

/**
 * 128-bit ID Represented as a 32 digit hash string.
 *
 * This value is safe to use in [Sets][Set] and [Maps][Map].
 *
 * @author Elizabeth Harper
 * @since  v2.0.0
 */
sealed interface HashID {

  /**
   * The URL-safe, stringified form of this [HashID].
   *
   * This value will be a 32 digit hex string.
   */
  val string: String

  /**
   * A copy of the backing byte array for this [HashID].
   *
   * This value will be 16 bytes in length.
   */
  val bytes: ByteArray

  companion object {

    /**
     * Creates a new [HashID] instance from the given hash string.
     *
     * The input string must be a valid hex string of exactly 32 characters.
     *
     * @param value Hash string.
     *
     * @return New [HashID] instance wrapping the given hash string.
     *
     * @throws IllegalArgumentException If the input string is not exactly 32
     * characters in length, or if the input string contains characters that
     * are not valid hex digits.
     */
    @JvmStatic
    fun ofHash(value: String): HashID {
      if (value.length != 32)
        throw IllegalArgumentException()

      return HashIDImpl(value, parseBytes(value))
    }


    /**
     * Creates a new [HashID] instance from the given byte array.
     *
     * The input array must be exactly 16 bytes in length.
     *
     * @param value Hash bytes.
     *
     * @param lowercase If set to `true`, the wrapped hash string will be all
     * lowercase hex digits.  If set to `false` the wrapped string will be all
     * uppercase hex digits.
     *
     * Defaults to `false`
     *
     * @return New [HashID] instance wrapping the given byte array.
     *
     * @throws IllegalArgumentException If the input array is not exactly 16
     * bytes in length.
     */
    @JvmStatic
    @JvmOverloads
    fun ofHash(value: ByteArray, lowercase: Boolean = false): HashID {
      if (value.size != 16)
        throw IllegalArgumentException()

      return HashIDImpl(renderBytes(value, lowercase), value)
    }

    /**
     * Creates a new [HashID] instance wrapping the MD5 hash of the given input
     * string.
     *
     * @param value String to hash.
     *
     * @param lowercase If set to `true`, the wrapped hash string will be all
     * lowercase hex digits.  If set to `false` the wrapped string will be all
     * uppercase hex digits.
     *
     * Defaults to `false`
     *
     * @return New [HashID] instance wrapping the MD5 hash of the given input
     * string.
     */
    @JvmStatic
    @JvmOverloads
    fun ofMD5(value: String, lowercase: Boolean = false): HashID =
      value.md5().let { HashIDImpl(renderBytes(it, lowercase), it) }


    /**
     * Creates a new [HashID] instance wrapping the MD5 hash of the contents of
     * the given [InputStream].
     *
     * @param value [InputStream] whose contents will be hashed.
     *
     * @param lowercase If set to `true`, the wrapped hash string will be all
     * lowercase hex digits.  If set to `false` the wrapped string will be all
     * uppercase hex digits.
     *
     * Defaults to `false`
     *
     * @param close Whether the given [InputStream] should be closed upon
     * completion of this method.
     *
     * @return New [HashID] instance wrapping the MD5 hash of the contents of
     * the given [InputStream].
     */
    @JvmStatic
    @JvmOverloads
    fun ofMD5(value: InputStream, lowercase: Boolean = false, close: Boolean = false): HashID =
      value.md5(close).let { HashIDImpl(renderBytes(it, lowercase), it) }


    /**
     * Creates a new [HashID] instance wrapping the MD5 hash of the
     * [Object.toString] value of the given object.
     *
     * This method is a convenience over `ofMD5(foo.toString())`.
     *
     * @param value Object to hash.
     *
     * @param lowercase If set to `true`, the wrapped hash string will be all
     * lowercase hex digits.  If set to `false` the wrapped string will be all
     * uppercase hex digits.
     *
     * Defaults to `false`
     *
     * @return New [HashID] instance wrapping the MD5 hash of the `toString()`
     * value of the input object.
     */
    @JvmStatic
    @JvmOverloads
    fun ofMD5(value: Any, lowercase: Boolean = false): HashID =
      ofMD5(value.toString(), lowercase)
  }
}