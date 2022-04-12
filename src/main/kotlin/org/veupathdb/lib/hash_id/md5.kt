package org.veupathdb.lib.hash_id

import java.io.BufferedInputStream
import java.io.InputStream
import java.nio.charset.StandardCharsets
import java.security.MessageDigest

@Suppress("NOTHING_TO_INLINE")
internal inline fun InputStream.md5(close: Boolean = false): ByteArray {
  val stream = when (this) {
    is BufferedInputStream -> this
    else                   -> BufferedInputStream(this)
  }

  val buff = ByteArray(8192)

  return with(MessageDigest.getInstance("MD5")) {
    try {
      while (true) {
        val red = stream.read(buff)

        update(buff, 0, red)

        if (red < buff.size) {
          break
        }
      }

      digest()
    } finally {
      if (close)
        stream.close()
    }
  }
}

@Suppress("NOTHING_TO_INLINE")
internal inline fun String.md5(): ByteArray =
  with(MessageDigest.getInstance("MD5")) {
    update(toByteArray(StandardCharsets.UTF_8))
    digest()
  }