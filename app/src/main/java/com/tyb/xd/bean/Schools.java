package com.tyb.xd.bean;

import java.io.Serializable;
import java.util.List;

/**
 * 学校
 */
public class Schools implements Serializable {
    private String school;
    private List<Campuss> campuss;

    public Schools(List<Campuss> campuss, String school) {
        this.campuss = campuss;
        this.school = school;
    }

    public Schools() {
    }

    public void setCampuss(List<Campuss> campuss) {
        this.campuss = campuss;
    }

    public String getSchool() {
        return school;
    }

    public void setSchool(String school) {
        this.school = school;
    }

    public List<Campuss> getCampuss() {
        return campuss;
    }

    public boolean equals(String schools) {
        return school.equals(schools);
    }
}



