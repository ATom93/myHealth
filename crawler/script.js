var http = require('http');
var gplay = require('google-play-scraper');
var regex = /[^0-9\,]*/gi;
var regex_2 = /,/;
var handler = function(req, res) {
    req.on('data', function(data) {
        var req_json = JSON.parse(data);
        var category = req_json.category;
        gplay.list({
            category: category,
            collection: gplay.collection.TOP_FREE,
            country: 'it',
            lang: 'it',
            num: 5
        }).then(function(apps){
            res.writeHead(200, {"Content-Type": "application/json"});
            var json = [];
            for (i = 0; i < apps.length; i++){  
                var score = apps[i].scoreText.replace(regex, '');
                var score = score.replace(regex_2, '.');
                var icon = "https:" + apps[i].icon;
                json.push({
                    title: apps[i].title,
                    url: apps[i].url,
                    score: parseFloat(score),
                    icon: icon
                });
            }
            res.end(JSON.stringify(json));
        }).catch(function(e){
            res.end('There was an error fetching the application!');
        });
    });
};
var www = http.createServer(handler);
www.listen(8080);

/*
interesting CATEGORIES:
ANDROID_WEAR
AUTO_AND_VEHICLES
FOOD_AND_DRINK
HEALTH_AND_FITNESS
LIFESTYLE
MAPS_AND_NAVIGATION
MEDICAL
SPORTS
WEATHER
*/