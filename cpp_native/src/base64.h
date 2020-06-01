//
//  base64 encoding and decoding with C++.
//  Version: 2.rc.03 (release candidate)
//

#ifndef BASE64_H
#define BASE64_H

#include <string>

namespace base64 {
    std::string encode(const std::string& s, bool url = false);
    std::string encode(const unsigned char*, unsigned int len, bool url = false);

    std::string decode(const std::string& s);
}

#endif /* BASE64_H */
