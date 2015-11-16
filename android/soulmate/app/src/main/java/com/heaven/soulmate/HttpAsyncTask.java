package com.heaven.soulmate;

import android.os.AsyncTask;

import com.heaven.soulmate.model.HttpRequestData;
import com.heaven.soulmate.model.HttpResponseData;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;

/**
 * Created by legol on 2015/11/15.
 */
public class HttpAsyncTask extends AsyncTask<HttpRequestData, Void, HttpResponseData> {

    @Override
    protected HttpResponseData doInBackground(HttpRequestData... request) {
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
            JSONObject requestObj = request[0].getRequest();
            if (requestObj != null){
                DataOutputStream printout = new DataOutputStream(urlConn.getOutputStream());
                printout.write(URLEncoder.encode(requestObj.toString(), "UTF-8").getBytes());
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

                JSONObject responseObj = new JSONObject(sb.toString());
                response.setResponse(responseObj);
            }

            urlConn.disconnect();

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return response;
    }


    @Override
    protected void onPostExecute(HttpResponseData result) {

    }
}