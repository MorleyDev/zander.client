var linq = require("linq");
var fs = require("fs");
var path = require("path");

var args = linq.From(process.argv).Skip(2).ToArray();

var programName = args[0];
var programPath = args[1];
var programArgs = linq.From(args).Skip(2).ToArray();

var jsonOut = {
	"arguments" : programArgs.join(' '),
	"working_directory": path.normalize(process.cwd())
};

var output = [ ];
try { fs.mkdirSync(__dirname + "/execs"); } catch(e) { }
if ( fs.existsSync(__dirname + "/execs/" + programName + ".json") ) {
    output = JSON.parse(fs.readFileSync(__dirname + "/execs/" + programName + ".json"));
}
output.push(jsonOut);
var jsonString = JSON.stringify(output);

fs.writeFileSync(__dirname + "/execs/" + programName + ".json", jsonString);

var script = require(programPath);

var returnCode = script.invoke(programArgs);
process.exit(returnCode);
