package com.heaven.soulmate.chat;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.heaven.soulmate.chat.dao.OfflineMsgDAO;
import com.heaven.soulmate.chat.model.*;
import com.heaven.soulmate.chat.model.ChatMessages;
import org.apache.log4j.Logger;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.io.InputStream;

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

        // 1. generate a message id and store the message with id to db
        // 2. deliver the message to client b
        //      2.1 find which longconn the client b is connected with.
        //      2.2 deliver the message to longconn
        // 3. longconn: deliever the message to b.
        // 4. longconn: if succeeded, update db with message(id) delivered.

        long messageId = 0;
        if ((messageId= OfflineMsgDAO.sharedInstance().saveMsg(messages)) < 0){
            ret.setErrNo(messageId);
            ret.setErrMsg("can't write msg to db.");
            return ret;
        }
        messages.setMessageId(messageId);

        ServerInfo longconnServer = LongConnServerController.sharedInstance().serverByUid(messages.getTarget_uid());

        // todo: longconn mgr: find out where each user is.

        ret.setErrNo(0L);
        return ret;

    }
}
