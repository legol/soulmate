package com.heaven.soulmate.sdk.model;

/**
 * Created by legol on 2015/11/15.
 */
public class HttpRequestData {
    private String url;
    private String requestBody;

    public String getRequestBody() {
        return requestBody;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public void setRequestBody(String requestBody) {
        this.requestBody = requestBody;
    }
}
