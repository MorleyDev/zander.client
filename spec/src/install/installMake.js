var fs = require("fs"),
    path = require("path");
fs.extra = require("fs.extra");

module.exports = {
	invoke : function(args) {

        const zanderProgramDirectory = __dirname + "/../../../src";
        const cacheBinaryDirectory = path.normalize(zanderProgramDirectory + "/cache/unitest11/gnu/debug");

        if (args.length == 1 && args[0] == "install") {
            fs.mkdirSync(cacheBinaryDirectory + "/include");
            fs.writeFileSync(cacheBinaryDirectory + "/include/some_header", "sdhaskfhaskghas");
            fs.writeFileSync(cacheBinaryDirectory + "/lib/some_lib", "asdagegsd");
            fs.writeFileSync(cacheBinaryDirectory + "/bin/some_bin", "wqeqwtsdgdgfdfds");
        }

        return 0;
    }
}