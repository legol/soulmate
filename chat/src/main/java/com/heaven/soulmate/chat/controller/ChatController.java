package com.heaven.soulmate.chat.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.heaven.soulmate.chat.dao.LoginStatusDao;
import com.heaven.soulmate.chat.dao.OfflineMsgDAO;
import com.heaven.soulmate.chat.model.*;
import org.apache.log4j.Logger;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;

/**
 * Created by ChenJie3 on 2015/9/8.
 */
@Controller
public class ChatController {
    private static final Logger LOGGER = Logger.getLogger(ChatController.class);

    @RequestMapping(value = "/broadcast", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Object broadcast(HttpServletRequest request, @RequestBody ChatRequest msg) {
        ChatResult ret = new ChatResult();
        ret.errNo = 0;

        // 1. verify token
        if(!LoginStatusDao.sharedInstance().verifyToken(msg.getUid(), msg.getToken())){
            ret.errNo = -1;
            ret.errMsg = "can't verify token";
            return ret;
        }

        // 2. broadcast to all websocket server
        ObjectMapper mapper = new ObjectMapper();
        String messageInJson = null;
        try {
            messageInJson = mapper.writeValueAsString(msg);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            ret.setErrNo(-1);
            ret.setErrMsg(String.format("can't convert message to json"));
            return ret;
        }

        if(!InterServiceInvoker.broadcast(messageInJson)){
            ret.errNo = -1;
            ret.errMsg = "can't broadcast to websocket";
        }

        return ret;
    }

    @RequestMapping(value = "/chat", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Object chat(HttpServletRequest request, @RequestBody ChatRequest msg) {
        LOGGER.info(String.format("chat message received. uid_from=%d uid_to=%d token=%s", msg.getUid(), msg.getTarget_uid(), msg.getToken()));

        ChatResult ret = new ChatResult();

        // 0. verify token
        // 1. generate a message id and store the message with id to db
        // 2. deliver the message to client b
        //      2.1 find which longconn the client b is connected with.
        //      2.2 deliver the message to longconn
        // 3. longconn: deliever the message to b.
        // 4. longconn: if succeeded, update db with message(id) delivered.

        // 0. verify token
        if(!LoginStatusDao.sharedInstance().verifyToken(msg.getUid(), msg.getToken())){
            ret.setErrNo(-1);
            ret.setErrMsg(String.format("can't verify token"));
            return ret;
        }

        // 1. save to offlineMsgDB
        int messageId = 0;
        if ((messageId = (int)OfflineMsgDAO.sharedInstance().saveMsg(msg)) < 0) {
            ret.setErrNo(messageId);
            ret.setErrMsg("can't write msg to db.");
            return ret;
        }
        msg.setMessageId(messageId);
        LOGGER.info(String.format("chat message id generated. id=%d. uid_from=%d uid_to=%d token=%s", messageId, msg.getUid(), msg.getTarget_uid(), msg.getToken()));

        // 2. deliver message
        ObjectMapper mapper = new ObjectMapper();
        String messageInJson = null;
        try {
            messageInJson = mapper.writeValueAsString(msg);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            ret.setErrNo(-1);
            ret.setErrMsg(String.format("can't convert message to json"));
            return ret;
        }

        if (LongConnServerController.sharedInstance().sendMessage(msg.getTarget_uid(), 2, messageInJson) == false) {
            ret.setErrNo(-1);
            ret.setErrMsg(String.format("can't send message."));
            return ret;
        }

        ret.setErrNo(0);
        return ret;

    }

    @RequestMapping(value = "/chat_ack", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Object chatack(HttpServletRequest request, @RequestBody ChatAckRequest msg) {
        LOGGER.info(String.format("chat_ack received. uid=%d", msg.getUid()));

        ChatAckResult ret = new ChatAckResult();

        // 0. verify token
        if (LoginStatusDao.sharedInstance().verifyToken(msg.getUid(), msg.getToken()) ==  false){
            ret.setErrNo(-1L);
            ret.setErrMsg(String.format("can't verify token for uid=%d token=%s", msg.getUid(), msg.getToken()));
            return ret;
        }

        // 1. update offline msg db
        if (!OfflineMsgDAO.sharedInstance().updateDelivered(msg.getUid(), msg.getMessageIds())){
            ret.setErrNo(-1L);
            ret.setErrMsg(String.format("can't update delivered uid=%d", msg.getUid()));
            return ret;
        }

        ret.setErrNo(0L);
        return ret;
    }
}
