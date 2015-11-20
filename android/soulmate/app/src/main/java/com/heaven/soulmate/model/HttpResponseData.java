package com.heaven.soulmate.model;

import org.json.JSONObject;

/**
 * Created by legol on 2015/11/15.
 */
public class HttpResponseData {
    private int httpStatus;
    private String responseBody;

    public String getResponseBody() {
        return responseBody;
    }
    public void setResponseBody(String responseBody) {
        this.responseBody = responseBody;
    }

    public int getHttpStatus() {
        return httpStatus;
    }

    public void setHttpStatus(int httpStatus) {
        this.httpStatus = httpStatus;
    }
}
