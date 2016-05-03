package com.heaven.soulmate.login.controller;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;

import com.heaven.soulmate.login.model.*;
import com.heaven.soulmate.ServerSelector;

import java.util.LinkedList;

/**
 * Created by ChenJie3 on 2015/9/8.
 */
@Controller
public class LoginController {
    private static final Logger LOGGER = Logger.getLogger(LoginController.class);

    @RequestMapping("/login")
    @ResponseBody
    public Object login(HttpServletRequest request, @RequestBody LoginRequest loginRequest) {
        LOGGER.info(String.format("login. phone:<%s> pwd:<%s>", loginRequest.getPhone(), loginRequest.getPassword()));

        HttpResult httpResult = new HttpResult();
        LoginResult lr = LoginModelDAO.sharedInstance().login(loginRequest.getPhone(), loginRequest.getPassword());

        if (lr == null) {
            LOGGER.warn(String.format("login failed. phone:<%s> pwd:<%s>", loginRequest.getPhone(), loginRequest.getPassword()));
            httpResult.errNo = -1;
            httpResult.errMsg = "login failed.";
            return httpResult;
        }

        LOGGER.info(lr.toString());

        // assign a websocket server to client
        ServerInfo selectedWebSocketServer = ServerSelector.sharedInstance().selectServerBy("websocket", lr.getUid());
        if (selectedWebSocketServer == null) {
            httpResult.errNo = -1;
            httpResult.errMsg = "can't find a websocket server for you.";
            return httpResult;
        }
        lr.servers = new ServerInfoList();
        lr.servers.info = new LinkedList<ServerInfo>();
        lr.servers.info.add(selectedWebSocketServer);

        httpResult.errNo = 0;
        httpResult.data = lr;

        // notify all websocket server that someone logged out
        InterServiceInvoker.notifyClientsChanged();

        return httpResult;
    }

    @RequestMapping("/logout")
    @ResponseBody
    public Object logout(HttpServletRequest request, @RequestBody LogoutRequest logoutRequeste) {
        LOGGER.info(String.format("logout. uid:<%d> ", logoutRequeste.uid));

        HttpResult hr = new HttpResult();

        hr.errNo = 0;
        LoginModelDAO.sharedInstance().logout(logoutRequeste.uid);

        // notify all websocket server that someone logged out
        InterServiceInvoker.notifyClientsChanged();

        return hr;
    }

    @RequestMapping("/query_online_clients")
    @ResponseBody
    public Object queryClients(HttpServletRequest request, @RequestBody queryOnlineClientsRequest requestParsed) {

        HttpResult hr = new HttpResult();

        hr.errNo = 0;
        if (!LoginModelDAO.sharedInstance().authByToken(requestParsed.uid, requestParsed.token)){
            LOGGER.error(String.format("can't auth token=%s uid=%d", requestParsed.token, requestParsed.uid));

            hr.errNo = -2;
            hr.errMsg = String.format("can't auth uid=%d token=%s", requestParsed.uid, requestParsed.token);
            return hr;
        }

        QueryOnlineClientsResult res = new QueryOnlineClientsResult();
        hr.data = res;
        res.clients = LoginModelDAO.sharedInstance().queryOnlineClients();

        return hr;
    }
}
