package com.heaven.soulmate.login.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

/**
 * Created by legol on 2015/10/24.
 */
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
@JsonInclude(value = JsonInclude.Include.NON_EMPTY)
public class QueryOnlineClientsResult {

    @JsonProperty("err_msg")
    public String errmsg;

    @JsonProperty("errno")
    public Long errno;

    @JsonProperty("clients")
    public ClientInfoList clients;

}
