//
// Created by salomon on 30/05/2020.
//

#include "base64_c.h"

#include "base64.h"
#include <cstring>
#include <sstream>
#include <iostream>

extern "C" {

    int base64_max_len(int str_len) {
        return (str_len + 2) / 3 * 4;
    }

    char *base64_encode(const char* bytes, int bytes_len, int is_url, char *out_chars, int out_chars_maxlen, int* out_len) {
        auto result = base64::encode((unsigned const char*) bytes, bytes_len, is_url);

        if (result.length() > out_chars_maxlen) {
            std::stringstream ss;
            ss << "Need a buffer that is minimum " << result.length() << " bytes long, but got a smaller buffer of " << out_chars_maxlen << " bytes.";
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
