package com.heaven.soulmate.login;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;

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
            httpResult.setErrNo(-1L);
            httpResult.setErrMsg("login failed.");
            return httpResult;
        }

        LOGGER.info(lr.toString());

        // assign a websocket server to client
        ServerInfoList selectedWebSocketServer = ServerSelector.sharedInstance().selectServerBy("websocket", lr.getUid());
        if (selectedWebSocketServer == null) {
            httpResult.setErrNo(-1L);
            httpResult.setErrMsg("can't find a longconn server for you.");
            return httpResult;
        }
        lr.setServers(selectedWebSocketServer);

        httpResult.setErrNo(0L);
        httpResult.setData(lr);

        // todo: notify all other clients that someone logged in


        return httpResult;
    }

    @RequestMapping("/query_online_clients")
    @ResponseBody
    public Object queryClients(HttpServletRequest request, @RequestBody QueryOnlineClientsRequest requestParsed) {

        // todo: implementation

        if (!LoginModelDAO.sharedInstance().authByToken(requestParsed.uid, requestParsed.token)){

        }


        return null;
    }
}