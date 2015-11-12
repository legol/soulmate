package com.heaven.soulmate.login;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
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
    public Object login(HttpServletRequest request, HttpServletResponse response) {
        String phone = request.getParameter("phone");
        String password = request.getParameter("password");

        LoginResult lr = LoginModelDAO.sharedInstance().login(phone, password);

        if (lr.getErrNo() != 0) {
            LOGGER.warn(String.format("login failed. phone:<%s> pwd:<%s>", phone, password));
            return lr;
        }
        // assign a longconn server to client
        ServerInfo selectedLongconnServer = LongConnServerController.sharedInstance().serverByUid(lr.getUid());
        if (selectedLongconnServer == null){
            lr.setErrNo(-1L);
            lr.setErrMsg("can't find a longconn server for you.");
            return lr;
        }

        lr.setLongconnIP(selectedLongconnServer.ip);
        lr.setLongconnPort(selectedLongconnServer.portClient);

        return lr;
    }
}
