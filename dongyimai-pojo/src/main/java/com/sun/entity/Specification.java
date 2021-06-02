package com.sun.entity;

import com.sun.pojo.TbSpecification;
import com.sun.pojo.TbSpecificationOption;

import java.io.Serializable;
import java.util.List;

/**
 * @Description:
 * @Author: 孙斌
 * @Date: Create in 21:55 2021/3/31
 */
public class Specification implements Serializable {
    private TbSpecification specification;
    private List<TbSpecificationOption> specificationOptionList;

    public Specification() {
        super();
    }

    public Specification(TbSpecification specification, List<TbSpecificationOption> specificationOptionList) {
        this.specification = specification;
        this.specificationOptionList = specificationOptionList;
    }

    public TbSpecification getSpecification() {
        return specification;
    }

    public void setSpecification(TbSpecification specification) {
        this.specification = specification;
    }

    public List<TbSpecificationOption> getSpecificationOptionList() {
        return specificationOptionList;
    }

    public void setSpecificationOptionList(List<TbSpecificationOption> specificationOptionList) {
        this.specificationOptionList = specificationOptionList;
    }
}
