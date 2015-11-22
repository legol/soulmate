package com.heaven.soulmate.reg.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

/**
 * Created by legol on 2015/11/22.
 */
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
@JsonInclude(value = JsonInclude.Include.NON_EMPTY)
public class RegRequest {
    @JsonProperty("phone")
    private String phone;

    @JsonProperty("password")
    private String password;

    public String getPhone() {
        return phone;
    }

    public String getPassword() {
        return password;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
