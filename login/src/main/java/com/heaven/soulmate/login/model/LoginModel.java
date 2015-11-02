package com.heaven.soulmate.login.model;

import com.heaven.soulmate.Utils;
import com.mchange.v2.c3p0.ComboPooledDataSource;

import java.beans.PropertyVetoException;
import java.sql.*;
import java.util.Calendar;
import java.util.Properties;

/**
 * Created by ChenJie3 on 2015/10/23.
 */
public class LoginModel {
    private static LoginModel instance = null;

    private ComboPooledDataSource cpds = null;
    private Properties props = null;

    protected LoginModel() {
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

    public static LoginModel sharedInstance() {
        if(instance == null) {
            instance = new LoginModel();
        }
        return instance;
    }

    // 1. verify password
    // 2. generate token and store it into login_status
    public LoginResult login(String phone, String password){
        assert(cpds != null);

        LoginResult login_result = new LoginResult();

        if(phone.isEmpty() || password.isEmpty()){
            login_result.setErrNo(-1L);
            login_result.setErrMsg("wrong parameters.");
            return login_result;
        }
        Connection conn = null;
        PreparedStatement statement = null;
        ResultSet rs = null;
        long uid = 0L;
        String password_from_mysql = "";

        try {
            conn = cpds.getConnection();

            statement = conn.prepareStatement("select uid,password from user where phone=?");
            statement.setString(1, phone);
            rs = statement.executeQuery();

            while (rs.next()){
                uid = rs.getInt("uid");
                password_from_mysql = rs.getString("password");

                if (uid == 0 || password_from_mysql.isEmpty()) {
                    login_result.setErrNo(-2L);
                    login_result.setErrMsg("user does not exist.");
                    break;
                }

                if (Utils.md5(password).compareToIgnoreCase(password_from_mysql) != 0) {
                    login_result.setErrNo(-3L);
                    login_result.setErrMsg("wrong password.");
                    break;
                }

                login_result.setErrNo(0L);
                login_result.setUid(uid);
                login_result.setToken(Utils.generateToken(uid, password_from_mysql));

                break;
            }

            // save token to login_status table
            statement = conn.prepareStatement("delete from login_status where uid = ?");
            statement.setLong(1, uid);
            statement.executeUpdate();

            Timestamp token_gen_time = new Timestamp(System.currentTimeMillis());

            Calendar cal = Calendar.getInstance();
            cal.setTimeInMillis(token_gen_time.getTime());
            cal.add(Calendar.HOUR, 3); // expires after 3 hours

            Timestamp token_expire_time = new Timestamp(cal.getTime().getTime());

            statement = conn.prepareStatement("insert into login_status(uid, token, token_gen_time, token_expire_time, location) values (?, ?, ?, ?, ST_GEOMFROMTEXT(?))");
            statement.setLong(1, uid);
            statement.setString(2, login_result.getToken());
            statement.setTimestamp(3, token_gen_time);
            statement.setTimestamp(4, token_expire_time);
            statement.setString(5, "POINT(0 0)");
            int rowsAffacted = statement.executeUpdate();
            if (rowsAffacted != 1) {
                login_result.setErrNo(-4L);
                login_result.setToken("");
                login_result.setErrMsg(String.format("can't write login_status(uid=%d)", uid));
            }

            statement.close();
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return login_result;
    }
}
