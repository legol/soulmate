package com.heaven.soulmate.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

/**
 * Created by chenjie3 on 2015/11/6.
 */
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
@JsonInclude(value = JsonInclude.Include.NON_EMPTY)
public class Payload {

    @JsonProperty("type")
    public int type = 0;

    @JsonProperty("content")
    public String content = null;
}
