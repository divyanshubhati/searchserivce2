package com.zotdrive.searchservice.search;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.util.Date;

public class SearchRequestDTO {

    String userId;
    String keyword;
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date createdAfter;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date createdBefore;

    boolean deleted;
    String parentId;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getKeyword() {
        return keyword;
    }

    public Date getCreatedBefore() {
        return createdBefore;
    }

    public void setCreatedBefore(Date createdBefore) {
        this.createdBefore = createdBefore;
    }

    public boolean isDeleted() {
        return deleted;
    }

    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }

    public String getParentId() {
        return parentId;
    }

    public void setParentId(String parentId) {
        this.parentId = parentId;
    }

    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }

    public Date getCreatedAfter() {
        return createdAfter;
    }

    public void setCreatedAfter(Date createdAfter) {
        this.createdAfter = createdAfter;
    }
}
