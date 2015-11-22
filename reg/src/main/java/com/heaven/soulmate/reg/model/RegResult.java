package com.heaven.soulmate.reg.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

/**
 * Created by legol on 2015/11/22.
 */
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
@JsonInclude(value = JsonInclude.Include.NON_EMPTY)
public class RegResult {
    @JsonProperty("err_no")
    private long errNo;

    @JsonProperty("err_msg")
    private String errMsg;

    @JsonProperty("uid")
    private long uid;

    public long getErrNo() {
        return errNo;
    }

    public String getErrMsg() {
        return errMsg;
    }

    public long getUid() {
        return uid;
    }

    public void setErrNo(long errNo) {
        this.errNo = errNo;
    }

    public void setErrMsg(String errMsg) {
        this.errMsg = errMsg;
    }

    public void setUid(long uid) {
        this.uid = uid;
    }
}
