const webpack = require("webpack");
const path = require('path');

config.resolve.alias = {
    cpp_base64_js: path.resolve(__dirname, '../../../../cpp_native/build/cmake/out/base64Wasm/js/cpp_base64_js.js')
}
config.plugins.push(new webpack.IgnorePlugin(/(fs)/));
