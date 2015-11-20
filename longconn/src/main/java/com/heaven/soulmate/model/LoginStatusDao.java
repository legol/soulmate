package com.heaven.soulmate.model;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.heaven.soulmate.Utils;
import com.mchange.v2.c3p0.ComboPooledDataSource;

import java.beans.PropertyVetoException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Properties;

/**
 * Created by ChenJie3 on 2015/11/20.
 */
public class LoginStatusDao {
    private static LoginStatusDao instance = null;

    private ComboPooledDataSource cpds = null;
    private Properties props = null;

    protected LoginStatusDao() {
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

    public static LoginStatusDao sharedInstance() {
        if(instance == null) {
            instance = new LoginStatusDao();
        }
        return instance;
    }

    public boolean verifyToken(long uid, String token){
        Connection conn = null;
        PreparedStatement statement = null;
        String tokenFromDb = null;

        try {
            conn = cpds.getConnection();
            statement = conn.prepareStatement("select token from login_status where uid=?");
            statement.setLong(1, uid);

            ResultSet rs = statement.executeQuery();
            while (rs.next()) {
                tokenFromDb = rs.getString("token");
                break; // should have only one
            }

            if (tokenFromDb == null || token.compareToIgnoreCase(tokenFromDb) != 0){
                return false;
            }

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }
}
