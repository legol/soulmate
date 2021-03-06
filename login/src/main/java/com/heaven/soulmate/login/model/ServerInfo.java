package com.heaven.soulmate.login.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

/**
 * Created by chenjie3 on 2015/11/6.
 */
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
@JsonInclude(value = JsonInclude.Include.NON_EMPTY)
public class ServerInfo {
    @JsonProperty("role")
    public String role;

    @JsonProperty("ip")
    public String ip;

    @JsonProperty("url")
    public String url;

    @JsonProperty("portServer")
    public int portServer;

    @JsonProperty("portClient")
    public int portClient;

    @JsonProperty("uid_low")
    public long uidLow;

    @JsonProperty("uid_high")
    public int uidHigh;
}
