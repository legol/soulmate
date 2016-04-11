package com.heaven.soulmate.websocket.model;

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
    public String errMsg;

    @JsonProperty("err_no")
    public int errNo;

    @JsonProperty("data")
    public Object data;
}
