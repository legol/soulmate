package com.heaven.soulmate.websocket.model;

import com.heaven.soulmate.Utils;
import com.mchange.v2.c3p0.ComboPooledDataSource;

import java.beans.PropertyVetoException;
import java.sql.*;
import java.util.Calendar;
import java.util.Properties;

/**
 * Created by ChenJie3 on 2015/10/23.
 */
public class LoginModelDAO {
    private static LoginModelDAO instance = null;

    private ComboPooledDataSource cpds = null;
    private Properties props = null;

    protected LoginModelDAO() {
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

    public static LoginModelDAO sharedInstance() {
        if(instance == null) {
            instance = new LoginModelDAO();
        }
        return instance;
    }

    // 1. verify token
    // 2. save my address into login_status
    public LoginResult websocketLogin(long uid, String token){
        assert(cpds != null);

        LoginResult lr = new LoginResult();
        lr.errno = 0;

        if(token.isEmpty()){
            lr.errno = -1;
            lr.errmsg = "wrong parameters.";
            return null;
        }
        Connection conn = null;
        PreparedStatement statement = null;
        ResultSet rs = null;
        String token_from_mysql ="";

        try {
            conn = cpds.getConnection();

            statement = conn.prepareStatement("select token, token_expire_time from login_status where uid=?");
            statement.setLong(1, uid);
            rs = statement.executeQuery();

            while (rs.next()){
                token_from_mysql = rs.getString("token");

                if (token_from_mysql.isEmpty()) {
                    lr.errno = -1;
                    lr.errmsg = "uid not found";
                    break;
                }

                if (token_from_mysql.compareToIgnoreCase(token) != 0) {
                    lr.errno = -1;
                    lr.errmsg = "token mismatch";
                    break;
                }

                lr.errno = 0;

                break;
            }

            if (token_from_mysql.isEmpty()){
                lr.errno = -1;
                lr.errmsg = "uid not found";
                return lr;
            }

            // save websocket info to login_status
            statement = conn.prepareStatement("update login_status set websocket_addr=? where uid=?");
            statement.setString(1, Utils.getBindingIP());
            statement.setLong(2, uid);
            int rowsAffacted = statement.executeUpdate();
            if (rowsAffacted != 1) {
                lr.errno = -1;
                lr.errmsg = "can't save websocket info to db.";
                return lr;
            }

            statement.close();
            conn.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return lr;
    }
}
