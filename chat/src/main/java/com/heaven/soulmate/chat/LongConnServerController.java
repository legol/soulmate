package com.heaven.soulmate.chat;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.heaven.soulmate.chat.model.LongConnServerInfo;
import com.heaven.soulmate.chat.model.ServerInfo;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.io.InputStream;

/**
 * Created by chenjie3 on 2015/11/6.
 */
public class LongConnServerController {
    private static final Logger LOGGER = Logger.getLogger(LongConnServerController.class);
    private static LongConnServerInfo longConnServerInfo = null;

    private static LongConnServerController instance = null;

    public static LongConnServerController sharedInstance() {
        if(instance == null) {
            instance = new LongConnServerController();
        }
        return instance;
    }

    public LongConnServerController(){
        // read longconn server info
        if (longConnServerInfo == null){
            LOGGER.info("read long conn server info.");

            ClassLoader loader = Thread.currentThread().getContextClassLoader();
            InputStream stream = loader.getResourceAsStream("longconn.json");

            ObjectMapper mapper = new ObjectMapper();
            try {
                longConnServerInfo = mapper.readValue(stream, LongConnServerInfo.class);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public ServerInfo serverByUid(long uid){
        return null;
    }


}
