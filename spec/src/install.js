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
    beforeEach(function (done) {

        path.mkdirSync(workingDirectory);

        mock_program.startMocks([ mock_program.createMock("make", __dirname + "/installMake.js") ], done);
    });
    afterEach(function (done) {
        mock_program.stopMocks(done);
        fs.extra.rmrfSync(workingDirectory);
    });
    describe("successful install test: ", function ()  {

        beforeEach(function (done) {
            child_process.exec(path.normalize("node " + __dirname + "/../../src/run.js") + " install unittest11 debug msvc12", function(err, stdout, stderr) {
                assert(err == null, "Error occurred when running program: " + err.code);
                done();
            });
        });

        it("calls cmake with the expected arguments", function() {

        });
        it("calls make", function() {
            mock_program.verify("make", "", process.cwd())
        });
    });
});