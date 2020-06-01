#include <iostream>
#include <sstream>
#include "base64.h"

using namespace std;


int main(int argc, const char** argv)
{
    if (argc < 2) {
        cerr << "There must be a first argument which needs to be either 'encode' or 'decode'" << endl;
        return -1;
    }

    auto command = string(argv[1]);

    if (command != "encode" && command != "decode") {
        cerr << "First argument needs to be either 'encode' or 'decode'" << endl;
        return -1;
    }

    string str;
    if (argc < 3) {
        stringstream ss;
        ss << cin.rdbuf();
        str = ss.str();
    } else {
        str = argv[2];
    }

    string result;

    if (command == "encode") {
        result = base64::encode(str);
    } else if (command == "decode") {
        try {
            result = base64::decode(str);
        } catch(std::exception &ex) {
            cerr << ex.what() << endl;
            return -1;
        }
    }

    cout << result;

    return 0;
}
