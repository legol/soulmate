package com.heaven.soulmate.chat;

import com.heaven.soulmate.chat.model.ChatMessages;
import com.heaven.soulmate.chat.model.ChatMessages;
import com.heaven.soulmate.chat.model.ChatModel;
import com.heaven.soulmate.chat.model.ChatResult;
import org.apache.log4j.Logger;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
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
    private static final Logger LOGGER = Logger.getLogger(ChatController.class);

    @RequestMapping(value = "/chat", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Object chat(HttpServletRequest request, @RequestBody ChatMessages messages) {
        LOGGER.info("chat message received.");


        ChatResult ret = new ChatResult();
        ret.setErr_no(0L);
        return ret;

    }
}
