package com.heaven.soulmate.login.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import java.util.List;

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

    @JsonProperty("servers")
    public ServerInfoList servers;

    public ServerInfoList getServers() {
        return servers;
    }

    public void setServers(ServerInfoList servers) {
        this.servers = servers;
    }

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
