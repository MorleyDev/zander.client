var config = JSON.parse(require('fs').readFileSync(__dirname + '/config.json'));
console.log(config);
if (config.programs == null)
    config.programs = { };
if (config.programs.make == null)
    config.programs.make = "make";
console.log(config.programs.make);

require('child_process').exec(config.programs.make);
