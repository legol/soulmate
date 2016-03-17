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

    // 1. verify password
    // 2. generate token and store it into login_status
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
//            // save token to login_status table
//            statement = conn.prepareStatement("delete from login_status where uid = ?");
//            statement.setLong(1, uid);
//            statement.executeUpdate();
//
//            Timestamp token_gen_time = new Timestamp(System.currentTimeMillis());
//
//            Calendar cal = Calendar.getInstance();
//            cal.setTimeInMillis(token_gen_time.getTime());
//            cal.add(Calendar.HOUR, 3); // expires after 3 hours
//
//            Timestamp token_expire_time = new Timestamp(cal.getTime().getTime());
//
//            statement = conn.prepareStatement("insert into login_status(uid, token, token_gen_time, token_expire_time, location) values (?, ?, ?, ?, ST_GEOMFROMTEXT(?))");
//            statement.setLong(1, uid);
//            statement.setString(2, lr.getToken());
//            statement.setTimestamp(3, token_gen_time);
//            statement.setTimestamp(4, token_expire_time);
//            statement.setString(5, "POINT(0 0)");
//            int rowsAffacted = statement.executeUpdate();
//            if (rowsAffacted != 1) {
//                return null;
//            }
//
//            statement.close();
//            conn.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return lr;
    }
}
