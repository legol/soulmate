package com.heaven.soulmate.login.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

/**
 * Created by ChenJie3 on 2015/11/19.
 */
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
@JsonInclude(value = JsonInclude.Include.NON_EMPTY)
public class QueryOnlineClientsRequest {
    @JsonProperty("uid")
    public long uid;

    @JsonProperty("token")
    public String token;
}