var mock_program = require(__dirname + "/mock_program.js");
var child_process = require("child_process");
var fs = require("fs");
fs.extra = require("fs.extra");
var restify = require("restify");
var path = require("path");
var freeport = require("freeport");

const configFilePath = path.normalize(__dirname + "/../../src/config.json");
const configSpecFilePath = path.normalize(__dirname + "/../../config/spec.config.json");

module.exports = {

    directories : {
        program : __dirname + "/../../src",
        cache : __dirname + "/../../src/cache",
        tmp : __dirname + "/../../src/tmp",
        cacheSource : __dirname + "/../../src/cache/src",
        cacheBin : __dirname + "/../../src/cache/bin",

        getCacheBinaryDir : function(project, compiler, mode) {
            return __dirname + "/../../src/cache/bin/" + project + "/" + compiler + "/" + mode;
        },
        getCacheSourceDir : function(project) {
            return __dirname + "/../../src/cache/src/" + project;
        },
        getTmpWorkingDir : function(project) {
            return __dirname + "/../../src/tmp/" + project;
        }
    },

    loadTestConfig : function(done) {

        freeport(function(err, port) {

            var specFileJson = JSON.parse(fs.readFileSync(configSpecFilePath));
            specFileJson["server"] = "http://localhost:" + port;

            fs.writeFileSync(configFilePath, JSON.stringify(specFileJson));
            done(port);
        });
    },

    mocking : mock_program,

    createMockServer : function(port) {
        var server = restify.createServer();
        server.listen(port, "localhost");
        return server;
    },

    launch : function(operation, library, mode, compiler, workingDir, done) {
        const runFile = path.normalize(this.directories.program + "/run.js");

        child_process.exec("node " + runFile + " " + operation + " " + library + " " + compiler + " " + mode, { cwd: workingDir }, function(err, stdout, stderr) {
            done(err, stdout, stderr);
        });
    },

    clean : function() {
        fs.unlinkSync(configFilePath);
        fs.extra.rmrfSync(this.directories.cache);
        fs.extra.rmrfSync(this.directories.tmp);
    }
};
