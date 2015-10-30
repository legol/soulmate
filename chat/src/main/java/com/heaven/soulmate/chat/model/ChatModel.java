package com.heaven.soulmate.chat.model;

import com.heaven.soulmate.Utils;
import com.mchange.v2.c3p0.ComboPooledDataSource;

import java.beans.PropertyVetoException;
import java.sql.*;
import java.util.Calendar;
import java.util.Properties;

/**
 * Created by ChenJie3 on 2015/10/23.
 */
public class ChatModel {
    private static ChatModel instance = null;

    private ComboPooledDataSource cpds = null;
    private Properties props = null;

    protected ChatModel() {
        props = Utils.readProperties("datasource.properties");
        if (props == null){
            return;
        }

        cpds = new ComboPooledDataSource();
        try {
            cpds.setDriverClass(props.getProperty("driverClass")); //loads the jdbc driver
        } catch (PropertyVetoException e) {
            e.printStackTrace();
        }
        cpds.setJdbcUrl(props.getProperty("jdbcUrl"));
        cpds.setUser(props.getProperty("username"));
        cpds.setPassword(props.getProperty("password"));
    }

    public static ChatModel sharedInstance() {
        if(instance == null) {
            instance = new ChatModel();
        }
        return instance;
    }

}
