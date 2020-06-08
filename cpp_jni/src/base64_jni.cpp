#include "base64_c.h"

#include <jni.h>
#include <cstdlib>
#include <iostream>


#define EXCEPTION_CLASS "org/example/nativeb64/NativeBase64$Error"

extern "C" JNIEXPORT jstring JNICALL Java_org_example_nativeb64_cpp_NativeBase64Jvm_encode(JNIEnv *env, jobject, jbyteArray jBytes, jboolean url) {
    auto length = env->GetArrayLength(jBytes);
    int resultMaxLen = base64_max_encoded_len(length);
    auto resultChars = (char*) malloc(resultMaxLen * sizeof(char) + 1);

    auto bytes = (jbyte*) env->GetPrimitiveArrayCritical(jBytes, nullptr);

    int resultLen;
    char* error = base64_encode((const char*)bytes, length, url, resultChars, resultMaxLen, &resultLen);

    env->ReleasePrimitiveArrayCritical(jBytes, bytes, JNI_ABORT);

    if (error != nullptr) {
        env->ThrowNew(env->FindClass(EXCEPTION_CLASS), error);
        free(error);
        return nullptr;
    }

    resultChars[resultLen] = 0;
    jstring jResult = env->NewStringUTF(resultChars);
    free(resultChars);

    return jResult;
}

extern "C" JNIEXPORT jbyteArray JNICALL Java_org_example_nativeb64_cpp_NativeBase64Jvm_decode(JNIEnv *env, jobject, jstring jB64) {
    auto b64Length = env->GetStringLength(jB64);
    int resultMaxLen = base64_max_decoded_len(b64Length);
    auto resultBuffer = (char*) malloc(resultMaxLen * sizeof(char));

    auto b64 = env->GetStringUTFChars(jB64, nullptr);

    int resultLen;
    char* error = base64_decode(b64, resultBuffer, resultMaxLen, &resultLen);

    env->ReleaseStringUTFChars(jB64, b64);

    if (error != nullptr) {
        env->ThrowNew(env->FindClass(EXCEPTION_CLASS), error);
        free(error);
        return nullptr;
    }

    jbyteArray result = env->NewByteArray(resultLen);
    env->SetByteArrayRegion(result, 0, resultLen, (jbyte*) resultBuffer);
    free(resultBuffer);

    return result;
}
