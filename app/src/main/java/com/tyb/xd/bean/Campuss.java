package com.tyb.xd.bean;

import java.io.Serializable;
import java.util.List;

public class Campuss implements Serializable {
    private String campus;
    private List<Types> types;

    public Campuss() {
    }

    public Campuss(String campus, List<Types> types) {
        this.campus = campus;
        this.types = types;
    }


    public String getCampus() {
        return campus;
    }

    public List<Types> getTypes() {
        return types;
    }

    public void setCampus(String campus) {
        this.campus = campus;
    }

    public void setTypes(List<Types> types) {
        this.types = types;
    }

    public boolean equals(String campuss) {
        return campus.equals(campuss);
    }
}
