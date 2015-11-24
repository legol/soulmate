package com.heaven.soulmate.sdk.model.login;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

/**
 * Created by legol on 2015/10/24.
 */
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
@JsonInclude(value = JsonInclude.Include.NON_EMPTY)
public class LoginResult{

    @JsonIgnore
    private int loginErrNo;

    @JsonIgnore
    private String loginErrMsg;

    @JsonProperty("token")
    private String token;

    @JsonProperty("uid")
    private Long uid;

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

    public String getLongconnIP() {
        return longconnIP;
    }

    public int getLongconnPort() {
        return longconnPort;
    }

    public void setLongconnIP(String longconnIP) {
        this.longconnIP = longconnIP;
    }

    public void setLongconnPort(int longconnPort) {
        this.longconnPort = longconnPort;
    }

    public int getLoginErrNo() {
        return loginErrNo;
    }

    public String getLoginErrMsg() {
        return loginErrMsg;
    }

    public void setLoginErrNo(int loginErrNo) {
        this.loginErrNo = loginErrNo;
    }

    public void setLoginErrMsg(String loginErrMsg) {
        this.loginErrMsg = loginErrMsg;
    }
}
