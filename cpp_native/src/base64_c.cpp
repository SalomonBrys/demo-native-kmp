//
// Created by salomon on 30/05/2020.
//

#include "base64_c.h"

#include "base64.h"
#include <cstring>
#include <sstream>
#include <iostream>
#include <cmath>

extern "C" {

    int base64_max_encoded_len(int bytes_len) {
        return (int) ceil((double) bytes_len / 3.0 * 4.0) + 2;
    }

    int base64_max_decoded_len(int b64_len) {
        return (int) ((double) b64_len / 4.0 * 3.0);
    }

    char *base64_encode(const char* bytes, int bytes_len, int is_url, char *out_chars, int out_chars_maxlen, int* out_len) {
        auto result = base64::encode((unsigned const char*) bytes, bytes_len, is_url);

        if (result.length() > out_chars_maxlen) {
            std::stringstream ss;
            ss << "Need a char array that is minimum " << result.length() << " bytes long, but got a smaller array of " << out_chars_maxlen << " bytes.";
            return strdup(ss.str().c_str());
        }

        memcpy(out_chars, result.c_str(), result.length());
        *out_len = result.length();

        return nullptr;
    }

    char *base64_decode(const char* base64, char *out_buff, int out_buff_maxlen, int* out_len) {
        std::string result;
        try {
            result = base64::decode(base64);
        } catch (std::exception &ex) {
            return strdup(ex.what());
        }

        if (result.length() > out_buff_maxlen) {
            std::stringstream ss;
            ss << "Need a buffer that is minimum " << result.length() << " bytes long, but got a smaller buffer of " << out_buff_maxlen << " bytes.";
            return strdup(ss.str().c_str());
        }

        memcpy(out_buff, result.c_str(), result.length());
        *out_len = result.length();

        return nullptr;
    }

}
