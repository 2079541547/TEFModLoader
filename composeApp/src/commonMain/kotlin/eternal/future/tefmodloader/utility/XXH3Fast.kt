package eternal.future.tefmodloader.utility

object XXH3Fast {
    private const val PRIME64_1 = 0x7E3779B185EBCA87L
    private const val PRIME64_2 = 0x42B2AE3D27D4EB4FL
    private const val PRIME64_3 = 0x165667B19E3779F9L
    private const val PRIME64_4 = 0x27D4EB2F165667C5L

    fun hash64(data: ByteArray, length: Int, seed: Long = 0): Long {
        return when {
            length <= 16 -> smallInputHash(data, length, seed)
            length <= 128 -> mediumInputHash(data, length, seed)
            else -> largeInputHash(data, length, seed)
        }.finalize()
    }

    private fun smallInputHash(data: ByteArray, len: Int, seed: Long): Long {
        val in64 = when (len) {
            in 0..7 -> (getUInt32(data, 0).toLong() and 0xFFFFFFFFL) + ((len.toLong() + seed) shl 32)
            else -> getUInt64(data, 0) xor getUInt64(data, len - 8)
        }
        return (in64 + PRIME64_1 + seed).mix()
    }

    private fun mediumInputHash(data: ByteArray, len: Int, seed: Long): Long {
        var acc = seed + PRIME64_1 + PRIME64_2
        acc = process64(data, 0, acc)
        acc = process64(data, len - 16, acc)
        return acc.mix()
    }

    private fun largeInputHash(data: ByteArray, len: Int, seed: Long): Long {
        var acc1 = seed + PRIME64_1 + PRIME64_2
        var acc2 = seed + PRIME64_2
        var acc3 = seed
        var acc4 = seed - PRIME64_1

        var offset = 0
        val blockSize = 128

        while (offset + blockSize <= len) {
            acc1 = process64(data, offset, acc1)
            acc2 = process64(data, offset + 16, acc2)
            acc3 = process64(data, offset + 32, acc3)
            acc4 = process64(data, offset + 48, acc4)
            offset += blockSize
        }

        val combined = acc1.mix() + acc2.mix() + acc3.mix() + acc4.mix()
        return processTail(data, offset, len - offset, combined + seed).finalize()
    }

    private fun process64(data: ByteArray, offset: Int, acc: Long): Long {
        val value = getUInt64(data, offset)
        return (acc + (value xor PRIME64_2)) * PRIME64_1
    }

    private fun processTail(data: ByteArray, offset: Int, remaining: Int, acc: Long): Long {
        var temp = acc
        var pos = offset
        while (pos + 8 <= remaining) {
            temp = (temp + getUInt64(data, pos)) * PRIME64_3
            pos += 8
        }
        if (pos + 4 <= remaining) {
            temp = (temp + (getUInt32(data, pos).toLong() shl 32)) * PRIME64_2
            pos += 4
        }
        while (pos < remaining) {
            temp = (temp + (data[pos].toLong() and 0xFF shl ((pos % 8) * 8))) * PRIME64_4
            pos++
        }
        return temp.mix()
    }

    private fun getUInt32(data: ByteArray, offset: Int): Int {
        return if (offset + 4 <= data.size) {
            (data[offset].toInt() and 0xFF shl 24) or
                    (data[offset + 1].toInt() and 0xFF shl 16) or
                    (data[offset + 2].toInt() and 0xFF shl 8) or
                    (data[offset + 3].toInt() and 0xFF)
        } else 0
    }

    private fun getUInt64(data: ByteArray, offset: Int): Long {
        return if (offset + 8 <= data.size) {
            (data[offset].toLong() and 0xFF shl 56) or
                    (data[offset + 1].toLong() and 0xFF shl 48) or
                    (data[offset + 2].toLong() and 0xFF shl 40) or
                    (data[offset + 3].toLong() and 0xFF shl 32) or
                    (data[offset + 4].toLong() and 0xFF shl 24) or
                    (data[offset + 5].toLong() and 0xFF shl 16) or
                    (data[offset + 6].toLong() and 0xFF shl 8) or
                    (data[offset + 7].toLong() and 0xFF)
        } else 0L
    }

    private fun Long.mix(): Long {
        var temp = this xor (this ushr 33)
        temp *= PRIME64_2
        temp = temp xor (temp ushr 29)
        temp *= PRIME64_3
        return temp xor (temp ushr 32)
    }

    private fun Long.finalize(): Long {
        return this.mix()
    }
}