package com.heaven.soulmate.model;

import org.json.JSONObject;

/**
 * Created by legol on 2015/11/15.
 */
public class HttpRequestData {
    private String url;
    private JSONObject request;

    public JSONObject getRequest() {
        return request;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public void setRequest(JSONObject request) {
        this.request = request;
    }
}
