package org.veupathdb.lib.hash_id

import java.io.InputStream
import java.security.MessageDigest

/**
 * Hash/Digest Based Identifier.
 *
 * This class offers an implementation of a digest-based identifier that is more
 * robust than a naked digest string.  Once constructed from the source string,
 * it generates the digest and efficiently stores the digest as a 128-bit
 * number.
 *
 * This type is effectively a "new type" over `byte[16]` which provides the
 * following features:
 *
 * * Allows consumers to make guarantees about the values they are given as
 *   a [HashID] can only be constructed via a valid hash either by hex string or
 *   by a raw array of exactly 16 bytes.
 * * Eliminates the class(es) of bugs that could result from dealing with a
 *   builtin type, relying on ID consumers to perform validation at every
 *   necessary step.
 * * Can be constructed via the standard constructor, or via the provided
 *   convenience methods allowing the [HashID] to be created from an arbitrary
 *   [String], [InputStream], or [Object] which will be MD5 hashed to generate
 *   a new `HashID` instance.
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
   *
   * **NOTE**: This value is not cached, and is calculated on every call to this
   * property/getter.
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
     * Calculates the MD5 hash of the given value and wraps that hash in a new
     * [HashID] instance.
     *
     * @param value Value that will be MD5 hashed.
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
     * Calculates the MD5 hash value of the contents of the given [InputStream]
     * and wraps that hash in a new [HashID] instance.
     *
     * @param stream InputStream over the value(s) that will be MD5 hashed.
     *
     * @param close Whether the given input stream should be closed by this
     * method.
     *
     * Defaults to `false`.
     *
     * @return The new [HashID].
     */
    @JvmStatic
    @JvmOverloads
    fun ofMD5(stream: InputStream, close: Boolean = false): HashID {
      val digest = MessageDigest.getInstance("MD5")

      val stream = stream.buffered()
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
     * Calculates the MD5 hash value of the stringified form of the given value
     * and wraps that hash in a new [HashID] instance.
     *
     * This method is a simple convenience method over:
     * ```
     *   HashID.ofMD5(myType.toString())
     * ```
     *
     * @param value Value that will be MD5 hashed.
     *
     * @return The new [HashID].
     */
    @JvmStatic
    fun ofMD5(value: Any) = ofMD5(value.toString())
  }
}
