package jw.bikesharing;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

import jw.bikesharing.BluetoothService;


public class MainActivity extends AppCompatActivity {
    protected JSONObject mResult = null;
    protected RequestQueue mQueue = null;
    private BluetoothAdapter btAdapter;
    private BluetoothService btService = null;
    private BluetoothDevice btDevice = null;
    private ImageView img = null;
    BluetoothSocket mmSocket = null;
    OutputStream mOutputStream = null;
    InputStream mInputStream = null;

    private  static final String TAG = "MAIN";
    private static final int REQUEST_CONNECT_DEVICE = 1;
    private static final int REQUEST_ENABLE_BT = 2;

    private Button btn_Connect;
    private TextView txt_Result;
    private Button db;
    private Button login, join;
    private final Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mQueue = Volley.newRequestQueue(this);

        db = (Button)findViewById(R.id.db_connect);
        login = (Button)findViewById(R.id.login);
        join = (Button)findViewById(R.id.join);
        btn_Connect = (Button)findViewById(R.id.btn_connect);

        btn_Connect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (btService.getDeviceState())
                    btService.enableBluetooth();
                else
                    finish();
            }
        });

        db.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                requestinfo();
            }
        });

        join.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent =new Intent(getApplicationContext(),JoinActivity.class);
                startActivity(intent);
            }
        });

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent =new Intent(getApplicationContext(),LoginActivity.class);
                startActivity(intent);
            }
        });



        btn_Connect = (Button)findViewById(R.id.btn_connect);

        btn_Connect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(btService.getDeviceState())
                    btService.enableBluetooth();
                else
                    finish();
            }
        });

        if(btService == null){
            btService = new BluetoothService(this, mHandler);
        }
    }
    protected void requestinfo() {
        String url = "http://202.31.201.138/bike.php";
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Toast.makeText(MainActivity.this, "디비 연결 성공", Toast.LENGTH_SHORT).show();
                        mResult = response;
                        info();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(MainActivity.this, "Error : " + error.toString(), Toast.LENGTH_SHORT).show();
                    }
                }
        );
        mQueue.add(request);
    }

    protected void info() {
        try {
            JSONArray list = mResult.getJSONArray("list");
            for (int i = 0; i < list.length(); i++) {
                JSONObject node = list.getJSONObject(i);
                int number = node.getInt("number");
                String name = node.getString("name");
                String address = node.getString("address");
                Toast.makeText(MainActivity.this, ""+number+","+name+","+address, Toast.LENGTH_SHORT).show();
            }
        } catch (JSONException | NullPointerException e) {
            e.printStackTrace();
            Toast.makeText(this, "Error: " + e.toString(),
                    Toast.LENGTH_LONG).show();
            Toast.makeText(MainActivity.this, "여긴가", Toast.LENGTH_SHORT).show();
            mResult = null;
        }

    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d(TAG, "onActivityResult" + resultCode);

        switch (requestCode){
            case REQUEST_CONNECT_DEVICE:
                if(resultCode == Activity.RESULT_OK) {
                    btService.getDeviceInfo(data);
                }
                break;

            case REQUEST_ENABLE_BT:
                if(resultCode == Activity.RESULT_OK){
                    btService.scanDevice();
                }else {
                    Log.d(TAG, "Bluetooth is not enabled");
                }
                break;
        }
    }
}
