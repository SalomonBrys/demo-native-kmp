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
        internal lateinit var w: CppBase64Js

        private var loader: Promise<*>? = null

        fun init(): Promise<*> {
            if (loader == null) {
                loader = cpp_base64_js().then(
                        onFulfilled = { w = it },
                        onRejected = { error("Could not load cpp_base64_js") }
                )
            }
            return loader!!
        }
    }

    private inline fun <R> malloc(size: Int, block: (Ptr) -> R): R {
        val ptr = w._malloc(size)
        try {
            return block(ptr)
        } finally {
            w._free(ptr)
        }
    }

    private fun Ptr.check() {
        if (this == nullPtr) return
        val str = w.UTF8ToString(this)
        w._free(this)
        throw NativeBase64.Error(str)
    }

    override fun encode(bytes: ByteArray, url: Boolean): String {
        val resultMaxLen = w._base64_max_encoded_len(bytes.size)
        malloc(bytes.size) { bytesPtr ->
            w.HEAP8.set(bytes.unsafeCast<Array<Byte>>(), offset = bytesPtr)
            malloc(resultMaxLen) { resultPtr ->
                malloc(Int.SIZE_BYTES) { resultLenPtr ->
                    val isUrl = if (url) 1 else 0
                    w._base64_encode(bytesPtr, bytes.size, isUrl, resultPtr, resultMaxLen, resultLenPtr).check()
                    val resultLen = w.HEAP32[resultLenPtr / 4]
                    val resultArray = js("[]").slice.call(w.HEAP8.subarray(resultPtr, resultPtr + resultLen)).unsafeCast<ByteArray>()
                    return resultArray.decodeToString()
                }
            }
        }
    }

    override fun decode(b64: String): ByteArray {
        val b64Bytes = b64.encodeToByteArray()
        val resultMaxLen = w._base64_max_decoded_len(b64Bytes.size) + 1
        malloc(b64Bytes.size + 1) { b64Ptr ->
            w.HEAP8.set(b64Bytes.unsafeCast<Array<Byte>>(), offset = b64Ptr)
            w.HEAP8[b64Ptr + b64Bytes.size] = 0
            malloc(resultMaxLen) { resultPtr ->
                malloc(Int.SIZE_BYTES) { resultLenPtr ->
                    w._base64_decode(b64Ptr, resultPtr, resultMaxLen, resultLenPtr).check()
                    val resultLen = w.HEAP32[resultLenPtr / 4]
                    return js("[]").slice.call(w.HEAP8.subarray(resultPtr, resultPtr + resultLen)).unsafeCast<ByteArray>()
                }
            }
        }
    }

}

actual fun getCppNativeBase64(): NativeBase64 = NativeBase64Js()
