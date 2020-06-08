package org.example.nativeb64.cpp

import org.khronos.webgl.Int32Array
import org.khronos.webgl.Int8Array
import kotlin.js.Promise

typealias Ptr = Int
const val nullPtr: Ptr = 0

external interface CppBase64Js {
    fun _malloc(size: Int): Ptr

    fun _free(size: Ptr)

    val HEAP8: Int8Array
    val HEAP32: Int32Array

    fun UTF8ToString(chars: Ptr, maxBytesToRead: Int = definedExternally): String

    fun _base64_max_encoded_len(bytes_len: Int): Int
    fun _base64_max_decoded_len(b64_len: Int): Int
    fun _base64_encode(bytes: Ptr, bytes_len: Int, is_url: Int, out_chars: Ptr, out_chars_maxlen: Int, out_len: Ptr): Ptr
    fun _base64_decode(base64: Ptr, out_buff: Ptr, out_buff_maxlen: Int, out_len: Ptr): Ptr
}

@JsModule("cpp_base64_js")
@JsNonModule
external fun cpp_base64_js() : Promise<CppBase64Js>
