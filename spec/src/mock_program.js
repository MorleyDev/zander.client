var fs = require("fs"); fs.extra = require("fs.extra");
var path = require("path");
var assert = require("assert");

const configPath = __dirname + "/../../src/config.json";
const configBackupPath = __dirname + "/../../src/config_backup.json";

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
                fs.extra.copy(configPath, configBackupPath, function() { fs.unlink(configPath, writeConfig); });
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
	
	verify : function(programName, expectedArguments, workingDirectory) {

        // Bug(Feature?) in Node.js requires all quotes to be slashed in the command line.
        // This is stupid.
        expectedArguments = expectedArguments.replace(/"/g, '');
        expectedArguments = expectedArguments.replace(/'/g, '');

        const expectedWorkingDirectory = path.normalize(workingDirectory);

        var matchFound = false;
        var failureDescription = "Expected invocation of \"" + programName + (expectedArguments.length > 0 ? " " + expectedArguments : "") + "\" in " + expectedWorkingDirectory + " was not found\n";

        const executionLogPath = path.normalize(__dirname + "/../mockbin/execs/" + programName + ".json");
        if (fs.existsSync(executionLogPath)) {
            var invocations = JSON.parse(fs.readFileSync(executionLogPath));

            invocations.forEach(function (invocation) {
                const actualWorkingDirectory = path.normalize(invocation.working_directory);
                const actualArguments = invocation.arguments;

                var wasInvoked = ( actualArguments == expectedArguments && actualWorkingDirectory == expectedWorkingDirectory );
                failureDescription += ("Found invocation of \"" + programName + (actualArguments.length > 0 ? " " + actualArguments : "") + "\" in " + actualWorkingDirectory + "\n");

                if (wasInvoked)
                    matchFound = true;
            });
        } else {
            failureDescription += "No invocation found\n";
        }
        assert(matchFound, failureDescription);
	}
};
