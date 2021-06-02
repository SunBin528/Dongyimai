//基础控制层
app.controller('baseController',function ($scope) {
   //重新加载数据
    $scope.reloadList = function () {
        //切换页码要重新发起查询
        $scope.search($scope.paginationConf.currentPage,
            $scope.paginationConf.itemsPerPage);

    };
    //分页控件配置
    $scope.paginationConf = {
        currentPage: 1,
        totalItems: 10,
        itemsPerPage: 10,
        perPageOptions: [5, 10, 15, 20],
        onChange: function () {
            //重新加载数据
            $scope.reloadList();
        }
    };

    $scope.selectIds = [];//选中的ID集合
    //更新复选
    $scope.updateSelection = function($event,id){
        if($event.target.checked){//如果被选中，则增加到数组
            $scope.selectIds.push(id)
        }else{
            var idx = $scope.selectIds.indexOf(id);
            $scope.selectIds.splice(idx,1);//从数组中删除
        }
    }

    //提取JSON字符串数据中某个属性，返回拼接字符串 逗号分隔
    $scope.jsonToString = function (jsonString, key) {
        var json = JSON.parse(jsonString);//将json字符串转换为json对象

        var value = "";

        for(var i=0;i<json.length;i++){
            if(i>0){
                value+=",";
            }
            value+=json[i][key];
        }
        return value;
    };

    //从集合中按照key查询对象
    $scope.searchObjectByKey = function (list,key,keyValue){
        for(var i=0;i<list.length;i++){
            if(list[i][key]==keyValue){
                return list[i];
            }
        }
        return null;
    }
})