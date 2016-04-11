package com.heaven.soulmate.login.model;

import com.heaven.soulmate.Utils;
import com.mchange.v2.c3p0.ComboPooledDataSource;

import java.beans.PropertyVetoException;
import java.sql.*;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;
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

    // remove record from login_status
    public void logout(long uid){
        Connection conn = null;
        PreparedStatement statement = null;
        ResultSet rs = null;

        try {
            conn = cpds.getConnection();

            // remove record from login_status table
            statement = conn.prepareStatement("delete from login_status where uid=?");
            statement.setLong(1, uid);
            statement.executeUpdate();

            statement = conn.prepareStatement("delete from websocket where uid=?");
            statement.setLong(1, uid);
            statement.executeUpdate();

            statement.close();
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // 1. verify password
    // 2. generate token and store it into login_status
    public LoginResult login(String phone, String password){
        assert(cpds != null);

        LoginResult lr = new LoginResult();

        if(phone.isEmpty() || password.isEmpty()){
            lr.setLoginErrNo(-1);
            lr.setLoginErrMsg("wrong parameters.");
            return null;
        }
        Connection conn = null;
        PreparedStatement statement = null;
        ResultSet rs = null;
        long uid = 0L;
        String password_from_mysql = "";

        try {
            conn = cpds.getConnection();

            uid = -1;
            statement = conn.prepareStatement("select uid,password from user where phone=?");
            statement.setString(1, phone);
            rs = statement.executeQuery();

            while (rs.next()){
                uid = rs.getInt("uid");
                password_from_mysql = rs.getString("password");

                if (uid == 0 || password_from_mysql.isEmpty()) {
                    lr.setLoginErrNo(-1);
                    lr.setLoginErrMsg("no uid found");
                    break;
                }

                if (Utils.md5(password).compareToIgnoreCase(password_from_mysql) != 0) {
                    lr.setLoginErrNo(-1);
                    lr.setLoginErrMsg("wrong password");
                    break;
                }

                lr.setLoginErrNo(0);
                lr.setUid(uid);
                lr.setToken(Utils.generateToken(uid, password_from_mysql));

                break;
            }

            if (uid == -1){
                return null;
            }

            // save token to login_status table
            Timestamp token_gen_time = new Timestamp(System.currentTimeMillis());

            Calendar cal = Calendar.getInstance();
            cal.setTimeInMillis(token_gen_time.getTime());
            cal.add(Calendar.HOUR, 3); // expires after 3 hours

            Timestamp token_expire_time = new Timestamp(cal.getTime().getTime());

            statement = conn.prepareStatement("replace login_status(uid, token, token_gen_time, token_expire_time, location) values (?, ?, ?, ?, ST_GEOMFROMTEXT(?))");
            statement.setLong(1, uid);
            statement.setString(2, lr.getToken());
            statement.setTimestamp(3, token_gen_time);
            statement.setTimestamp(4, token_expire_time);
            statement.setString(5, "POINT(0 0)");
            int rowsAffacted = statement.executeUpdate();
            if (rowsAffacted == 0) {
                statement.close();
                conn.close();
                return null;
            }

            statement.close();
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return lr;
    }

    public boolean authByToken(long uid, String token){
        assert(cpds != null);

        if(token.isEmpty()){
            return false;
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
                    return false;
                }

                if (token_from_mysql.compareToIgnoreCase(token) != 0) {
                    return false;
                }

                break;
            }

            if (token_from_mysql.isEmpty()){
                return false;
            }

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }

    public List<ClientInfo> queryOnlineClients(){
        LinkedList<ClientInfo> clients = new LinkedList<ClientInfo>();

        assert(cpds != null);
        Connection conn = null;
        PreparedStatement statement = null;
        ResultSet rs = null;

        try {
            conn = cpds.getConnection();

            statement = conn.prepareStatement("select user.uid, user.name, user.phone from user where user.uid in (select distinct uid from websocket)");
            rs = statement.executeQuery();

            while (rs.next()){
                ClientInfo client = new ClientInfo();
                client.uid = rs.getLong("uid");
                client.name = rs.getString("name");
                client.phone = rs.getString("phone");

                clients.add(client);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }

        return clients;
    }
}
