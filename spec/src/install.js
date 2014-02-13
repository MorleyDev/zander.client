var mockProgram = require(__dirname + "/mockProgram.js");
var child_process = require('child_process');
var path = require('path');

describe("install: ", function () {
    beforeEach(function (done) {

        var programs = [
            mockProgram.createMock("make", __dirname + "/installMake.js")
        ];

        mockProgram.startMocks(programs, done);
    });
    afterEach(function (done) {
        mockProgram.stopMocks(done);
    });
    describe("test: ", function ()  {

        beforeEach(function (done) {
            child_process.exec(path.normalize("node " + __dirname + "/../../src/run.js"), function(err, stdout, stderr) {
                if ( err ) {
                    console.log(err.code);
                }
                done();
            });
        });

        it("calls make", function() {
            mockProgram.verify("make", "", process.cwd())
        });
    });
});