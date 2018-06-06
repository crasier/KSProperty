package com.kswy.property.bean;

import com.alibaba.fastjson.annotation.JSONField;

import java.io.Serializable;
import java.util.ArrayList;

public class Inhabitant implements Serializable {

    @JSONField
    private Integer buildId;

    @JSONField
    private String buildName;

    private ArrayList<House> inhats;

    @Override
    public String toString() {
        return "buildId:"+buildId+";buildName:"+buildName+";inhabitants:"+inhats;
    }

    public Integer getBuildId() {
        return buildId;
    }

    public void setBuildId(Integer buildId) {
        this.buildId = buildId;
    }

    public String getBuildName() {
        return buildName;
    }

    public void setBuildName(String buildName) {
        this.buildName = buildName;
    }

    public ArrayList<House> getInhats() {
        return inhats;
    }

    public void setInhats(ArrayList<House> inhats) {
        this.inhats = inhats;
    }
}
