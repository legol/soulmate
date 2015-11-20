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
import com.heaven.soulmate.model.HttpAsyncTask;
import com.heaven.soulmate.model.HttpRequestData;
import com.heaven.soulmate.model.HttpResponseData;
import com.heaven.soulmate.model.IHttpDelegate;
import com.heaven.soulmate.model.login.LoginResponseBody;
import com.heaven.soulmate.model.login.LoginRequest;
import com.heaven.soulmate.model.login.LoginResponseBody;
import com.heaven.soulmate.model.longconn.ITcpClientDelegate;
import com.heaven.soulmate.model.longconn.LongConnMessage;
import com.heaven.soulmate.model.longconn.LongConnRegisterMessage;
import com.heaven.soulmate.model.longconn.TcpClient;
import com.heaven.soulmate.model.longconn.TcpPacket;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;


public class MainActivity extends AppCompatActivity
    implements IHttpDelegate ,ITcpClientDelegate
{
    IHttpDelegate mainActivity = this;

    private long uid;
    private String token;

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
        btnLogin.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                HttpAsyncTask httpTask;

                LoginRequest loginRequest = new LoginRequest();
                loginRequest.setPhone("15011113304");
                loginRequest.setPassword("803048");

                ObjectMapper mapper = new ObjectMapper();

                HttpRequestData request = new HttpRequestData();
                request.setUrl("http://192.168.132.69:8080/soulmate/login");
                try {
                    request.setRequestBody(mapper.writeValueAsString(loginRequest));
                } catch (JsonProcessingException e) {
                    e.printStackTrace();
                }

                httpTask = new HttpAsyncTask();
                httpTask.setDelegate(mainActivity);
                httpTask.execute(request);
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
    public void onHttpResponse(HttpRequestData request, HttpResponseData response) {
        String responseBody = response.getResponseBody();

        ObjectMapper mapper = new ObjectMapper();
        LoginResponseBody httpResult = null;
        try {
            httpResult = mapper.readValue(responseBody, LoginResponseBody.class);
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (httpResult == null){
            return;
        }

        uid = httpResult.getData().getUid();
        token = httpResult.getData().getToken();

        TextView txtResponse = (TextView)findViewById(R.id.txtResponse);
        txtResponse.setText(responseBody + "\n");

        TcpClient tcpclient = null;
        tcpclient = new TcpClient(this, httpResult.getData().getLongconnIP(), httpResult.getData().getLongconnPort());
        tcpclient.start();
    }

    @Override
    public void connected(TcpClient client){
        LongConnRegisterMessage longconnRegMsg = new LongConnRegisterMessage();
        longconnRegMsg.setUid(uid);
        longconnRegMsg.setToken(token);

        ObjectMapper mapper = new ObjectMapper();
        LongConnMessage longconnMsg = new LongConnMessage();
        longconnMsg.setErrNo(0);
        longconnMsg.setType(1);

        String longconnMsgInJson = null;
        try {
            longconnMsg.setPayload(mapper.writeValueAsString(longconnRegMsg));
            longconnMsgInJson = mapper.writeValueAsString(longconnMsg);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        client.send(longconnMsgInJson);

        this.runOnUiThread(
                new Runnable() {
                    @Override
                    public void run() {
                        TextView txtResponse = (TextView)findViewById(R.id.txtResponse);
                        txtResponse.setText(txtResponse.getText() + "connected to longconn.\n");
                    }
                }
        );
    }

    @Override
    public void connectionLost(TcpClient client) {
        // todo reconoect
    }

    @Override
    public void packetReceived(TcpClient client, TcpPacket packet) {
        final TextView txtResponse = (TextView)findViewById(R.id.txtResponse);
        txtResponse.post(new Runnable() {
            @Override
            public void run() {
                txtResponse.setText("packet received.");
            }
        });
    }
}
