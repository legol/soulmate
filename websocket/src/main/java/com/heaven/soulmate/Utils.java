package com.heaven.soulmate;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;

import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicNameValuePair;
import org.springframework.core.io.ClassPathResource;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Timestamp;
import java.util.*;

/**
 * Created by ChenJie3 on 2015/10/23.
 */
public class Utils {

    static Random rand = new Random();
    static String binding_ip = "";

    static public void Init() {
        Properties props = new Properties();
        ClassLoader loader = Thread.currentThread().getContextClassLoader();
        InputStream stream = loader.getResourceAsStream("myself.properties");
        try {
            props.load(stream);
        } catch (IOException e) {
            e.printStackTrace();
        }

        binding_ip = props.getProperty("binding_ip");
    }

    static public String getBindingIP() {
        return binding_ip;
    }

    // read a property file from resource folder
    static public Properties readProperties(String filename) {
        Properties props = new Properties();

        ClassPathResource resource = new ClassPathResource(filename);
        InputStream stream = null;
        try {
            stream = resource.getInputStream();
            props.load(stream);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        return props;
    }

    public static String md5(String original) {
        MessageDigest md = null;
        try {
            md = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return "";
        }

        md.update(original.getBytes());
        byte[] digest = md.digest();
        StringBuffer sb = new StringBuffer();
        for (byte b : digest) {
            sb.append(String.format("%02x", b & 0xff));
        }

        return sb.toString();
    }

    public static int randInt(int min, int max) {
        // nextInt is normally exclusive of the top value,
        // so add 1 to make it inclusive
        return rand.nextInt((max - min) + 1) + min;
    }

    public static String generateToken(long uid, String password) {
        String secret = "chenjie love cxf!";
        long rand = randInt(19800202, 19901116);
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());

        String token_raw = String.format("%d %d %s %s %d", uid, rand, secret, password, timestamp.getTime());
        return md5(token_raw);
    }

    // HTTP GET request
    public static String httpGet(String url){

        HttpClient client = HttpClientBuilder.create().build();
        HttpGet request = new HttpGet(url);
        String result = null;

        // add request header
        request.addHeader("User-Agent", "soulmate");

        HttpResponse response = null;
        try {
            response = client.execute(request);

            BufferedReader rd = new BufferedReader(
                    new InputStreamReader(response.getEntity().getContent()));

            StringBuffer sb = new StringBuffer();
            String line = "";
            while ((line = rd.readLine()) != null) {
                sb.append(line);
            }

            result = sb.toString();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

    // post json to url
    public static String httpPost(String url, String json){
        HttpClient client = HttpClientBuilder.create().build();
        HttpPost post = new HttpPost(url);
        String result = null;

        // add header
        post.setHeader("User-Agent", "soulmate");

        StringEntity requestEntity = new StringEntity(
                json,
                ContentType.APPLICATION_JSON);
        post.setEntity(requestEntity);

        HttpResponse response = null;
        try {
            response = client.execute(post);

            BufferedReader rd = new BufferedReader(
                    new InputStreamReader(response.getEntity().getContent()));

            StringBuffer sb = new StringBuffer();
            String line = "";
            while ((line = rd.readLine()) != null) {
                sb.append(line);
            }

            result = sb.toString();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return result;
    }

}