package com.heaven.soulmate.login.model;

import com.heaven.soulmate.Utils;
import com.mchange.v2.c3p0.ComboPooledDataSource;

import java.beans.PropertyVetoException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
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

    public LoginResult login(String phone, String password){
        assert(cpds != null);

        LoginResult login_result = new LoginResult();

        if(phone.isEmpty() || password.isEmpty()){
            login_result.err_no = -1;
            login_result.err_msg = "wrong parameters.";
            return login_result;
        }
        Connection conn = null;
        PreparedStatement statement = null;
        ResultSet rs = null;
        int uid = 0;
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
                    login_result.err_no = -2;
                    login_result.err_msg = "user does not exist.";
                    break;
                }

                if (Utils.md5(password).compareToIgnoreCase(password_from_mysql) != 0) {
                    login_result.err_no = -3;
                    login_result.err_msg = "wrong password.";
                    break;
                }

                login_result.err_no = 0;
                login_result.uid = uid;
                login_result.token = Utils.generateToken(uid, password_from_mysql);

                break;
            }

            statement.close();
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return login_result;
    }
}
