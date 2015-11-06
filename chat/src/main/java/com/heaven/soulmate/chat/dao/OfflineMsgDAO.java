package com.heaven.soulmate.chat.dao;

import com.heaven.soulmate.Utils;
import com.heaven.soulmate.chat.model.ChatMessages;
import com.mchange.v2.c3p0.ComboPooledDataSource;

import java.beans.PropertyVetoException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Properties;

/**
 * Created by chenjie3 on 2015/11/5.
 */
public class OfflineMsgDAO {
    private static OfflineMsgDAO instance = null;

    private ComboPooledDataSource cpds = null;
    private Properties props = null;

    protected OfflineMsgDAO() {
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

    public static OfflineMsgDAO sharedInstance() {
        if(instance == null) {
            instance = new OfflineMsgDAO();
        }
        return instance;
    }

    public long saveMsg(ChatMessages messages){
        Connection conn = null;
        PreparedStatement statement = null;
        long messageId = -1L;
        String password_from_mysql = "";

        try {
            conn = cpds.getConnection();

            statement = conn.prepareStatement("insert into msg(`from_uid`, `to_uid`, `msg`) values (?,?,?)");
            statement.setLong(1, messages.getUid());
            statement.setLong(2, messages.getTarget_uid());
            statement.setString(3, messages.getMessages().toString());

            int rowsAffactted = statement.executeUpdate();
            if(rowsAffactted == 1){
                statement = conn.prepareStatement("SELECT LAST_INSERT_ID() as messageId");
                ResultSet rs = statement.executeQuery();
                while (rs.next()) {
                    messageId = rs.getLong("messageId");
                    break;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return messageId;
    }
}

