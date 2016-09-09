(function() {

var app = angular.module('demo', []);

app.controller('Hello', function($scope, $http) {
    $http.get('http://54.227.167.51\:8080/greeting?name=frank').
        then(function(response) {
            $scope.greeting = response.data;
        });
});
})();
