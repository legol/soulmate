package com.heaven.soulmate.login.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

/**
 * Created by legol on 2015/10/24.
 */
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
@JsonInclude(value = JsonInclude.Include.NON_EMPTY)
public class LoginResult {

    @JsonProperty("token")
    private String token;

    @JsonProperty("uid")
    private Long uid;

    @JsonProperty("err_msg")
    private String errMsg;

    @JsonProperty("err_no")
    private Long errNo;

    @JsonProperty("longconn_ip")
    private String longconnIP;

    @JsonProperty("longconn_port")
    private int longconnPort;

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public Long getUid() {
        return uid;
    }

    public void setUid(Long uid) {
        this.uid = uid;
    }

    public String getErrMsg() {
        return errMsg;
    }

    public String getLongconnIP() {
        return longconnIP;
    }

    public int getLongconnPort() {
        return longconnPort;
    }

    public void setErrMsg(String errMsg) {
        this.errMsg = errMsg;
    }

    public Long getErrNo() {
        return errNo;
    }

    public void setErrNo(Long errNo) {
        this.errNo = errNo;
    }

    public void setLongconnIP(String longconnIP) {
        this.longconnIP = longconnIP;
    }

    public void setLongconnPort(int longconnPort) {
        this.longconnPort = longconnPort;
    }
}
