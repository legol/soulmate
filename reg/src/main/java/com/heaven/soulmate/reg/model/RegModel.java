package com.heaven.soulmate.reg.model;

import com.heaven.soulmate.reg.Utils;
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

        Connection conn = null;
        PreparedStatement statement = null;
        int rowsAffected = 0;
        int newUID = 0;

        try {
            conn = cpds.getConnection();

            statement = conn.prepareStatement("");
            statement.setString(0, phone);
            statement.setString(1, password);
            rowsAffected = statement.executeUpdate();

            if(rowsAffected == 1){
                statement = conn.prepareStatement("SELECT LAST_INSERT_ID()");
                ResultSet rs = statement.executeQuery();
                rs.first();
                newUID = rs.getInt(0);
            }
            statement.close();
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return newUID;
    }
}
