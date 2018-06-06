package com.kswy.property.bean;

import com.alibaba.fastjson.annotation.JSONField;

import java.io.Serializable;

/**
 * 费用明细
 * */
public class Fee implements Serializable{
    @JSONField
    private Integer id;
    @JSONField
    private String inNo;
    @JSONField
    private String inName;
    @JSONField
    private String feeName;
    @JSONField
    private String buildId;
    @JSONField
    private String total;
    @JSONField
    private String price;
    @JSONField
    private String counts;
    @JSONField
    private String lastCount;
    @JSONField
    private String lastTime;
    @JSONField
    private String thisCount;
    @JSONField
    private String thisTime;
    @JSONField
    private String payMonth;
    @JSONField
    private String isPay;
    @JSONField
    private String creator;
    @JSONField
    private String billFile;
    @JSONField
    private String payFile;
    @JSONField
    private String createTime;
    @JSONField
    private String remark;

    private boolean hasTitle;//用来标记是否已经加了标题，不属于正常数据

    @Override
    public String toString() {
        return "id:"+id+";inNo:"+inNo+";inName:"+inName+";feeName:"+feeName+";buildId:"+buildId+";total:"+total
                +";price:"+price+";counts:"+counts+";lastCount:"+lastCount+";lastTime:"+lastTime+";thisCount:"+thisCount
                +";thisTime:"+thisTime+";isPay:"+isPay+";creator:"+inNo+";creator:"+ billFile +";inNo:"+ billFile
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
}


