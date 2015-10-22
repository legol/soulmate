package com.heaven.soulmate.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created by ChenJie3 on 2015/9/8.
 */
@Controller
public class UserController {
    @RequestMapping("/user")
    @ResponseBody
    public Object user(HttpServletRequest request, HttpServletResponse response) {
        return "hello user & spring mvc!";
    }
}
