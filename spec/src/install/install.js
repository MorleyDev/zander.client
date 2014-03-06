var mock_program = require(__dirname + "/../mock_program.js");
var assert = require('assert');
var child_process = require('child_process');
var path = require('path');
var restify = require('restify');
var fs = require('fs');
fs.extra = require('fs.extra');

const originalWorkingDirectory = process.cwd();
const workingDirectory = __dirname + "/tmp";

describe("install: ", function () {
    const zanderProgramDirectory = __dirname + "/../../../src";
    const zanderProcess = path.normalize("node " + zanderProgramDirectory + "/run.js");

    const cacheSourceDirectory = zanderProgramDirectory + "/cache/source";
    const cacheBinaryDirectory = zanderProgramDirectory + "/cache/unitest11/gnu/debug";
    const temporaryDirectory = zanderProgramDirectory + "/src/tmp/unittest11";

    const gitUrl = "http://github.com/morleydev/unittest11";
    var mockServer = restify.createServer();

    before(function (done) {

        if (fs.existsSync(workingDirectory))
            fs.extra.rmrfSync(workingDirectory);

        fs.mkdirSync(workingDirectory);
        process.chdir(workingDirectory);

        mockServer.listen(1337, "localhost");
        mockServer.get("/projects/unittest11", function (request, response, next) {
            response.send(200, { git:gitUrl });
            return next();
        });

        var mocks = [
            mock_program.createMock("make", __dirname + "/installMake.js"),
            mock_program.createMock("cmake", __dirname + "/installCMake.js") ,
            mock_program.createMock("git", __dirname + "/installGit.js")
        ];
        mock_program.startMocks(mocks, done);
    });
    after(function (done) {
        mock_program.stopMocks(done);

        process.chdir(originalWorkingDirectory);
        fs.extra.rmrfSync(workingDirectory);
    });
    describe("successful install test: ", function ()  {

        before(function (done) {
            this.timeout(0);
            child_process.exec(zanderProcess + " install unittest11 debug gnu", function(err, stdout, stderr) {
                assert(err == null, "Error occurred when running program " + err + "\nStdOut:\n" + stdout + "\nStdErr:\n" + stderr);
                done();
            });
        });

        it("calls git with the expected arguments", function() {
            mock_program.verify("git", "clone " + gitUrl + " unittest11", cacheSourceDirectory);
        });
        it("calls cmake with the expected arguments", function() {
            mock_program.verify("cmake", path.normalize(cacheSourceDirectory) + "-G\"MinGW Makefiles\" -DCMAKE_BUILD_TYPE=Debug -DCMAKE_INSTALL_DIRECTORY=" + path.normalize(cacheBinaryDirectory), temporaryDirectory);
        });
        it("calls make", function() {
            mock_program.verify("make", "install", temporaryDirectory)
        });
        it("calls make install", function() {
            mock_program.verify("make", "install", temporaryDirectory)
        });
        it("copies the install files into the working directory", function() {
            assert(fs.existsSync(cacheBinaryDirectory + "/include/some_header"), "expected header file was not copied");
            assert(fs.existsSync(cacheBinaryDirectory + "/lib/some_lib"), "expected lib file was not copied");
            assert(fs.existsSync(cacheBinaryDirectory + "/bin/some_bin"), "expected binary file was not copied");
        });
    });
});