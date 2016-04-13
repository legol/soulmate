package com.heaven.soulmate.websocket.controller;

import com.heaven.soulmate.ServerSelector;
import com.heaven.soulmate.Utils;
import com.heaven.soulmate.websocket.model.BroadcastRequest;
import com.heaven.soulmate.websocket.model.HttpResult;
import com.heaven.soulmate.websocket.model.LoginModelDAO;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import javax.websocket.CloseReason;
import javax.websocket.OnClose;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;

/**
 * Created by ChenJie3 on 2016/2/16.
 */
@Controller
public class WebSocketServer {
    private static final Logger LOGGER = Logger.getLogger(WebSocketServer.class);
    private static WebSocketServerEndPoint websocket = new WebSocketServerEndPoint();


    public WebSocketServer(){
        // this is to make sure we load datasource.properties from the mainthread.
        // We won't be able to load datasource.properties from OnMessage
        // We won't be able to load myself.properties from OnMessage
        LoginModelDAO.sharedInstance();
        Utils.Init();
        ServerSelector.sharedInstance();
    }

    @RequestMapping("/test")
    @ResponseBody
    public Object test(HttpServletRequest request, HttpServletResponse response) {
        return "hello spring mvc and websocket";
    }

    @RequestMapping("/broadcast")
    @ResponseBody
    public Object broadcast(HttpServletRequest request, @RequestBody BroadcastRequest broadcastRequest) {

        HttpResult hr = new HttpResult();
        hr.errNo = 0;

        websocket.broadcast(broadcastRequest.message);

        return hr;
    }
}
