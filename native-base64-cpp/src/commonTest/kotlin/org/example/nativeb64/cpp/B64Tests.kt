package org.example.nativeb64.cpp

import org.example.nativeb64.NativeBase64
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue


expect inline fun initAndRun(crossinline test: () -> Unit)

@ExperimentalStdlibApi
class B64Tests {

    private val b64 = getCppNativeBase64()

    @Test fun simpleB64Encode() = initAndRun {
        assertEquals("U2Fsb21vbg==", b64.encode("Salomon".encodeToByteArray()))

        assertEquals("AQIDBAU=", b64.encode(byteArrayOf(1, 2, 3, 4, 5)))
    }

    @Test fun simpleB64Decode() = initAndRun {
        assertEquals("Salomon", b64.decode("U2Fsb21vbg==").decodeToString())

        assertTrue(byteArrayOf(1, 2, 3, 4, 5).contentEquals(b64.decode("AQIDBAU=")))
    }

    @Test fun invalidCharacter() = initAndRun {
        val ex = assertFailsWith<NativeBase64.Error> { b64.decode("AB*CD") }
        assertEquals("Input is incorrect: found non-base64 caracter '*'.", ex.message)
    }
}
