var mock_program = require(__dirname + "/mock_program.js");
var assert = require('assert');
var child_process = require('child_process');
var path = require('path');
var nock = require('nock');
var fs = require('fs');
fs.extra = require('fs.extra');

const originalWorkingDirectory = process.cwd();
const workingDirectory = __dirname + "/tmp";

describe("install: ", function () {
    const gitUrl = "http://github.com/morleydev/unittest11";
    const cacheSourceDirectory = __dirname + "/../../src/cache/source";
    const cacheBinaryDirectory = __dirname + "/../../src/cache/unitest11/gnu/debug";
    const temporaryDirectory = __dirname + "/../../src/tmp/unittest11";

    beforeEach(function (done) {

        fs.mkdirSync(workingDirectory);
        process.chdir(workingDirectory);

        nock('http://localhost:1337/')
            .get('/projects/unittest11')
            .reply(200, {
                "name": "unittest11",
                "git": gitUrl
            });

        var mocks = [
            mock_program.createMock("make", __dirname + "/installMake.js"),
            mock_program.createMock("cmake", __dirname + "/installCMake.js") ,
            mock_program.createMock("git", __dirname + "/installGit.js")
        ];
        mock_program.startMocks(mocks, done);
    });
    afterEach(function (done) {
        mock_program.stopMocks(done);

        process.chdir(originalWorkingDirectory);
        fs.extra.rmrfSync(workingDirectory);
    });
    describe("successful install test: ", function ()  {

        beforeEach(function (done) {
            child_process.exec(path.normalize("node " + __dirname + "/../../src/run.js") + " install unittest11 debug gnu", function(err, stdout, stderr) {
                assert(err == null, "Error occurred when running program " + err);
                done();
            });
        });

        it("calls git with the expected arguments", function() {
            mock_program.verify("git", "clone " + gitUrl + "unittest11", cacheSourceDirectory);
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
    });
});