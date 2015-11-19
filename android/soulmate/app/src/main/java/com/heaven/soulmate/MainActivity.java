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

import com.heaven.soulmate.model.HttpAsyncTask;
import com.heaven.soulmate.model.HttpRequestData;
import com.heaven.soulmate.model.HttpResponseData;
import com.heaven.soulmate.model.IHttpDelegate;
import com.heaven.soulmate.model.longconn.ITcpClientDelegate;
import com.heaven.soulmate.model.longconn.TcpClient;
import com.heaven.soulmate.model.longconn.TcpPacket;

import org.json.JSONException;
import org.json.JSONObject;


public class MainActivity extends AppCompatActivity
    implements IHttpDelegate ,ITcpClientDelegate
{
    IHttpDelegate mainActivity = this;

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

                JSONObject requestData = new JSONObject();
                try {
                    requestData.put("phone", "15011113304");
                    requestData.put("password", "803048");
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                HttpRequestData request = new HttpRequestData();
                request.setUrl("http://192.168.132.69:8080/soulmate/login");
                request.setRequest(requestData);

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
        JSONObject responseObj = response.getResponse();

        TextView txtResponse = (TextView)findViewById(R.id.txtResponse);
        txtResponse.setText(responseObj.toString() + "\n");

        TcpClient tcpclient = null;
        try {
            JSONObject responseData = responseObj.getJSONObject("data");
            tcpclient = new TcpClient(this, responseData.getString("longconn_ip"), responseData.getInt("longconn_port"));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        tcpclient.start();
    }

    @Override
    public void connected(TcpClient client) {
        TextView txtResponse = (TextView)findViewById(R.id.txtResponse);
        txtResponse.setText(txtResponse.getText() + "connected.");
    }

    @Override
    public void connectionLost(TcpClient client) {
        // todo reconoect
    }

    @Override
    public void packetReceived(TcpClient client, TcpPacket packet) {

    }
}
