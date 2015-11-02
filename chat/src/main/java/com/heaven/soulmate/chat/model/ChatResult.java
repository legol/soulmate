package com.heaven.soulmate.chat.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

/**
 * Created by ChenJie3 on 2015/10/30.
 */
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
@JsonInclude(value = JsonInclude.Include.NON_EMPTY)
public class ChatResult {
    @JsonProperty("err_no")
    private Long err_no;

    public Long getErr_no() {
        return err_no;
    }

    public void setErr_no(Long err_no) {
        this.err_no = err_no;
    }
}
