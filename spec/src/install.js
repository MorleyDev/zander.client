var mockProgram = require(__dirname + "/mockProgram.js");

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
});