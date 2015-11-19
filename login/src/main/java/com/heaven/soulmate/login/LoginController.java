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
        // assign a longconn server to client
        ServerInfo selectedLongconnServer = LongConnServerController.sharedInstance().serverByUid(lr.getUid());
        if (selectedLongconnServer == null){
            httpResult.setErrNo(-1L);
            httpResult.setErrMsg("can't find a longconn server for you.");
            return httpResult;
        }

        httpResult.setErrNo(0L);

        lr.setLongconnIP(selectedLongconnServer.ip);
        lr.setLongconnPort(selectedLongconnServer.portClient);

        httpResult.setData(lr);

        return httpResult;
    }
}
