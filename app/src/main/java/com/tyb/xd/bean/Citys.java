package com.tyb.xd.bean;

import java.io.Serializable;
import java.util.List;

public class Citys implements Serializable {

    private String city;
    private List<Schools> schools;

    public Citys(String city, List<Schools> schools) {
        this.city = city;
        this.schools = schools;
    }

    public Citys() {
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public List<Schools> getSchools() {
        return schools;
    }

    public void setSchools(List<Schools> schools) {
        this.schools = schools;
    }

    public boolean equals(String citys) {
        return city.equals(citys);
    }
}

