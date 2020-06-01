package org.example.nativeb64.cpp

import kotlin.js.Promise
import kotlin.test.Test
import kotlin.test.fail

class JsTests {

    @Test fun load(): Promise<*> =
            cpp_base64_js().then(
                    onFulfilled = {},
                    onRejected = {
                        fail()
                    }
            )

}
