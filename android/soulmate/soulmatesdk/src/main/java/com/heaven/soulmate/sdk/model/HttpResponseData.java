package com.heaven.soulmate.sdk.model;

import java.net.HttpURLConnection;

/**
 * Created by legol on 2015/11/15.
 */
public class HttpResponseData {
    private int httpStatus = HttpURLConnection.HTTP_NOT_FOUND;
    private String responseBody = null;

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
