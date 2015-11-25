package com.heaven.soulmate.sdk.controller;

import android.util.Log;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.heaven.soulmate.sdk.model.HttpAsyncTask;
import com.heaven.soulmate.sdk.model.HttpRequestData;
import com.heaven.soulmate.sdk.model.HttpResponseData;
import com.heaven.soulmate.sdk.model.IHttpDelegate;
import com.heaven.soulmate.sdk.model.chat.ChatAckRequest;
import com.heaven.soulmate.sdk.model.chat.ChatRequest;
import com.heaven.soulmate.sdk.model.login.LoginRequest;
import com.heaven.soulmate.sdk.model.login.LoginResponseBody;
import com.heaven.soulmate.sdk.model.longconn.ITcpClientDelegate;
import com.heaven.soulmate.sdk.model.longconn.LongConnMessage;
import com.heaven.soulmate.sdk.model.longconn.LongConnRegisterMessage;
import com.heaven.soulmate.sdk.model.longconn.TcpClient;
import com.heaven.soulmate.sdk.model.longconn.TcpPacket;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.util.LinkedList;

/**
 * Created by ChenJie3 on 2015/11/24.
 */
public class SoulMate implements IHttpDelegate, ITcpClientDelegate{
    private static SoulMate ourInstance = new SoulMate();

//    private String loginUrl = "http://192.168.1.87:8080/soulmate/login";
//    private String chatUrl = "http://192.168.1.87:8081/soulmate/chat";
//    private String chatAckUrl = "http://192.168.1.87:8081/soulmate/chat_ack";

    private String loginUrl = "http://192.168.132.69:8080/soulmate/login";
    private String chatUrl = "http://192.168.132.69:8081/soulmate/chat";
    private String chatAckUrl = "http://192.168.132.69:8081/soulmate/chat_ack";

    private String phone = null;
    private String password = null;

    private long uid = 0;
    private String token = null;

    private String longconnHost = null;
    private int longconnPort = 0;

    private LongConnKeeper longconnKeeper = new LongConnKeeper(this);

    private ISoulMateDelegate delegate = null;

    public static SoulMate getInstance() {
        return ourInstance;
    }

    private SoulMate() {
    }

    public boolean Login(String phone, String password){
        Log.i(this.getClass().getName(), String.format("start login. %s:%s", phone, password));

        this.phone = phone;
        this.password = password;

        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setPhone(phone);
        loginRequest.setPassword(password);

        return sendHttpRequest(loginUrl, loginRequest);
    }

    private boolean chatAck(LinkedList<Long> messageIds) {
        Log.i(this.getClass().getName(), String.format("chat ack. %d", uid));

        ChatAckRequest chatAckRequest = new ChatAckRequest();
        chatAckRequest.setUid(uid);
        chatAckRequest.setToken(token);
        chatAckRequest.setMessageIds(messageIds);

        return sendHttpRequest(chatAckUrl, chatAckRequest);
    }

    public boolean chat(long targetUid, String payload){
        // todo:
        return true;
    }

