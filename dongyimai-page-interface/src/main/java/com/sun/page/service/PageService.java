package com.sun.page.service;

/**
 * 商品详细页接口
 */
public interface PageService {
    /**
     * 生成商品详细页
     * @param goodsId
     * @return
     */
    public boolean genItemHtml(Long goodsId);

    /**
     * 删除商品详情页
     * @param goodsId
     * @return
     */
    public boolean deleteItemHtml(Long[] goodsId);
}
