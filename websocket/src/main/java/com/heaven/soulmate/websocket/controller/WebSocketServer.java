package com.heaven.soulmate.websocket.controller;

import com.heaven.soulmate.Utils;
import com.heaven.soulmate.websocket.model.LoginModelDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
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
    @RequestMapping("/websocket")
    @ResponseBody
    public Object test(HttpServletRequest request, HttpServletResponse response) {
        LoginModelDAO.sharedInstance().websocketLogin(1, "ae1d1540b2650168767ff45ad053965d");
        return "hello websocket & spring mvc! ";
    }
}
