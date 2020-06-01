package org.example.nativeb64.cpp

import org.example.nativeb64.NativeBase64
import org.khronos.webgl.Int8Array
import org.khronos.webgl.get
import org.khronos.webgl.set
import kotlin.js.Promise


private external class TextDecoder() {
    fun decode(array: Int8Array)
}

@OptIn(ExperimentalStdlibApi::class)
class NativeBase64Js : NativeBase64 {

    companion object {
        internal lateinit var instance: CppBase64Js

        fun init(): Promise<*> =
                cpp_base64_js().then(
                        onFulfilled = { instance = it },
                        onRejected = { error("Could not load cpp_base64_js") }
                )
    }

    private inline fun <R> malloc(size: Int, block: (Ptr) -> R): R {
        val ptr = instance._malloc(size)
        try {
            return block(ptr)
        } finally {
            instance._free(ptr)
        }
    }

    override fun encode(bytes: ByteArray, url: Boolean): String {
        val resultMaxLen = instance._base64_max_len(bytes.size)
        val isUrl = if (url) 1 else 0
        malloc(bytes.size) { bytesPtr ->
            instance.HEAP8.set(bytes.unsafeCast<Array<Byte>>(), offset = bytesPtr)
            malloc(resultMaxLen) { resultPtr ->
                malloc(Int.SIZE_BYTES) { resultLenPtr ->
                    val error = instance._base64_encode(bytesPtr, bytes.size, isUrl, resultPtr, resultMaxLen, resultLenPtr)
                    if (error != nullPtr) {
                        val str = instance.UTF8ToString(error)
                        instance._free(error)
                        throw NativeBase64.Error(str)
                    }
                    val resultLen = instance.HEAP32[resultLenPtr / 4]
                    val resultArray = js("[]").slice.call(instance.HEAP8.subarray(resultPtr, resultPtr + resultLen)).unsafeCast<ByteArray>()
                    return resultArray.decodeToString()
                }
            }
        }
    }

    override fun decode(b64: String): ByteArray {
        val b64Bytes = b64.encodeToByteArray()
        val maxSize = b64Bytes.size + 1
        malloc(maxSize) { b64Ptr ->
            instance.HEAP8.set(b64Bytes.unsafeCast<Array<Byte>>(), offset = b64Ptr)
            instance.HEAP8[b64Ptr + b64Bytes.size] = 0
            malloc(maxSize) { resultPtr ->
                malloc(Int.SIZE_BYTES) { resultLenPtr ->
                    val error = instance._base64_decode(b64Ptr, resultPtr, maxSize, resultLenPtr)
                    if (error != nullPtr) {
                        val str = instance.UTF8ToString(error)
                        instance._free(error)
                        throw NativeBase64.Error(str)
                    }
                    val resultLen = instance.HEAP32[resultLenPtr / 4]
                    return js("[]").slice.call(instance.HEAP8.subarray(resultPtr, resultPtr + resultLen)).unsafeCast<ByteArray>()
                }
            }
        }
    }

}

actual fun getCppNativeBase64(): NativeBase64 = NativeBase64Js()

