package com.heaven.soulmate.reg;

import com.heaven.soulmate.reg.model.RegModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.heaven.soulmate.reg.*;

import java.util.Dictionary;
import java.util.Enumeration;
import java.util.HashMap;

/**
 * Created by ChenJie3 on 2015/9/8.
 */
@Controller
public class RegController {
    @RequestMapping("/reg")
    @ResponseBody
    public Object reg(HttpServletRequest request, HttpServletResponse response) {

        String phone = request.getParameter("phone");
        String password = request.getParameter("password");

        HashMap<String, Object> retMap = new HashMap<String, Object>();
        int newUID = RegModel.sharedInstance().register(phone, password);
        if (newUID != 0) {
            retMap.put("err_no", new Integer(0));
            retMap.put("uid", new Integer(newUID));
        }
        else{
            retMap.put("err_no", new Integer(100));
            retMap.put("err_msg", "register failed");
        }

        return retMap;
    }

    @RequestMapping("/reg/test")
    public Object test(HttpServletRequest request, HttpServletResponse response) {
        return new ModelAndView("test");
    }
}
