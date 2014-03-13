var fs = require("fs"),
    path = require("path");
fs.extra = require("fs.extra");

module.exports = {
	invoke : function(args) {

        const cacheBinaryDirectory = path.normalize(__dirname + "/../../../src/cache/bin/unittest11/gnu/debug");

        if (args.length == 1 && args[0] == "install") {
            fs.extra.mkdirpSync(cacheBinaryDirectory + "/include");
            fs.extra.mkdirpSync(cacheBinaryDirectory + "/lib");
            fs.extra.mkdirpSync(cacheBinaryDirectory + "/bin");

            fs.writeFileSync(cacheBinaryDirectory + "/include/some_header", "sdhaskfhaskghas");
            fs.writeFileSync(cacheBinaryDirectory + "/lib/some_lib", "asdagegsd");
            fs.writeFileSync(cacheBinaryDirectory + "/bin/some_bin", "wqeqwtsdgdgfdfds");
        }

        return 0;
    }
}