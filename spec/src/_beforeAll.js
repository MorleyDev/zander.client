var fs = require('fs');
fs.extra = require('fs.extra');
var path = require('path');

const configFilePath = path.normalize(__dirname + "/../../src/config.json");
const configSpecFilePath = path.normalize(__dirname + "/../../config/spec.config.json");

before(function (done) {
    if (fs.existsSync(configFilePath))
            fs.unlinkSync(configFilePath);

    fs.extra.copy(configSpecFilePath, configFilePath, done);
});
after(function (done) {
    done();
});

process.on('exit', function() {
    fs.extra.rmrfSync(__dirname + "/../../src/tmp");
    fs.extra.rmrfSync(__dirname + "/../../src/cache");
    fs.unlinkSync(configFilePath);
});
