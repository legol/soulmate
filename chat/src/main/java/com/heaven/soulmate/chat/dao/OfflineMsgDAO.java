package com.heaven.soulmate.chat.dao;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.heaven.soulmate.Utils;
import com.heaven.soulmate.chat.model.*;

import com.mchange.v2.c3p0.ComboPooledDataSource;
import org.apache.log4j.Logger;

import java.beans.PropertyVetoException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.Properties;

/**
 * Created by chenjie3 on 2015/11/5.
 */
public class OfflineMsgDAO {
    private static final Logger LOGGER = Logger.getLogger(OfflineMsgDAO.class);

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

    public long saveMsg(ChatRequest msg){
        Connection conn = null;
        PreparedStatement statement = null;
        long messageId = -1L;
        String password_from_mysql = "";

        try {
            conn = cpds.getConnection();

            statement = conn.prepareStatement("insert into msg(`from_uid`, `to_uid`, `msg`) values (?,?,?)");

            statement.setLong(1, msg.getUid());
            statement.setLong(2, msg.getTarget_uid());

            ObjectMapper mapper = new ObjectMapper();
            String messagesInJson = mapper.writeValueAsString(msg);
            statement.setString(3, messagesInJson);

            int rowsAffactted = statement.executeUpdate();
            if(rowsAffactted == 1){
                statement = conn.prepareStatement("SELECT LAST_INSERT_ID() as messageId");
                ResultSet rs = statement.executeQuery();
                while (rs.next()) {
                    messageId = rs.getLong("messageId");
                    break;
                }
            }

            statement.close();
            conn.close();

        } catch (SQLException e) {

            try {
                statement.close();
                conn.close();
            } catch (SQLException e1) {
                e1.printStackTrace();
            }

            e.printStackTrace();
        } catch (JsonProcessingException e) {

            try {
                statement.close();
                conn.close();
            } catch (SQLException e1) {
                e1.printStackTrace();
            }

            e.printStackTrace();
        }

        return messageId;
    }

    public boolean updateDelivered(long uid, LinkedList<Long> messageIds){
        Connection conn = null;
        PreparedStatement statement = null;
        long messageId = -1L;
        String password_from_mysql = "";

        if (messageIds.size() == 0){
            return false;
        }

        try {
            conn = cpds.getConnection();


            StringBuilder sb = new StringBuilder();
            sb.append(String.format("message_id in (%d", messageIds.getFirst()));
            for (int i = 1; i < messageIds.size(); i++){
                sb.append(String.format(",%d", messageIds.get(i)));
            }
            sb.append(")");
            String sql = "update msg set delivered=1 where to_uid=? and " + sb.toString();

            statement = conn.prepareStatement(sql);
            statement.setLong(1, uid);

            int rowsAffectted = statement.executeUpdate();
            LOGGER.info(String.format("update delivered, %d rowsAffected: %s", rowsAffectted, sql));

            statement.close();
            conn.close();

        } catch (SQLException e) {

            try {
                statement.close();
                conn.close();
            } catch (SQLException e1) {
                e1.printStackTrace();
            }

            e.printStackTrace();
        }

        return true;
    }
}

