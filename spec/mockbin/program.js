var Enumerable = require("linq");
var fileSystem = require("fs");

var args = Enumerable.From(process.argv).Skip(3).ToArray();
var programName = args[0];
var programPath = args[1];
var programArgs = Enumerable.From(args).Skip(1).ToArray();

var script = require(programPath);

var jsonOut = { 
	"program" : programName.toString(),
	"arguments" : programArgs
};
var jsonString = JSON.stringify(jsonOut);

try { fileSystem.mkdirSync(__dirname + "/execs"); } catch(e) { }
fileSystem.writeFileSync(__dirname + "/execs/" + programName + ".json", jsonString);

var returnCode = script.invoke(programArgs);
process.exit(returnCode);
