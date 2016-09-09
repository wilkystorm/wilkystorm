(function() {

	var app = angular.module("storm",[]);

	app.controller("StormController", function() {
		this.products = varietals;
	});

	app.controller("PanelController", function() {
		this.tab = 1;
		this.setTab = function(newValue) {
			this.tab = newValue;
	    	};
	    	this.isSet = function(tabName) {
	      		return this.tab === tabName;
	    	};
		this.getAlign = function() {
	      		return "center";
	    	};
	});
	
	app.controller('RestController', function($scope, $http) {
		$http.get('http://54.227.167.51/greeting?name=frank').then(function(response) {
            		$scope.frankGreeting = response.data;
        	 });
		$http.get('http://54.227.167.51/greeting?name=randy').then(function(response) {
            		$scope.randyGreeting = response.data;
        	 });
		$http.get('http://54.227.167.51/greeting?name=fred').then(function(response) {
            		$scope.fredGreeting = response.data;
        	 });
	});

	var varietals = [{
	      name: 'linkedin',
	      description: "linkedin account",
              url: "https://www.linkedin.com/in/wilkystorm",
	      images: [
		"img/MeWork.jpg",
		"img/githubkitty.png",
		"img/Me.jpg"
	      ]
	    }, {
	      name: 'github',
	      description: "github account",
              url: "https://github.com/wilkystorm",
	      images: [
		"img/MeWork.jpg",
		"img/githubkitty.png",
		"img/Me.jpg"
	      ]
	    }];
})();
