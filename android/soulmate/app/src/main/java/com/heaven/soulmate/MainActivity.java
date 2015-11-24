package com.heaven.soulmate;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.TextView;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.heaven.soulmate.sdk.model.HttpAsyncTask;
import com.heaven.soulmate.sdk.model.HttpRequestData;
import com.heaven.soulmate.sdk.model.HttpResponseData;
import com.heaven.soulmate.sdk.model.IHttpDelegate;
import com.heaven.soulmate.sdk.model.login.LoginResponseBody;
import com.heaven.soulmate.sdk.model.login.LoginRequest;
import com.heaven.soulmate.sdk.model.longconn.ITcpClientDelegate;
import com.heaven.soulmate.sdk.model.longconn.LongConnMessage;
import com.heaven.soulmate.sdk.model.longconn.LongConnRegisterMessage;
import com.heaven.soulmate.sdk.model.longconn.TcpClient;
import com.heaven.soulmate.sdk.model.longconn.TcpPacket;
import com.heaven.soulmate.sdk.controller.*;

import java.io.IOException;


public class MainActivity extends AppCompatActivity
    implements ISoulMateDelegate
{
    MainActivity mainActivity = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();

            }
        });

        Button btnLogin = (Button) findViewById(R.id.btnLogin);
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SoulMate.getInstance().Login("15011113304", "803048");
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void loginFailed() {

    }

    @Override
    public void packetReceived(TcpClient client, final TcpPacket packet) {
        final TextView txtResponse = (TextView)findViewById(R.id.txtResponse);
        txtResponse.post(new Runnable() {
            @Override
            public void run() {
                txtResponse.setText(String.format("packet received:%s", packet.payload));
            }
        });
    }
}
