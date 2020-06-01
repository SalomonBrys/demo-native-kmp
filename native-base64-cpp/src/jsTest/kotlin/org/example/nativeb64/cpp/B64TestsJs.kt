package org.example.nativeb64.cpp

actual inline fun initAndRun(crossinline test: () -> Unit): dynamic = NativeBase64Js.init().then { test() }
