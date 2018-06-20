package jw.bikit;

import android.app.Activity;
import android.app.Notification;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.res.ResourcesCompat;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

import static android.os.SystemClock.sleep;

public class MainActivity extends BaseActivity {
    protected JSONObject mResult = null;
    protected RequestQueue mQueue = null;

    private  static final String TAG = "MAIN";

    private Button db;
    private Button login, join;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mQueue = Volley.newRequestQueue(this);
        mQueue.start();

        db = (Button)findViewById(R.id.db_connect);
        login = (Button)findViewById(R.id.login);
        join = (Button)findViewById(R.id.join);

        db.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                requestinfo();
            }
        });

        join.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                startProgress();

                Intent intent =new Intent(getApplicationContext(),JoinActivity.class);
                startActivity(intent);
            }
        });

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                startProgress();

                Intent intent =new Intent(getApplicationContext(),LoginActivity.class);
                startActivity(intent);
            }
        });
    }

//    private void startProgress() {
//
//        progressON("Loading...");
//
//        new Handler().postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                progressOFF();
//            }
//        }, 2000);
//    }

    protected void requestinfo() {
        String url = "http://14.49.39.100/user.php";
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Toast.makeText(MainActivity.this, "디비 연결 성공", Toast.LENGTH_SHORT).show();
                        mResult = response;
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
}
