// proxy.js

var request = require('superagent');
var backend_host = require('./backend.js');

module.exports = {
	task1: function (id, password, cb) {
		request
		.get(backend_host + ":8080/MiniSite/task1?id=" + id + "&pwd=" + password)
		.set('Accept', 'text/plain')
		.end(function(err, res) {
			if (res === undefined ||
				res.text === undefined) {
				cb('');
		} else {
			var s = res.text;
			cb(s.substring(10, s.length-1));
		}
	});
	},
	task2: function (id, cb) {
		request
		.get(backend_host + ":8080/MiniSite/task2?id=" + id)
		.set('Accept', 'text/plain')
		.end(function(err, res) {
			if (res === undefined ||
				res.text === undefined) {
				cb('');
		} else {
			var s = res.text;
			cb(s.substring(10, s.length-1));
		}
	});
	},
	task3: function (id, cb) {
		console.log(id);
		request
		.get(backend_host + ":8080/MiniSite/task3?id=" + id)
		.set('Accept', 'text/plain')
		.end(function(err, res) {
			if (res === undefined ||
				res.text === undefined) {
				cb('');
		} else {
			var s = res.text;
			cb(s.substring(10, s.length-1));
		}
	});
	},
	task4: function (id, cb) {
		request
		.get(backend_host + ":8080/MiniSite/task4?id=" + id)
		.set('Accept', 'text/plain')
		.end(function(err, res) {
			if (res === undefined ||
				res.text === undefined) {
				cb('');
		} else {
			var s = res.text;
			cb(s.substring(10, s.length-1));
		}
	});
	},
	task5: function (id, cb) {
		request
		.get(backend_host + ":8080/MiniSite/task5?id=" + id)
		.set('Accept', 'text/plain')
		.end(function(err, res) {
			if (res === undefined ||
				res.text === undefined) {
				cb('');
		} else {
			var s = res.text;
			cb(s.substring(10, s.length-1));
		}
	});
	}
};