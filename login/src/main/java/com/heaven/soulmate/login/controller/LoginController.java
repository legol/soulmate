package com.heaven.soulmate.login.controller;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;

import com.heaven.soulmate.login.model.*;
import com.heaven.soulmate.login.ServerSelector;

/**
 * Created by ChenJie3 on 2015/9/8.
 */
@Controller
public class LoginController {
    private static final Logger LOGGER = Logger.getLogger(LoginController.class);

    @RequestMapping("/login")
    @ResponseBody
    public Object login(HttpServletRequest request, @RequestBody LoginRequest loginRequest) {

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
        ServerInfoList selectedWebSocketServer = ServerSelector.sharedInstance().selectServerBy("websocket", lr.getUid());
        if (selectedWebSocketServer == null) {
            httpResult.errNo = -1;
            httpResult.errMsg = "can't find a websocket server for you.";
            return httpResult;
        }
        lr.setServers(selectedWebSocketServer);

        httpResult.errNo = 0;
        httpResult.data = lr;

        return httpResult;
    }

    @RequestMapping("/query_online_clients")
    @ResponseBody
    public Object queryClients(HttpServletRequest request, @RequestBody QueryOnlineClientsRequest requestParsed) {

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
