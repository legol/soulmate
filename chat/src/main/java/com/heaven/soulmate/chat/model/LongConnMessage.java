package com.heaven.soulmate.chat.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

/**
 * Created by legol on 2015/11/8.
 */
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
@JsonInclude(value = JsonInclude.Include.NON_EMPTY)
public class LongConnMessage {

    @JsonProperty("err_no")
    private int errNo;

    @JsonProperty("err_msg")
    private String errMsg;

    @JsonProperty("type")
    private int type; // 1 -- client reg; 2 --  chat

    @JsonProperty("payload")
    private String payload; // will be delivered to payload

    public String getPayload() {
        return payload;
    }

    public int getType() {
        return type;
    }

    public String getErrMsg() {
        return errMsg;
    }

    public int getErrNo() {
        return errNo;
    }

    public void setPayload(String payload) {
        this.payload = payload;
    }

    public void setErrNo(int errNo) {
        this.errNo = errNo;
    }

    public void setErrMsg(String errMsg) {
        this.errMsg = errMsg;
    }

    public void setType(int type) {
        this.type = type;
    }
}
