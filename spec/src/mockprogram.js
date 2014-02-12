var fs = require("fs");
var fsextra = require("fs.extra");
var path = require("path");

const configPath = __dirname + "/../../src/config.json";
const configBackupPath = __dirname + "/../../src/config_backup.json";

module.exports = { 
	createMock : function(programName, programStub) {
		const stubCmd = "node " + path.normalize(__dirname + "/../mockbin/program.js") + " " + programName + " " + path.normalize(programStub);
        var retVal = { };
		retVal[programName] = stubCmd;
		return retVal;
	},

	startMocks : function(programs, onStarted) {
		const programConfig = {
			"programs" : programs
		};

        var writeConfig = function () {
            fs.writeFile(configPath, JSON.stringify(programConfig), function() { onStarted(); });
        };

        fs.exists(configPath, function(exists) {
            if (exists)
                fsextra.copy(configPath, configBackupPath, writeConfig);
            else
                writeConfig();
        });
	},

	stopMocks : function(onStopped) {

        fs.unlinkSync(configPath);
        fsextra.copy(configBackupPath, configPath, function() {
            fs.unlinkSync(configBackupPath);
            onStopped();
        });
	},
	
	verify : function(programName, arguments) { 

	}
};
