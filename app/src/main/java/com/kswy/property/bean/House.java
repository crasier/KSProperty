package com.kswy.property.bean;


import com.alibaba.fastjson.annotation.JSONField;

import java.io.Serializable;

/**
 * 住房信息
 * */
public class House implements Serializable{
    @JSONField
    private Integer id;
    @JSONField
    private String inName;
    @JSONField
    private String inNo;
    @JSONField
    private String buildId;
    @JSONField
    private String buildName;
    @JSONField
    private Integer unit;
    @JSONField
    private Integer floor;
    @JSONField
    private Float area;
    @JSONField
    private String phone;
    @JSONField
    private String cardID;

    @Override
    public String toString() {
        return "id:"+id+";inName:"+inName+";inNo:"+inNo+";buildId:"+buildId+";buildName:"+buildName
                +";unit:"+unit+";floor:"+floor+";area:"+area+";phone:"+phone+";cardId:"+cardID;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getInName() {
        return inName;
    }

    public void setInName(String inName) {
        this.inName = inName;
    }

    public String getInNo() {
        return inNo;
    }

    public void setInNo(String inNo) {
        this.inNo = inNo;
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

    public Integer getUnit() {
        return unit;
    }

    public void setUnit(Integer unit) {
        this.unit = unit;
    }

    public Integer getFloor() {
        return floor;
    }

    public void setFloor(Integer floor) {
        this.floor = floor;
    }

    public Float getArea() {
        return area;
    }

    public void setArea(Float area) {
        this.area = area;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getCardID() {
        return cardID;
    }

    public void setCardID(String cardID) {
        this.cardID = cardID;
    }
}
