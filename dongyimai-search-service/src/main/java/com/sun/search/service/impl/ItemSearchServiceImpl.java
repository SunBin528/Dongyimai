package com.sun.search.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import com.github.promeg.pinyinhelper.Pinyin;
import com.sun.pojo.TbItem;
import com.sun.search.sercive.ItemSearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.data.solr.core.query.*;
import org.springframework.data.solr.core.query.result.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service(timeout = 3000)
public class ItemSearchServiceImpl implements ItemSearchService{

    @Autowired
    private SolrTemplate solrTemplate;
    @Autowired
    private RedisTemplate redisTemplate;

    @Override
    public Map<String,Object> search(Map searchMap) {
        Map<String,Object> map = new HashMap<>();
        //1.按关键字查询 高亮显示
        map.putAll(searchList(searchMap));
        //2.根据关键字查询商品分类
        List categoryList = searchCategoryList(searchMap);
        map.put("categoryList",categoryList);
        //3、根据商品类目查询对应的品牌、规格
        //读取分类名称
        String categoryName = (String) searchMap.get("category");
        if(!"".equals(categoryName)){
            //按照分类名称重新读取对应品牌、规格
            map.putAll(searchBrandAndSpecList(categoryName));
        }else {
            if(categoryList.size()>0){
                Map brandAndSpecList = searchBrandAndSpecList((String) categoryList.get(0));
                map.putAll(brandAndSpecList);
            }
        }


        return map;
    }

    @Override
    public void importList(List<TbItem> list) {
        for (TbItem item : list) {
            System.out.println(item.getTitle());
            //从数据库中提取规格json字符串转换为map
            Map<String,String> specMap = JSON.parseObject(item.getSpec(), Map.class);

            Map map = new HashMap();
            for (String key : specMap.keySet()) {
                map.put("item_spec_"+Pinyin.toPinyin(key, "").toLowerCase(), specMap.get(key));
            }
            item.setSpecMap(map); //给带动态域注解的字段赋值
        }
        solrTemplate.saveBeans(list);
        solrTemplate.commit();
    }

    @Override
    public void deleteByGoodsIds(List goodsIdList) {
        System.out.println("删除商品id"+goodsIdList);
        Query query= new SimpleQuery();
        Criteria criteria = new Criteria("item_goodsid").in(goodsIdList);
        query.addCriteria(criteria);
        solrTemplate.delete(query);
        solrTemplate.commit();
    }

