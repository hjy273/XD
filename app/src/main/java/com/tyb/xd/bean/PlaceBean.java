package com.tyb.xd.bean;

import java.io.Serializable;
import java.util.List;

/**
 * 选择地方的
 */
public class PlaceBean implements Serializable {

    private List<Citys> citys;

    public PlaceBean() {
    }
    public PlaceBean(List<Citys> citys) {
        this.citys = citys;
    }

    public List<Citys> getCitys() {
        return citys;
    }
    public void setCitys(List<Citys> citys) {
        this.citys = citys;
    }
}


