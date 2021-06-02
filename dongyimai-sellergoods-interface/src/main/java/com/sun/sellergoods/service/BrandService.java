package com.sun.sellergoods.service;

import com.sun.entity.PageResult;
import com.sun.pojo.TbBrand;

import java.util.List;
import java.util.Map;

/**
 * @Description:
 * @Author: 孙斌
 * @Date: Create in 15:38 2021/3/30
 */
public interface BrandService {

    public List<TbBrand> findAll();

    //分页
    public PageResult findPage(int PageNum, int PageSize);

    //添加
    public void add(TbBrand brand);

    //根据ID获取实体
    public TbBrand findOne(Long id);

    //修改
    public void update(TbBrand brand);

    //批量删除
    public void delete(Long [] ids);
    //查询 PageNum:当前页码 PageSize：每页记录数
    public PageResult findPage(TbBrand brand,int PageNum,int PageSize);

    //品牌下拉框数据
    List<Map> selectOptionList();
}
