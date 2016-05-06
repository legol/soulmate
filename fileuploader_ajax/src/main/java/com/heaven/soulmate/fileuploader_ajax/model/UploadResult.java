package com.heaven.soulmate.fileuploader_ajax.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import java.util.List;

/**
 * Created by legol on 2015/10/24.
 */
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
@JsonInclude(value = JsonInclude.Include.NON_EMPTY)
public class UploadResult {

    @JsonProperty("err_msg")
    public String errMsg;

    @JsonProperty("err_no")
    public int errNo;

    @JsonProperty("data")
    public List<UploadResultItem> data;
}

