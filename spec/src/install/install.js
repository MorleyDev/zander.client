var assert = require('assert');
var child_process = require('child_process');
var path = require('path');
var fs = require('fs');
fs.extra = require('fs.extra');

var zander = require(__dirname + "/../zander.js");

describe("install: Given a server and available programs", function () {

    const workingDirectory = __dirname + "/tmp";

    const libraryName = "unittest11";

    const temporaryDirectory = zander.directories.getTmpWorkingDir(libraryName);
    const projectCachedBinaryDirectory = zander.directories.getCacheBinaryDir(libraryName, "gnu", "debug");

    const gitUrl = "http://github.com/morleydev/" + libraryName;

    var mockServer;

    beforeEach(function (done) {
        fs.extra.mkdirpSync(workingDirectory);

        zander.loadTestConfig(function(port) {
            mockServer = zander.createMockServer(port);
            mockServer.get("/projects/" + libraryName, function (request, response, next) {
                response.send(200, { git : gitUrl });
                return next();
            });

            var mocks = [
                zander.mocking.createMock("make", __dirname + "/installMake.js"),
                zander.mocking.createMock("cmake", __dirname + "/installCMake.js") ,
                zander.mocking.createMock("git", __dirname + "/installGit.js")
            ];
            zander.mocking.startMocks(mocks, done);
        });
    });
    afterEach(function (done) {
        mockServer.close();
        zander.mocking.stopMocks(function() {

            zander.clean();
            fs.extra.rmrfSync(workingDirectory);
            done();
        });
    });
    describe("When installing an existing library in debug mode: ", function ()  {

        beforeEach(function (done) {

            zander.launch("install", libraryName, "debug", "gnu", workingDirectory, function(err, stdout, stderr) {
                assert(err == null, "Error occurred when running program " + err + "\nStdOut:\n" + stdout + "\nStdErr:\n" + stderr);
                done();
            });
        });

        it("Then it calls git with the expected arguments", function() {
            zander.mocking.verify("git", "clone " + gitUrl + " " + libraryName, zander.directories.cacheSource);
        });
        it("Then it calls cmake with the expected arguments", function() {
            zander.mocking.verify("cmake", path.normalize(zander.directories.getCacheSourceDir(libraryName)) + " -G\"MinGW Makefiles\" -DCMAKE_BUILD_TYPE=Debug -DCMAKE_INSTALL_DIRECTORY=" + path.normalize(projectCachedBinaryDirectory), temporaryDirectory);
        });
        it("Then it calls make", function() {
            zander.mocking.verify("make", "install", temporaryDirectory)
        });
        it("Then it calls make install", function() {
            zander.mocking.verify("make", "install", temporaryDirectory)
        });
        it("Then it copies the install files into the cache binary directory", function() {
            assert(fs.existsSync(projectCachedBinaryDirectory + "/include/some_header"), "expected header file was not copied");
            assert(fs.existsSync(projectCachedBinaryDirectory + "/lib/some_lib"), "expected lib file was not copied");
            assert(fs.existsSync(projectCachedBinaryDirectory + "/bin/some_bin"), "expected binary file was not copied");
        });
        it("Then it copies the install files into the working directory", function() {
            assert(fs.existsSync(workingDirectory + "/include/some_header"), "expected header file was not copied");
            assert(fs.existsSync(workingDirectory + "/lib/some_lib"), "expected lib file was not copied");
            assert(fs.existsSync(workingDirectory + "/bin/some_bin"), "expected binary file was not copied");
        });
    });
});