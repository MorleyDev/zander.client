var Enumerable = require("linq");
var fileSystem = require("fs");

var args = Enumerable.From(process.argv).Skip(2).ToArray();
var programName = args[0];
var programPath = args[1];
var programArgs = Enumerable.From(args).Skip(2).ToArray();

var jsonOut = { 
	"program" : programName.toString(),
	"arguments" : programArgs,
	"working_directory": process.cwd()
};
var jsonString = JSON.stringify(jsonOut);

try { fileSystem.mkdirSync(__dirname + "/execs"); } catch(e) { }
fileSystem.writeFileSync(__dirname + "/execs/" + programName + ".json", jsonString);

var script = require(programPath);

var returnCode = script.invoke(programArgs);
process.exit(returnCode);
