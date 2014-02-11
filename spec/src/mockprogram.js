var fileSystem = require("fs");
var path = require("path");

module.exports = { 
	createMock : function(programName, programStub) {
		var stubCmd = "node " + path.normalize(__dirname + "/../mockbin/program.js") + " " + programName + " " + path.normalize(programStub);
		var name = programName;

		var retVal = { };
		retVal[name] = stubCmd;
		return retVal;
	},

	startMocks : function(programs) {
		var programConfig = { 
			"programs" : programs
		};

		fileSystem.writeFileSync(path.normalize(__dirname + "/../../src/config.json"), JSON.stringify(programConfig));
	},

	stopMocks : function() {
		
	},
	
	verify : function(programName, arguments) { 

	}
};
