var fs = require('fs');
var child_process = require('child_process');

function loadConfigurationFile() {
    var config = { };
    try {
        const configFile = fs.readFileSync(__dirname + '/config.json');
        config = JSON.parse(configFile);
    } catch(e) {
    }

    if (config.programs == null)
        config.programs = { };
    if (config.programs.make == null)
        config.programs.make = "make";
    return config;
}

var config = loadConfigurationFile();
child_process.exec(config.programs.make);
