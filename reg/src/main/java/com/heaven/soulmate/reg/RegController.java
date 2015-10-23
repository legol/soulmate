package com.heaven.soulmate.reg;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created by ChenJie3 on 2015/9/8.
 */
@Controller
public class RegController {
    @RequestMapping("/reg")
    @ResponseBody
    public Object reg(HttpServletRequest request, HttpServletResponse response) {
        return "hello reg & spring mvc!";
    }

    @RequestMapping("/reg/test")
    public Object test(HttpServletRequest request, HttpServletResponse response) {
        return new ModelAndView("test");
    }
}
