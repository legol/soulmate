package com.heaven.soulmate.sdk.model;

import android.os.AsyncTask;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by legol on 2015/11/15.
 */
public class HttpAsyncTask extends AsyncTask<HttpRequestData, Void, HttpResponseData> {

    private HttpRequestData requestData = null;
    private HttpResponseData responseData = null;

    private IHttpDelegate delegate = null;

    public IHttpDelegate getDelegate() {
        return delegate;
    }

    public void setDelegate(IHttpDelegate delegate) {
        this.delegate = delegate;
    }

    @Override
    protected HttpResponseData doInBackground(HttpRequestData... request) {

        assert(requestData == null);
        requestData = request[0];

        URL url;
        HttpURLConnection urlConn;


        DataInputStream input;

        StringBuilder sb = new StringBuilder();
        HttpResponseData response = new HttpResponseData();

        try {
            url = new URL(request[0].getUrl());
            urlConn = (HttpURLConnection)url.openConnection();
            urlConn.setDoInput(true);
            urlConn.setDoOutput(true);
            urlConn.setUseCaches(false);
            urlConn.setRequestProperty("Content-Type", "application/json");
            urlConn.connect();

            // Send POST output.
            String requestBody = request[0].getRequestBody();
            if (requestBody!= null){
                DataOutputStream printout = new DataOutputStream(urlConn.getOutputStream());
                printout.write(requestBody.getBytes());
                printout.flush();
                printout.close();
            }

            // process response
            int httpResult = urlConn.getResponseCode();
            response.setHttpStatus(httpResult);

            if(httpResult == HttpURLConnection.HTTP_OK){
                BufferedReader br = new BufferedReader(new InputStreamReader(
                        urlConn.getInputStream(),"utf-8"));
                String line = null;
                while ((line = br.readLine()) != null) {
                    sb.append(line + "\n");
                }
                br.close();

                response.setResponseBody(sb.toString());
            }

            urlConn.disconnect();

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return response;
    }


    @Override
    protected void onPostExecute(HttpResponseData result) {
        if (delegate != null){
            delegate.onHttpResponse(requestData, result);
        }
    }
}