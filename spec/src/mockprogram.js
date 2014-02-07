module.exports = { 
	mock : function(programName, programStub) {
		var programConfig = { 
			"programs" : [
				programName : "node " + __dirname + "../mockbin/program.js " + programName + " " + __dirname = "/" + programStub 
			]
		};
	}
	
	verify : function(programName, arguments) { 
	}
};
