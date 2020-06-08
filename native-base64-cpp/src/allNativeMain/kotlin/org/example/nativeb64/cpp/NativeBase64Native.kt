package org.example.nativeb64.cpp

import kotlinx.cinterop.*
import org.example.nativeb64.NativeBase64
import org.example.nativeb64.cpp.cinterop.*


class NativeBase64Native : NativeBase64 {

    private fun CPointer<ByteVar>?.check() {
        if (this == null) return

        val message = this.toKString()
        nativeHeap.free(this)
        throw NativeBase64.Error(message)
    }

    override fun encode(bytes: ByteArray, url: Boolean): String {
        memScoped {
            val resultMaxLen = base64_max_encoded_len(bytes.size)
            val resultChars = allocArray<ByteVar>(resultMaxLen + 1)
            val resultLen = alloc<IntVar>()

            base64_encode(bytes.toCValues(), bytes.size, url.toByte().toInt(), resultChars, resultMaxLen, resultLen.ptr).check()

            resultChars[resultLen.value] = 0
            return resultChars.toKString()
        }
    }

    override fun decode(b64: String): ByteArray {
        memScoped {
            val resultMaxLen = base64_max_decoded_len(b64.length)
            val resultBuffer = allocArray<ByteVar>(resultMaxLen)
            val resultLen = alloc<IntVar>()

            base64_decode(b64, resultBuffer, resultMaxLen, resultLen.ptr).check()

            return ByteArray(resultLen.value) { resultBuffer[it] }
        }
    }

}

actual fun getCppNativeBase64(): NativeBase64 = NativeBase64Native()
