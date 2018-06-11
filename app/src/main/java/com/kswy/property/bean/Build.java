package com.kswy.property.bean;

import com.alibaba.fastjson.annotation.JSONField;

import java.io.Serializable;

/**
 * 楼宇信息
 * */
public class Build implements Serializable{
    @JSONField(name = "id")
    private Integer id;
    @JSONField(name = "buildId")
    private String buildId;
    @JSONField(name = "buildName")
    private String buildName;
    @JSONField(name = "buildStru")
    private String buildStru;
    @JSONField(name = "unitCount")
    private Integer unitCount;
    @JSONField(name = "floorCount")
    private Integer floorCount;
    @JSONField(name = "houseCount")
    private Integer houseCount;

    @Override
    public String toString() {
        return "id:"+id+";buildId:"+buildId+";buildName:"+buildName+";buildStru:"+buildStru
                +";unitCount:"+unitCount+";floorCount:"+floorCount+";houseCount:"+houseCount;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getBuildId() {
        return buildId;
    }

    public void setBuildId(String buildId) {
        this.buildId = buildId;
    }

    public String getBuildName() {
        return buildName;
    }

    public void setBuildName(String buildName) {
        this.buildName = buildName;
    }

    public String getBuildStru() {
        return buildStru;
    }

    public void setBuildStru(String buildStru) {
        this.buildStru = buildStru;
    }

    public Integer getUnitCount() {
        return unitCount;
    }

    public void setUnitCount(Integer unitCount) {
        this.unitCount = unitCount;
    }

    public Integer getFloorCount() {
        return floorCount;
    }

    public void setFloorCount(Integer floorCount) {
        this.floorCount = floorCount;
    }

    public Integer getHouseCount() {
        return houseCount;
    }

    public void setHouseCount(Integer houseCount) {
        this.houseCount = houseCount;
    }
}
