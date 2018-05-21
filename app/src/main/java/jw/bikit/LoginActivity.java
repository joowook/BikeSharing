package jw.bikit;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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

public class LoginActivity extends Activity {
    private Button login;
    private EditText e_id, e_password;
    private String id, password;
    protected JSONObject mResult = null;
    protected RequestQueue mQueue = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);
        mQueue = Volley.newRequestQueue(this);
        login = (Button)findViewById(R.id.login);
        e_id = (EditText)findViewById(R.id.id);
        e_password=(EditText)findViewById(R.id.password);

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                id = e_id.getText().toString();
                password = e_password.getText().toString();
                requestinfo();
//                Intent intent =new Intent(getApplicationContext(),NMapViewer.class);
//                startActivity(intent);
            }
        });


    }

    protected void info() {
        try {
            JSONArray list = mResult.getJSONArray("list");
            for (int i = 0; i < list.length(); i++) {
                JSONObject node = list.getJSONObject(i);

                String d_email = node.getString("email");

                if(!d_email.equals(id)) {
                    continue;
                }
                else {
                    //Toast.makeText(LoginActivity.this, d_email , Toast.LENGTH_SHORT).show();
                    String d_password = node.getString("password");
                    if(password.equals(d_password)) {
                        Intent intent =new Intent(getApplicationContext(),NMapViewer.class);
                        startActivity(intent);
                        Toast.makeText(LoginActivity.this, "로그인 성공" , Toast.LENGTH_SHORT).show();
                    }else{
                        Toast.makeText(LoginActivity.this, "ID나 Password 일치하지 않음" , Toast.LENGTH_SHORT).show();
                    }
                }
            }
        } catch (JSONException | NullPointerException e) {
            e.printStackTrace();
            Toast.makeText(this, "Error: " + e.toString(),
                    Toast.LENGTH_LONG).show();
            Toast.makeText(LoginActivity.this, "여긴가", Toast.LENGTH_SHORT).show();
            mResult = null;
        }
    }

    protected void requestinfo() {
        String url = "http://192.168.43.168/user.php";
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Toast.makeText(LoginActivity.this, "디비 연결 성공", Toast.LENGTH_SHORT).show();
                        mResult = response;
                        info();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(LoginActivity.this, "Error : " + error.toString(), Toast.LENGTH_SHORT).show();
                    }
                }
        );
        mQueue.add(request);
    }


}