    private void onLoginResponse(HttpRequestData request, HttpResponseData response){
        if (response == null || response.getHttpStatus() != HttpURLConnection.HTTP_OK){
            loginFailed();
            return;
        }

        String responseBody = response.getResponseBody();

        ObjectMapper mapper = new ObjectMapper();
        LoginResponseBody httpResult = null;
        try {
            httpResult = mapper.readValue(responseBody, LoginResponseBody.class);
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (httpResult == null){
            loginFailed();
            return;
        }

        if (httpResult.getErrNo() != 0){
            loginFailed();
            return;
        }

        uid = httpResult.getData().getUid();
        token = httpResult.getData().getToken();
        longconnHost = httpResult.getData().getLongconnIP();
        longconnPort = httpResult.getData().getLongconnPort();

        Log.i(this.getClass().getName(), String.format("login succeeded. %s:%s uid=%d token=%s longconnHost=%s longconnPort=%d",
                phone, password,
                uid, token,
                longconnHost, longconnPort));

        longconnKeeper.setLongconnHost(longconnHost);
        longconnKeeper.setLongconnPort(longconnPort);
        longconnKeeper.connect();
    }

    private void onChatAckResponse(HttpRequestData request, HttpResponseData response) {
        // do nothing
    }

    private boolean sendHttpRequest(String url, Object requestBody){
        Log.i(this.getClass().getName(), String.format("send http request. url=%s", url));

        HttpRequestData request = new HttpRequestData();
        request.setUrl(url);
        try {
            ObjectMapper mapper = new ObjectMapper();
            request.setRequestBody(mapper.writeValueAsString(requestBody));
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            Log.e(this.getClass().getName(), String.format("failed to send http request. url=%s", url));
            return false;
        }

        HttpAsyncTask httpTask = new HttpAsyncTask();
        httpTask.setDelegate(this);
        httpTask.execute(request);

        return true;
    }

    @Override
    public void onHttpResponse(HttpRequestData request, HttpResponseData response) {
        if(response == null || response.getHttpStatus() != HttpURLConnection.HTTP_OK) {
            Log.e(this.getClass().getName(), String.format("failed to send http request. url=%s", request.getUrl()));
            return;
        }

        if (request.getUrl().compareToIgnoreCase(loginUrl) == 0){
            onLoginResponse(request, response);
        }
        else if (request.getUrl().compareToIgnoreCase(chatAckUrl) == 0){
            onChatAckResponse(request, response);
        }
        else{

        }
    }

    private void loginFailed(){
        Log.e(this.getClass().getName(), String.format("login failed. %s:%s", phone, password));
        if (delegate != null){
            delegate.loginFailed();
        }
    }

    @Override
    public void connectionFailure(TcpClient client) {
        Log.wtf(this.getClass().getName(), String.format("can't connected to %s:%d.", longconnHost, longconnPort));
        longconnKeeper.connect();
    }

    @Override
    public void connected(TcpClient client) {
        Log.d(this.getClass().getName(), String.format("connected to %s:%d", longconnHost, longconnPort));

        // send uid to longconn to register
        LongConnRegisterMessage longconnRegMsg = new LongConnRegisterMessage();
        longconnRegMsg.setUid(uid);
        longconnRegMsg.setToken(token);

        ObjectMapper mapper = new ObjectMapper();
        LongConnMessage longconnMsg = new LongConnMessage();
        longconnMsg.setErrNo(0);
        longconnMsg.setType(1);

        String longconnMsgInJson = null;
        try {
            longconnMsg.setPayload(mapper.writeValueAsString(longconnRegMsg));
            longconnMsgInJson = mapper.writeValueAsString(longconnMsg);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        Log.d(this.getClass().getName(), String.format("register to %s:%d uid=%d token=%s", longconnHost, longconnPort, uid, token));
        client.send(longconnMsgInJson);
    }

    @Override
    public void connectionLost(TcpClient client) {
        Log.d(this.getClass().getName(), String.format("connection lost from %s:%d", longconnHost, longconnPort));
        // do nothing. LongConnKeeper will reconnect automatically
    }

    @Override
    public void packetReceived(TcpClient client, TcpPacket packet) {
        Log.d(this.getClass().getName(), String.format("packet received from %s:%d\n%s", longconnHost, longconnPort, packet.payload));

        if (delegate != null){
            delegate.packetReceived(client, packet);
        }

        // send chat_ack to chat
        ObjectMapper mapper = new ObjectMapper();
        ChatRequest chatMsg = null;
        try {
            chatMsg = mapper.readValue(packet.payload, ChatRequest.class);
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (chatMsg == null){
            Log.d(this.getClass().getName(), String.format("unknown packet received from %s:%d\n%s", longconnHost, longconnPort, packet.payload));
            return;
        }

        LinkedList<Long> messageIds = new LinkedList<Long>();
        messageIds.add(chatMsg.getMessageId());
        if(!chatAck(messageIds)){
            Log.d(this.getClass().getName(), String.format("unknown packet received from %s:%d\n%s", longconnHost, longconnPort, packet.payload));
            return;
        }
    }

    public ISoulMateDelegate getDelegate() {
        return delegate;
    }

    public void setDelegate(ISoulMateDelegate delegate) {
        this.delegate = delegate;
    }

    public long getUid() {
        return uid;
    }
}
