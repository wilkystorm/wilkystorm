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
	
	app.controller("RestController", function($scope, $http) {
		this.getFrank = function() { $http({
		    url: "http://localhost:8080/greeting", 
		    method: "GET",
		    params: {name: frank}
		 }).then(function(response) {
            		$scope.greeting = response.data;
        	 });};
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
