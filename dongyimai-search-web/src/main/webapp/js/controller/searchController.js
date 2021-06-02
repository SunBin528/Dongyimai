app.controller('searchController',function ($scope,$location,searchService) {

    //搜索
    $scope.search = function () {
        $scope.searchMap.pageNo = parseInt($scope.searchMap.pageNo);
        searchService.search($scope.searchMap).success(
            function (response) {
                $scope.resultMap = response;//返回搜索结果
                bulidPageLable();//调用分页
        });
    };

    //添加搜索项方法
    $scope.searchMap = {//搜索对象
        'keywords':'',
        'category':'',
        'brand':'',
        'spec':{},
        'pageNo':1,
        'pageSize':10,
        'sort':'',
        'sortField':'',
        'price':''
    };

    $scope.addSearchItem = function (key,value ) {
        $scope.searchMap.pageNo=1;//初始化当前页为1
        if(key=='category' || key=='brand' || key=='price'){
            $scope.searchMap[key] = value;
        }else {
            $scope.searchMap.spec[key] = value;
        }
        $scope.search();//执行搜索
    };
    //撤销搜索项
    $scope.deleteSearchItem = function (key) {
        $scope.searchMap.pageNo=1;//初始化当前页为1
        if(key=='category' || key=='brand' || key=='price'){
            $scope.searchMap[key] = "";
        }else {
          delete  $scope.searchMap.spec[key] ;
        }
        $scope.search();//执行搜索
    };
    $scope.keywordIsBrand = function(){
        for(var i = 0; i < $scope.resultMap.brandList.length; i++){
            if($scope.searchMap.keywords.indexOf($scope.resultMap.brandList[i].text) >= 0){
                return true;
            }
        }
        return false;
    }
    //构建分页标签
    bulidPageLable = function () {
        $scope.pageLable = [];//新增分页栏属性
        var maxPageNo = $scope.resultMap.totalPages;//最后页码
        var firstPage = 1;//开始页码
        var lastPage = maxPageNo;//截止页码
        $scope.firstDot = true;//前面有点
        $scope.lastDot = true;//后面有点
        if($scope.resultMap.totalPages > 5){//如果总页码大于5，显示部分页码
            if($scope.searchMap.pageNo <= 3){//当前页小于等于3
                lastPage = 5;//前5页
                $scope.firstDot = false;
            }else if($scope.searchMap.pageNo>=lastPage-2){//当前页大于等于最大页码-2
                firstPage = maxPageNo-4;//后5页
                $scope.lastDot = false;
            }else{
                firstPage = $scope.searchMap.pageNo-2;
                lastPage = $scope.searchMap.pageNo+2;
            }
        }else {
            $scope.firstDot = false;
            $scope.lastDot = false
        }

        //循环产生页码标签
        for(var i = firstPage; i <= lastPage; i++){
            $scope.pageLable.push(i);
        }

    }
    //根据页码查询
    $scope.queryByPage=function(pageNo){
        //页码验证
        if(pageNo<1 || pageNo>$scope.resultMap.totalPages){
            return;
        }
        $scope.searchMap.pageNo=pageNo;
        $scope.search();
    }
    //判断当前页为第一页
    $scope.isTopPage = function () {
        if($scope.searchMap.pageNo==1){
            return true;
        }else {
            return false;
        }
    }

    //判断指定页码是否是当前页
    $scope.ispage = function (page) {
        if(parseInt(page)==parseInt($scope.searchMap.pageNo)){
            return true;
        }else {
            return false;
        }
    }

    //设置排序规则
    $scope.sortSearch = function (sortFiled,sort) {
        $scope.searchMap.sortField = sortFiled;
        $scope.searchMap.sort = sort;
        $scope.search();
    };

    //加载查询字符串
    $scope.loadkeywords = function () {
        $scope.searchMap.keywords = $location.search()['keywords'];
        $scope.search();
    }
})