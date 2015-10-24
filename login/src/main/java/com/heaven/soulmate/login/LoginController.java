package com.heaven.soulmate.login;

import com.heaven.soulmate.login.model.LoginModel;
import com.heaven.soulmate.login.model.LoginResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;

/**
 * Created by ChenJie3 on 2015/9/8.
 */
@Controller
public class LoginController {
    @RequestMapping("/login")
    @ResponseBody
    public Object login(HttpServletRequest request, HttpServletResponse response) {
        String phone = request.getParameter("phone");
        String password = request.getParameter("password");

        LoginResult lr = LoginModel.sharedInstance().login(phone, password);
        HashMap<String, Object> retMap = new HashMap<String, Object>();
        HashMap<String, Object> dataMap = new HashMap<String, Object>();

        if (lr.err_no != 0) {
            retMap.put("err_no", new Integer(lr.err_no));
            retMap.put("err_msg", lr.err_msg);

            return retMap;
        }

        retMap.put("err_no", new Integer(0));

        dataMap.put("uid", new Integer(lr.uid));
        dataMap.put("token", lr.token);
        retMap.put("data",dataMap);

        return retMap;
    }
}
