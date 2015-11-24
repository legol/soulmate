package com.heaven.soulmate.sdk.model.longconn;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

/**
 * Created by ChenJie3 on 2015/11/20.
 */
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
@JsonInclude(value = JsonInclude.Include.NON_EMPTY)
public class LongConnRegisterMessage {
    @JsonProperty("uid")
    private long uid;

    @JsonProperty("token")
    private String token;

    public long getUid() {
        return uid;
    }

    public String getToken() {
        return token;
    }

    public void setUid(long uid) {
        this.uid = uid;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
