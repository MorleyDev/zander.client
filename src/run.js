var fs = require('fs');
fs.extra = require('fs.extra');
var child_process = require('child_process');
var restify = require('restify');
var path = require("path");

function loadConfigurationFile() {
    var config = { };
    try {
        const configFile = fs.readFileSync(__dirname + '/config.json');
        config = JSON.parse(configFile);
    } catch(e) {
    }

    if ( config.server == null )
        config.server = "http://54.194.45.176/";
    if (config.programs == null)
        config.programs = { };
    if (config.programs.make == null)
        config.programs.make = "make";
    if (config.programs.cmake == null)
        config.programs.cmake = "cmake";
    if (config.programs.git == null)
        config.programs.git = "git";

    return config;
}

var config = loadConfigurationFile();

const method = process.argv[2];
const library = process.argv[3];
const compiler = process.argv[4];
const mode = process.argv[5];

const zanderWorkingDirectory = process.cwd();
const cacheSourceDirectory = path.normalize(__dirname + "/cache/src");
const cacheBinaryDirectory = path.normalize(__dirname + "/cache/bin/" + library + "/gnu/debug");
const temporaryDirectory = path.normalize(__dirname + "/tmp/" + library);

var restClient = restify.createJsonClient({ url: config.server });
restClient.get("/projects/" + library, function(err, req, res, data) {

    if (!fs.existsSync(cacheSourceDirectory))
        fs.extra.mkdirpSync(cacheSourceDirectory);

    child_process.exec(config.programs.git + " clone " + data.git + " " + library, { cwd: cacheSourceDirectory }, function(err, stdout, stderr) {

        if (!fs.existsSync(temporaryDirectory))
            fs.extra.mkdirpSync(temporaryDirectory);

        child_process.exec(config.programs.cmake + " " + path.normalize(cacheSourceDirectory + "/" + library) + " -G\"MinGW Makefiles\" -DCMAKE_BUILD_TYPE=Debug -DCMAKE_INSTALL_DIRECTORY=" + path.normalize(cacheBinaryDirectory), { cwd: temporaryDirectory  }, function(err, stdout, stderr) {
            child_process.exec(config.programs.make, { cwd: temporaryDirectory  }, function(err, stdout, stderr) {
                child_process.exec(config.programs.make + " install", { cwd: temporaryDirectory  }, function(err, stdout, stderr) {
                    fs.extra.copyRecursive(temporaryDirectory, cacheBinaryDirectory, function(err) {
                        fs.extra.copyRecursive(cacheBinaryDirectory, zanderWorkingDirectory, function(err) {
                            process.exit();
                        });
                    });
                });
            });
        });
    });
});
