package com.sun.entity;

import java.io.Serializable;
import java.util.List;

/**
 * @Description:
 * @Author: 孙斌
 * @Date: Create in 19:29 2021/3/30
 */
public class PageResult implements Serializable {

    //总记录数
    private long total;

    //分页的结果集
    private List rows;

    public PageResult() {
        super();
    }

    public PageResult(long total, List rows) {
        this.total = total;
        this.rows = rows;
    }

    public long getTotal() {
        return total;
    }

    public void setTotal(long total) {
        this.total = total;
    }

    public List getRows() {
        return rows;
    }

    public void setRows(List rows) {
        this.rows = rows;
    }
}
