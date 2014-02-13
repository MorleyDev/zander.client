var fs = require("fs"); fs.extra = require("fs.extra");
var path = require("path");
var assert = require("assert");

const configPath = __dirname + "/../../src/config.json";
const configBackupPath = __dirname + "/../../src/config_backup.json";

function checkMockInvoked(programName, arguments, workingDirectory, callback) {

    if (fs.existsSync(__dirname + "/../mockbin/execs/" + programName + ".json")) {
        var invocation = JSON.parse(fs.readFileSync(__dirname + "/../mockbin/execs/" + programName + ".json"));
        var wasInvoked = ( invocation.arguments == arguments  && invocation.working_directory == path.normalize(workingDirectory) );
        var failureDescription = ("Found invocation of \"" + programName + (invocation.arguments.length > 0 ? " " + invocation.arguments : "") + "\" in " + invocation.working_directory);
        callback(wasInvoked, failureDescription);
    }
    callback(false, "No invocation found");
}

module.exports = { 
	createMock : function(programName, programStub) {
		const stubCmd = "node " + path.normalize(__dirname + "/../mockbin/program.js") + " " + programName + " " + path.normalize(programStub);
        return { "name" : programName, "stub" : stubCmd };
	},

	startMocks : function(programs, onStarted) {

        var originalConfig = { };
        if (fs.existsSync(configPath))
            originalConfig = JSON.parse(fs.readFileSync(configPath));

		var programConfig = originalConfig;
        programConfig.programs = { };

       programs.forEach(function (program) {
           programConfig.programs[program.name] = program.stub;
        });

        var writeConfig = function () {
            fs.writeFile(configPath, JSON.stringify(programConfig), function() { onStarted(); });
        };

        fs.exists(configPath, function(exists) {
            if (exists)
                fs.extra.copy(configPath, configBackupPath, writeConfig);
            else
                writeConfig();
        });
	},

	stopMocks : function(onStopped) {

        if ( fs.existsSync(__dirname + "/../mockbin/execs/") )
            fs.extra.rmrfSync(__dirname + "/../mockbin/execs/");

        fs.unlinkSync(configPath);
        if ( fs.existsSync(configBackupPath) ) {
            fs.extra.copy(configBackupPath, configPath, function() {
                fs.unlinkSync(configBackupPath);
                onStopped();
            });
        } else
            onStopped();
	},
	
	verify : function(programName, arguments, workingDirectory) {

        var failureDescription = "Expected invocation of \"" + programName + (arguments.length > 0 ? " " + arguments : "") + "\" in " + workingDirectory + " was not found";
        checkMockInvoked(programName, arguments, workingDirectory, function (wasInvoked, description) {
            assert(wasInvoked, failureDescription + '\n' + description);
        });
	},

    verifyNot : function(programName, arguments, workingDirectory) {

        checkMockInvoked(programName, arguments, workingDirectory, function (wasInvoked, description) {
            var failureDescription = "Unexpected invocation of \"" + programName + (arguments.length > 0 ? " " + arguments : "") + "\" in " + workingDirectory + " was found";
            assert(!wasInvoked, failureDescription + '\n' + description);
        });
    }
};
