package com.heaven.soulmate.reg;

import com.heaven.soulmate.reg.model.RegModel;
import com.heaven.soulmate.reg.model.RegResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
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
    @RequestMapping(value="/reg", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Object reg(HttpServletRequest request, HttpServletResponse response) {

        String phone = request.getParameter("phone");
        String password = request.getParameter("password");

        HashMap<String, Object> retMap = new HashMap<String, Object>();
        Long newUID = RegModel.sharedInstance().register(phone, password);

        RegResult ret = new RegResult();
        if (newUID != 0) {
            ret.setErrNo(0L);
            ret.setUid(newUID);
        }
        else{
            ret.setErrNo(100L); // TODO: define err code
            ret.setErrMsg("registered failed. no new uid is generated.");
        }

        return ret;
    }

    @RequestMapping("/reg/test")
    public Object test(HttpServletRequest request, HttpServletResponse response) {
        return new ModelAndView("test");
    }
}