    /**
     * 搜索关键字  高亮
     * @param searchMap
     * @return
     */
    private Map searchList(Map searchMap){
        Map map=new HashMap();

        //1、创建一个支持高亮查询器对象
        SimpleHighlightQuery query = new SimpleHighlightQuery();
        //2、设定需要高亮处理字段
        HighlightOptions highlightOptions = new HighlightOptions();
        highlightOptions.addField("item_title");
        //3、设置高亮前缀
        highlightOptions.setSimplePrefix("<em style='color:red'>");
        //4、设置高亮后缀
        highlightOptions.setSimplePostfix("</em>");
        //5、关联高亮选项到高亮查询器对象
        query.setHighlightOptions(highlightOptions);
        /**
         * 按照关键字查询
         */
        //6、设定查询条件 根据关键字查询
        //创建查询条件对象
        Criteria criteria = new Criteria("item_keywords").is(searchMap.get("keywords"));
        //关联查询条件到查询器对象
        query.addCriteria(criteria);
        /**
         * 按照分类筛选
         */
        if(!"".equals(searchMap.get("category"))){
            Criteria filterCri = new Criteria("item_category").is(searchMap.get("category"));
            SimpleFilterQuery filterQuery = new SimpleFilterQuery(filterCri);
            query.addFilterQuery(filterQuery);
        }
        /**
         * 按品牌筛选
         */
        if(!"".equals(searchMap.get("brand"))){
            Criteria filterCri = new Criteria("item_brand").is(searchMap.get("brand"));
            SimpleFilterQuery filterQuery = new SimpleFilterQuery(filterCri);
            query.addFilterQuery(filterQuery);
        }
        /**
         * 按照规格过滤筛选
         */
        if(searchMap.get("spec")!=null){
            Map<String,String> specMap = (Map) searchMap.get("spec");
            for (String key : specMap.keySet()) {
                Criteria filterCri =
                        new Criteria("item_spec_" + Pinyin.toPinyin(key, "").toLowerCase()).is(searchMap.get(key));
                SimpleFilterQuery filterQuery = new SimpleFilterQuery(filterCri);
                query.addFilterQuery(filterQuery);
            }

        }
        /**
         * 按价格筛选
         */
        if(!"".equals(searchMap.get("price"))){
            String[] price = ((String)searchMap.get("price")).split("-");
            if(!price[0].equals("0")){//如果区间起点不等于0
                Criteria filterCri = new Criteria("item_price").greaterThanEqual(price[0]);
                SimpleFilterQuery filterQuery = new SimpleFilterQuery(filterCri);
                query.addFilterQuery(filterQuery);
            }
            if(!price[1].equals("*")){//如果区间终点不等于*
                Criteria filterCri = new Criteria("item_price").lessThanEqual(price[1]);
                SimpleFilterQuery filterQuery = new SimpleFilterQuery(filterCri);
                query.addFilterQuery(filterQuery);
            }
        }
        /**
         * 分页查询
         */
        Integer pageNo = (Integer) searchMap.get("pageNo");//提取页码
        if(pageNo==null){
            pageNo = 1;//默认第一页
        }
        Integer pageSize = (Integer) searchMap.get("pageSize");//每页多少条
        if(pageSize==null){
            pageSize = 20;//默认20
        }
        query.setOffset((pageNo-1)*pageSize);//从第几天记录查询
        query.setRows(pageSize);
        /**
         * 按照价格排序
         */
        String sortValue = (String) searchMap.get("sort");
        String sortField = (String) searchMap.get("sortField");//排序字段
        if(sortValue!=null && !sortValue.equals("")){
            if(sortValue.equals("ASC")){
                Sort sort = new Sort(Sort.Direction.ASC,"item_"+sortField);
                query.addSort(sort);
            }
            if(sortValue.equals("DESC")){
                Sort sort = new Sort(Sort.Direction.DESC,"item_"+sortField);
                query.addSort(sort);
            }
        }

        /**
         * 下面是高亮显示处理
         */
        //7、发出带高亮数据查询请求
        HighlightPage<TbItem> page = solrTemplate.queryForHighlightPage(query, TbItem.class);
        //8、获取高亮集合入口
        List<HighlightEntry<TbItem>> highlightEntryList = page.getHighlighted();
        //9、遍历高亮集合
        for(HighlightEntry<TbItem> highlightEntry:highlightEntryList){
            //获取基本数据对象
            TbItem tbItem = highlightEntry.getEntity();
            if(highlightEntry.getHighlights().size()>0&&highlightEntry.getHighlights().get(0).getSnipplets().size()>0) {
                List<HighlightEntry.Highlight> highlightList = highlightEntry.getHighlights();
                //高亮结果集合
                List<String> snipplets = highlightList.get(0).getSnipplets();
                //获取第一个高亮字段对应的高亮结果，设置到商品标题
                tbItem.setTitle(snipplets.get(0));
            }

        }

        //把带高亮数据集合存放map
        map.put("rows",page.getContent());
        map.put("totalPages",page.getTotalPages());//返回总页数
        map.put("total",page.getTotalElements());//返回总记录数
        return map;
    }

    /**
     * 查询分类列表
     * @param searchMap
     * @return
     */
    private List searchCategoryList(Map searchMap){
        List<String> list = new ArrayList<>();
        Query query = new SimpleQuery();
        //按照关键字查询
        Criteria criteria = new Criteria("item_keywords").is(searchMap.get("keywords"));
        query.addCriteria(criteria);
        //设置分组选项
        GroupOptions groupOptions = new GroupOptions().addGroupByField("item_category");
        query.setGroupOptions(groupOptions);//添加到查询条件中
        //得到分组页
        GroupPage<TbItem> page = solrTemplate.queryForGroupPage(query, TbItem.class);
        //根据列得到分类结果集
        GroupResult<TbItem> groupResult = page.getGroupResult("item_category");
        //得到分组结果入口页
        Page<GroupEntry<TbItem>> groupEntries = groupResult.getGroupEntries();
        //得到分组入口集合
        List<GroupEntry<TbItem>> content = groupEntries.getContent();
        for (GroupEntry<TbItem> entry : content) {
            //将分组结果的名称封装到返回值中
            list.add(entry.getGroupValue());

        }
        return list;
    }

    /**
     * 查询品牌和规格列表
     * @param category
     * @return
     */
    private Map searchBrandAndSpecList(String category){
        Map map = new HashMap();
        //获取模板id
        Long typeId = (Long) redisTemplate.boundHashOps("itemCat").get(category);
       if(typeId!=null){
           //根据模板id查询品牌列表
           List brandList = (List) redisTemplate.boundHashOps("brandList").get(typeId);
           map.put("brandList",brandList);//返回值添加列表

           //根据模板id查询品牌列表
           List specList = (List) redisTemplate.boundHashOps("specList").get(typeId);
           map.put("specList",specList);
       }
        return map;
    }


}
