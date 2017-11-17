
package com.tntrip.mob.askq.biz.qa.vo;

import java.util.Date;

public class TagVO {
    private int id;
    private String name;
    private int tagType;
    private String tagValue;
    private String description;
    private int hot;
    private int enabled;
    private int creatorCustomerId;
    private String creatorCustomerName;
    private int editorCustomerId;
    private String editorCustomerName;
    private Date createTime;
    private Date updateTime;
    private int delFlag;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getTagType() {
        return tagType;
    }

    public void setTagType(int tagType) {
        this.tagType = tagType;
    }

    public String getTagValue() {
        return tagValue;
    }

    public void setTagValue(String tagValue) {
        this.tagValue = tagValue;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getHot() {
        return hot;
    }

    public void setHot(int hot) {
        this.hot = hot;
    }

    public int getEnabled() {
        return enabled;
    }

    public void setEnabled(int enabled) {
        this.enabled = enabled;
    }

    public int getCreatorCustomerId() {
        return creatorCustomerId;
    }

    public void setCreatorCustomerId(int creatorCustomerId) {
        this.creatorCustomerId = creatorCustomerId;
    }

    public String getCreatorCustomerName() {
        return creatorCustomerName;
    }

    public void setCreatorCustomerName(String creatorCustomerName) {
        this.creatorCustomerName = creatorCustomerName;
    }

    public int getEditorCustomerId() {
        return editorCustomerId;
    }

    public void setEditorCustomerId(int editorCustomerId) {
        this.editorCustomerId = editorCustomerId;
    }

    public String getEditorCustomerName() {
        return editorCustomerName;
    }

    public void setEditorCustomerName(String editorCustomerName) {
        this.editorCustomerName = editorCustomerName;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public int getDelFlag() {
        return delFlag;
    }

    public void setDelFlag(int delFlag) {
        this.delFlag = delFlag;
    }
}
