package com.heaven.soulmate.reg.model;

import com.heaven.soulmate.Utils;
import com.mchange.v2.c3p0.ComboPooledDataSource;

import java.beans.PropertyVetoException;

import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.PreparedStatement;
import java.util.Properties;

/**
 * Created by ChenJie3 on 2015/10/23.
 */
public class RegModel {
    private static RegModel instance = null;

    private ComboPooledDataSource cpds = null;
    private Properties props = null;

    protected RegModel() {
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

    public static RegModel sharedInstance() {
        if(instance == null) {
            instance = new RegModel();
        }
        return instance;
    }

    public int register(String phone, String password){
        assert(cpds != null);

        if(phone.isEmpty() || password.isEmpty()){
            return 0;
        }
        Connection conn = null;
        PreparedStatement statement = null;
        int rowsAffected = 0;
        int newUID = 0;

        try {
            conn = cpds.getConnection();

            statement = conn.prepareStatement("insert into `user`(`phone`, `password`) values (?,?)");
            statement.setString(1, phone);
            statement.setString(2, Utils.md5(password));
            rowsAffected = statement.executeUpdate();

            if(rowsAffected == 1){
                statement = conn.prepareStatement("SELECT LAST_INSERT_ID() as newUID");
                ResultSet rs = statement.executeQuery();
                while (rs.next()) {
                    newUID = rs.getInt("newUID");
                    break;
                }
            }
            statement.close();
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return newUID;
    }
}
