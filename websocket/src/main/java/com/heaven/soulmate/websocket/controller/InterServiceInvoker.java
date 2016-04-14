package com.heaven.soulmate.websocket.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.heaven.soulmate.ServerSelector;
import com.heaven.soulmate.Utils;
import com.heaven.soulmate.websocket.model.LogoutRequest;
import com.heaven.soulmate.websocket.model.ServerInfo;
import org.apache.log4j.Logger;

/**
 * Created by ChenJie3 on 2016/4/14.
 */
public class InterServiceInvoker {
    private static final Logger LOGGER = Logger.getLogger(InterServiceInvoker.class);


    public static boolean logout(long uid){
        // remove record from login_status table
        LOGGER.info(String.format("remove from login_status: uid=%d", uid));

        ServerInfo loginServerInfo = ServerSelector.sharedInstance().selectServerBy("login", uid);
        LogoutRequest logoutRequest = new LogoutRequest();
        logoutRequest.uid = uid;

        ObjectMapper mapper = new ObjectMapper();
        String requestInJson = null;
        try {
            requestInJson = mapper.writeValueAsString(logoutRequest);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            LOGGER.error(String.format("unknown error happened while trying to logout: uid=%d", uid));
            return false;
        }

        String response = Utils.httpPost(String.format("http://%s:%d/login/logout", loginServerInfo.ip, loginServerInfo.portServer), requestInJson);
        LOGGER.info(String.format("logout: uid=%d response=%s", uid, response));

        return true;
    }
}
