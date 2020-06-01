#ifndef BASE64_C_H
#define BASE64_C_H

#ifdef __cplusplus
extern "C" {
#endif

int base64_max_len(int str_len);

char *base64_encode(const char* bytes, int bytes_len, int is_url, char *out_chars, int out_chars_maxlen, int* out_len);
char *base64_decode(const char* base64, char *out_buff, int out_buff_maxlen, int* out_len);

#ifdef __cplusplus
}
#endif

#endif //OCV_DETECT_BASE64_C_H
