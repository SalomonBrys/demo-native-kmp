package org.example.nativeb64.cpp

import org.example.nativeb64.NativeBase64


class NativeBase64Jvm : NativeBase64 {

    companion object JNI {
        init {
            System.loadLibrary("cpp_base64_jni")
        }
    }

    external override fun encode(bytes: ByteArray, url: Boolean): String

    external override fun decode(b64: String): ByteArray

}

actual fun getCppNativeBase64(): NativeBase64 = NativeBase64Jvm()
