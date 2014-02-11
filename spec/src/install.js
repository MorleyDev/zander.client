var mockprogram = require(__dirname + "/mockprogram.js");

mockprogram.startMocks([ mockprogram.createMock("make", __dirname + "/installMake.js") ]);

mockprogram.stopMocks();
