package com.heaven.soulmate.login.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

/**
 * Created by ChenJie3 on 2015/11/19.
 */
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
@JsonInclude(value = JsonInclude.Include.NON_EMPTY)
public class HttpResult {
    @JsonProperty("err_msg")
    private String errMsg;

    @JsonProperty("err_no")
    private Long errNo;

    @JsonProperty("data")
    private Object data;

    public String getErrMsg() {
        return errMsg;
    }

    public Long getErrNo() {
        return errNo;
    }

    public void setErrMsg(String errMsg) {
        this.errMsg = errMsg;
    }

    public void setErrNo(Long errNo) {
        this.errNo = errNo;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }
}
