package org.veupathdb.lib.hash_id

internal data class HashIDImpl(
  override val string: String,
  private  val raw:    ByteArray,
): HashID {

  override val bytes: ByteArray
    get() = raw.copyOf()

  override fun toString() = string

  override fun hashCode() = raw.contentHashCode()

  override fun equals(other: Any?) =
    when (other) {
      is HashIDImpl -> raw.contentEquals(other.raw)
      else          -> false
    }
}
