/**
 * Created by gbecan on 16/10/14.
 */


angular.module("openCompare")
    .controller("PCMListController", function($scope, $http) {
    $scope.pcmList = ["1" , "2" , "3"]

    $http.get("list").success(function(data) {
        $scope.pcmList = data
    });
});