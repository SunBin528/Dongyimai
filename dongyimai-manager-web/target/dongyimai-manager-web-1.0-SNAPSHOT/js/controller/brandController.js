app.controller('brandController',function($scope,$http,$controller,brandService) {

    $controller('baseController',{$scope:$scope});//控制继承
    //读取列表数据绑定到表单
    $scope.findAll = function () {
        brandService.findAll().success(
            function (response) {
            $scope.list = response;
        })
    };

    $scope.findPage = function (page, rows) {
        brandService.findPage(page,rows).success(function (response) {
            $scope.list = response.rows;
            $scope.paginationConf.totalItems = response.total;
        })
    };

    //保存
    $scope.save=function(){
        //var methodName="add";//方法名称
        if($scope.entity.id != null){//如果有ID
            //则执行修改方法
            brandService.update($scope.entity).success(function(response){
                if(response.success){
                    //重新查询
                    $scope.reloadList();//重新加载
                }else{
                    alert(response.message);
                }
            });
        }else{
            brandService.add($scope.entity).success(function(response){
                if(response.success){
                    //重新查询
                    $scope.reloadList();//重新加载
                }else{
                    alert(response.message);
                }
            });
        }

    }

    $scope.findOne = function (id) {
        brandService.findOne(id).success(
            function (response) { //response=tbbrand
                $scope.entity = response;
            }
        );
    }


    //批量删除
    $scope.delete = function () {
        //获取选中的复选框
        brandService.delete($scope.selectIds).success(
            function (response) {
                if(response.success){
                    $scope.reloadList();//刷新列表
                }
            }
        );
    }

    //条件查询
    $scope.searchEntity = {};//定义搜索对象

    $scope.search = function (page,rows) {
        brandService.search(page,rows,$scope.searchEntity).success(
            function (response) {
                $scope.paginationConf.totalItems = response.total;//总记录数
                $scope.list = response.rows;//给列表标量赋值
            }
        );
    }

    //刷新列表

})