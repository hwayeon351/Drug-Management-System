/*
 * ------------------------------------------------------------------------
 * Copyright 2014 Korea Electronics Technology Institute
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ------------------------------------------------------------------------
 */

package kr.re.keti.nCubeThyme;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

import kr.re.keti.nCubeThyme.service.NCubeService;
import kr.re.keti.nCubeThyme.setting.NCubeSetting;
import kr.re.keti.nCubeThyme.setting.NCubeSettingData;
import kr.re.keti.nCubeThyme.setting.NCubeSettingDataShare;
import kr.re.keti.nCubeThyme.service.INCubeService;
import kr.re.keti.nCubeThyme.service.nCubeCore.tasserver.TasSender;

public class MainActivity extends Activity {

    public static final int SETTING_CODE = 1;
    private static String nCubeSetupData = "";
    private boolean settingState = false;
    private boolean runningState = false;
    private boolean serviceConnection = false;
    private Intent intent;
    private INCubeService nCubeService = null;
    private String  timeall = "";

    EditText time1, time2, time3;
    TextView result;

    private ServiceConnection serveConn = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            nCubeService = INCubeService.Stub.asInterface(service);
            Log.d("bindService", "onServiceConnected");
            serviceConnection = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            Log.d("bindService", "onServiceDisconnected");
            serviceConnection = false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Log.i("TEST",  "2 Start  Main");
        //set time value
        findViewById(R.id.btn_setting).setOnClickListener(
                new Button.OnClickListener(){
                    @Override
                    public void onClick(View v){
                        new Thread(new Runnable(){
                            @Override
                            public void run(){
                                try{
                                    time1 = (EditText)findViewById(R.id.txt_time1);
                                    time2 = (EditText)findViewById(R.id.txt_time2);
                                    time3 = (EditText)findViewById(R.id.txt_time3);
                                    //option (space)
                                    timeall = time1.getText().toString() +" "+ time2.getText().toString() +" "+ time3.getText().toString();
                                    Log.i("timeall:", timeall);

                                    Socket socket = new Socket("localhost", 7622);
                                    PrintWriter writer = new PrintWriter(socket.getOutputStream(), true);

                                    JSONObject contentObj = new JSONObject();
                                    try{
                                        contentObj.put("ctname", "cnt-setting");
                                        contentObj.put("con", timeall);
                                        Log.i("TEST", "send container" + timeall);
                                    //    Toast.makeText(getApplicationContext(), "설정 되었습니다.", Toast.LENGTH_SHORT).show();
                                    }catch(JSONException e){
                                        e.printStackTrace();
                                    }

                                    writer.println(contentObj.toString());
                                    writer.flush();
                                }catch (Exception e){
                                    e.printStackTrace();
                                    Log.e("Test", "There is  problem" + e.toString());
                                }
                            }
                        }).start();
                    }
                });
        findViewById(R.id.runCube).setOnClickListener(
                new Button.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // Run nCube activity

                        if(settingState && !serviceConnection) {
                            intent = new Intent(MainActivity.this, NCubeService.class);
                            NCubeSettingDataShare regData = new NCubeSettingDataShare(nCubeSetupData);
                            intent.putExtra("regData", regData);
                            startService(intent);
                            Intent serviceIntent = new Intent();
                            serviceIntent.setClass(getApplicationContext(), NCubeService.class);
                            bindService(serviceIntent, serveConn, Context.BIND_AUTO_CREATE);
                            runningState = true;
                            Toast.makeText(getApplicationContext(), "&Cube service is start", Toast.LENGTH_SHORT).show();
                        }

                        else if(settingState && serviceConnection) {
                            Toast.makeText(getApplicationContext(), "&Cube service is already running", Toast.LENGTH_SHORT).show();
                        }

                        else {
                            Toast.makeText(getApplicationContext(), "Need Settings", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
        );

        findViewById(R.id.checkCube).setOnClickListener(
                new Button.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // Send to nCube service

                        if(settingState && runningState) {
                            boolean state = false;
                            try {
                                state = nCubeService.checkRunningState();
                            } catch (RemoteException e) {
                                e.printStackTrace();
                            }

                            if (state) {
                                runningState = true;
                                Toast.makeText(getApplicationContext(), "&Cube service is running now", Toast.LENGTH_SHORT).show();
                            }
                        }

                        else if (settingState && !runningState) {
                            runningState = false;
                            Toast.makeText(getApplicationContext(), "&Cube service isn't running", Toast.LENGTH_SHORT).show();
                        }

                        else {
                            Toast.makeText(getApplicationContext(), "Need Settings", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
        );

        findViewById(R.id.stopCube).setOnClickListener(
                new Button.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // Stop nCube activity
                        if(settingState && runningState) {
                            Intent stopIntent = new Intent();
                            stopIntent.setClass(getApplicationContext(), NCubeService.class);
                            try {
                                nCubeService.setStopService();
                                unbindService(serveConn);
                            } catch (RemoteException e) {
                                e.printStackTrace();
                            }
                            getApplicationContext().stopService(stopIntent);
                            runningState = false;
                            serviceConnection = false;
                            Toast.makeText(getApplicationContext(), "&Cube service stop success", Toast.LENGTH_SHORT).show();
                        }

                        else if (settingState && !runningState) {
                            Toast.makeText(getApplicationContext(), "&Cube service isn't running", Toast.LENGTH_SHORT).show();
                        }

                        else {
                            Toast.makeText(getApplicationContext(), "Need Settings", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
        );

        findViewById(R.id.sendData).setOnClickListener(
                new Button.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // Send to CSE service
                        Toast.makeText(getApplicationContext(), settingState+""+runningState, Toast.LENGTH_SHORT).show();
                        new Thread(new Runnable(){
                            @Override
                            public void run(){
                                try{
                                    Log.i("Test",  "aaa");
                                    HttpClient client = new DefaultHttpClient();
                                    HttpGet request = new HttpGet("http://210.94.185.17:8888/mysql_php.php");
                                    HttpResponse response = client.execute(request);
                                    String html = "";
                                    InputStream in = response.getEntity().getContent();
                                    BufferedReader reader  = new BufferedReader(new InputStreamReader(in));
                                    StringBuilder str = new StringBuilder();
                                    String line = null;
                                    while((line=reader.readLine() )!=null){
                                        str.append(line);
                                    }
                                    in.close();
                                    html = str.toString();
                                    Log.i("This is html", html);

                                    /*Socket socket = new Socket("localhost", 7622);
                                    PrintWriter writer = new PrintWriter(socket.getOutputStream(), true);

                                    JSONObject contentObj = new JSONObject();
                                    try{
                                        contentObj.put("ctname", "cnt-setting");
                                        contentObj.put("con", "12:00 15:00 17:00");
                                        Log.i("TEST", "send container");
                                    }catch(JSONException e){
                                        e.printStackTrace();
                                    }

                                    writer.println(contentObj.toString());
                                    writer.flush();*/
                                    result = (TextView)findViewById(R.id.result_text);
                                    result.setText(html);
                                }catch (Exception e){
                                    e.printStackTrace();
                                    Log.e("Test", "There is  problem" + e.toString());
                                }
                            }
                        }).start();
                    }
                }
        );

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

            //return true;
            Intent intent = new Intent(MainActivity.this, NCubeSetting.class);
            //startActivity(intent);
            startActivityForResult(intent, SETTING_CODE);
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == SETTING_CODE) {

            if (resultCode != RESULT_OK) {
                // error
            }

            else {
                //Intent intent = getIntent();
                NCubeSettingDataShare settingData = (NCubeSettingDataShare) data.getSerializableExtra("setting");
                Toast.makeText(getApplicationContext(), settingData.configData, Toast.LENGTH_SHORT).show();
                nCubeSetupData = settingData.configData;

                if(!nCubeSetupData.isEmpty()) {
                    settingState = true;
                }
            }
        }

        else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    protected void onDestroy() {
        if (runningState) {
            unbindService(serveConn);
            runningState = false;
        }
        super.onDestroy();
    }

}