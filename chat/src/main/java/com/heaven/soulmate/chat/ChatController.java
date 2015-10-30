package com.heaven.soulmate.chat;

import com.heaven.soulmate.chat.model.ChatModel;
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
public class ChatController {

    @RequestMapping("/chat")
    @ResponseBody
    public Object chat(HttpServletRequest request, HttpServletResponse response) {
        HashMap<String, Object> retMap = new HashMap<String, Object>();

        String phone = request.getParameter("phone");
        String password = request.getParameter("password");

        retMap.put("err_no", new Integer(0));

        return retMap;
    }
}
