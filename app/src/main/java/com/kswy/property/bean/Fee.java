package com.kswy.property.bean;

import android.os.Parcel;
import android.os.Parcelable;

import com.alibaba.fastjson.annotation.JSONField;

import java.io.Serializable;
import java.lang.annotation.Target;

/**
 * 费用明细
 * */
public class Fee implements Serializable{
    @JSONField(name = "id")
    private Integer id;
    @JSONField(name = "inNo")
    private String inNo;
    @JSONField(name = "inName")
    private String inName;
    @JSONField(name = "feeName")
    private String feeName;
    @JSONField(name = "buildId")
    private String buildId;
    @JSONField(name = "total")
    private String total;
    @JSONField(name = "price")
    private String price;
    @JSONField(name = "counts")
    private String counts;
    @JSONField(name = "lastCount")
    private String lastCount;
    @JSONField(name = "lastTime")
    private String lastTime;
    @JSONField(name = "thisCount")
    private String thisCount;
    @JSONField(name = "thisTime")
    private String thisTime;
    @JSONField(name = "payMonth")
    private String payMonth;
    @JSONField(name = "isPay")
    private String isPay;
    @JSONField(name = "creator")
    private String creator;
    @JSONField(name = "billFile")
    private String billFile;
    @JSONField(name = "payFile")
    private String payFile;
    @JSONField(name = "createTime")
    private String createTime;
    @JSONField(name = "remark")
    private String remark;

    private boolean hasTitle;//用来标记是否已经加了标题，不属于正常数据
    private String payMonthTo;//不是从服务器端获取的数据，用来存储缴费到的日期

    public Fee() {

    }


    @Override
    public String toString() {
        return "id:"+id+";inNo:"+inNo+";inName:"+inName+";feeName:"+feeName+";buildId:"+buildId+";total:"+total
                +";price:"+price+";counts:"+counts+";lastCount:"+lastCount+";lastTime:"+lastTime+";thisCount:"+thisCount
                +";thisTime:"+thisTime+";payMonth:"+payMonth+";payMonthTo:"+payMonthTo+";isPay:"+isPay+";creator:"+inNo+";creator:"+ billFile +";inNo:"+ billFile
                +";payFile:"+payFile+";createTime:"+createTime+";remark:"+remark;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getInNo() {
        return inNo;
    }

    public void setInNo(String inNo) {
        this.inNo = inNo;
    }

    public String getInName() {
        return inName;
    }

    public void setInName(String inName) {
        this.inName = inName;
    }

    public String getFeeName() {
        return feeName;
    }

    public void setFeeName(String feeName) {
        this.feeName = feeName;
    }

    public String getBuildId() {
        return buildId;
    }

    public void setBuildId(String buildId) {
        this.buildId = buildId;
    }

    public String getTotal() {
        return total;
    }

    public void setTotal(String total) {
        this.total = total;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getCounts() {
        return counts;
    }

    public void setCounts(String counts) {
        this.counts = counts;
    }

    public String getLastCount() {
        return lastCount;
    }

    public void setLastCount(String lastCount) {
        this.lastCount = lastCount;
    }

    public String getLastTime() {
        return lastTime;
    }

    public void setLastTime(String lastTime) {
        this.lastTime = lastTime;
    }

    public String getThisCount() {
        return thisCount;
    }

    public void setThisCount(String thisCount) {
        this.thisCount = thisCount;
    }

    public String getThisTime() {
        return thisTime;
    }

    public void setThisTime(String thisTime) {
        this.thisTime = thisTime;
    }

    public String getPayMonth() {
        return payMonth;
    }

    public void setPayMonth(String payMonth) {
        this.payMonth = payMonth;
    }

    public String getIsPay() {
        return isPay;
    }

    public void setIsPay(String isPay) {
        this.isPay = isPay;
    }

    public String getCreator() {
        return creator;
    }

    public void setCreator(String creator) {
        this.creator = creator;
    }

    public String getBillFile() {
        return billFile;
    }

    public void setBillFile(String billFile) {
        this.billFile = billFile;
    }

    public String getPayFile() {
        return payFile;
    }

    public void setPayFile(String payFile) {
        this.payFile = payFile;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public boolean isHasTitle() {
        return hasTitle;
    }

    public void setHasTitle(boolean hasTitle) {
        this.hasTitle = hasTitle;
    }

    public String getPayMonthTo() {
        return payMonthTo;
    }

    public void setPayMonthTo(String payMonthTo) {
        this.payMonthTo = payMonthTo;
    }
}


