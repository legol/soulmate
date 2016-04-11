package com.heaven.soulmate.websocket.model;

import com.heaven.soulmate.Utils;
import com.mchange.v2.c3p0.ComboPooledDataSource;
import org.apache.log4j.Logger;

import java.beans.PropertyVetoException;
import java.sql.*;
import java.util.Calendar;
import java.util.Properties;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

/**
 * Created by ChenJie3 on 2015/10/23.
 */
public class LoginModelDAO {
    private static LoginModelDAO instance = null;

    private ComboPooledDataSource cpds = null;
    private Properties props = null;

    private static final Logger LOGGER = Logger.getLogger(LoginModelDAO.class);

    protected LoginModelDAO() {
        props = Utils.readProperties("datasource.properties");
        if (props == null) {
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

    public static LoginModelDAO sharedInstance() {
        if (instance == null) {
            instance = new LoginModelDAO();
        }
        return instance;
    }

    public boolean auth(long uid, String token){
        Connection conn = null;
        PreparedStatement statement = null;
        ResultSet rs = null;
        String token_from_mysql = "";

        try {
            conn = cpds.getConnection();

            statement = conn.prepareStatement("select token, token_expire_time from login_status where uid=?");
            statement.setLong(1, uid);
            rs = statement.executeQuery();

            while (rs.next()) {
                token_from_mysql = rs.getString("token");

                if (token_from_mysql.isEmpty()) {
                    return false;
                }

                if (token_from_mysql.compareToIgnoreCase(token) != 0) {
                    return false;
                }

                break;
            }

            if (token_from_mysql.isEmpty()) {
                return false;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }

    // 1. verify token
    // 2. save my address into login_status
    public LoginResult websocketLogin(long uid, String token, String websocket_session_id) {
        assert (cpds != null);

        LoginResult lr = new LoginResult();
        lr.errno = 0;

        if (token.isEmpty()) {
            lr.errno = -1;
            lr.errmsg = "wrong parameters.";
            return null;
        }
        Connection conn = null;
        PreparedStatement statement = null;
        ResultSet rs = null;
        String token_from_mysql = "";

        try {
            conn = cpds.getConnection();

            if (!auth(uid, token)){
                lr.errmsg = String.format("can't auth uid=%d token=%s", uid, token);
                lr.errno = -2;
                return lr;
            }

            // save websocket info to websocket table
            statement = conn.prepareStatement("insert into websocket(uid, websocket, websocket_session_id, last_active_time) values(?, ?, ?, CURRENT_TIMESTAMP)" +
                    " ON DUPLICATE KEY UPDATE last_active_time=CURRENT_TIMESTAMP");
            statement.setLong(1, uid);
            statement.setString(2, Utils.getBindingIP());
            statement.setString(3, websocket_session_id);
            int rowsAffacted = statement.executeUpdate();
            if (rowsAffacted == 0) {
                lr.errno = -1;
                lr.errmsg = "can't save websocket info to db.";
                return lr;
            }

            statement.close();
            conn.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }

        LOGGER.info(String.format("websocket login: uid=%d websocket_session_id=%s", uid, websocket_session_id));

        return lr;
    }

    public void websocketLogout(long uid, String websocket_session_id) {
        Connection conn = null;
        PreparedStatement statement = null;
        ResultSet rs = null;
        String token_from_mysql = "";

        try {
            conn = cpds.getConnection();

            statement = conn.prepareStatement("delete from websocket where uid=? and websocket=? and websocket_session_id=?");
            statement.setLong(1, uid);
            statement.setString(2, Utils.getBindingIP());
            statement.setString(3, websocket_session_id);
            statement.executeUpdate();

            LOGGER.info(String.format("websocket logout: uid=%d websocket_session_id=%s", uid, websocket_session_id));

            statement = conn.prepareStatement("select count(1) as count from websocket where uid=?");
            statement.setLong(1, uid);
            statement.executeQuery();
            rs = statement.executeQuery();

            int count = 0;
            while (rs.next()) {
                count = rs.getInt("count");
                break;
            }

            if (count == 0){
                // remove record from login_status table
                statement = conn.prepareStatement("delete from login_status where uid=?");
                statement.setLong(1, uid);
                statement.executeUpdate();

                LOGGER.info(String.format("remove from login_status: uid=%d", uid));
            }

            statement.close();
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}