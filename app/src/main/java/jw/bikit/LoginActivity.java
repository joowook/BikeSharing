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

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.MessageDigest;

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
            }
        });


    }

    protected void info() {
        LockPassword();
        try {
            JSONArray list = mResult.getJSONArray("list");

//            if(list.length()==0)
//            {
//                Toast.makeText(LoginActivity.this, "ID나 Password 일치하지 않음" , Toast.LENGTH_SHORT).show();
//                return;
//            }
//            else {
//                Toast.makeText(LoginActivity.this, "길이 ="+ list.length() , Toast.LENGTH_SHORT).show();
//            }
            if(id.equals("")) {
                Toast.makeText(LoginActivity.this, "아이디를 입력해 주세요." , Toast.LENGTH_SHORT).show();
                return;
            }
            else if(password.equals("e3b0c44298fc1c149afbf4c8996fb92427ae41e4649b934ca495991b7852b855")) {
                Toast.makeText(LoginActivity.this, "비밀번호를 입력해 주세요." , Toast.LENGTH_SHORT).show();
                return;
            }

            for (int i = 0; i < list.length(); i++) {
                JSONObject node = list.getJSONObject(i);

                String d_email = node.getString("email");

                if(!d_email.equals(id)) {
                    if(i == list.length()-1) {
                        Toast.makeText(LoginActivity.this, "존재하지 않는 아이디 입니다." , Toast.LENGTH_SHORT).show();
                    }
                    continue;
                }
                else {
                    String d_password=node.getString("password");
                    if( d_password.equals(password))
                    {
                        Intent intent =new Intent(getApplicationContext(),NMapViewer.class);
                        startActivity(intent);
                        Toast.makeText(LoginActivity.this, "로그인 성공" , Toast.LENGTH_SHORT).show();
                        break;
                    }else {
                        Toast.makeText(LoginActivity.this, "비밀번호가 틀렸습니다.", Toast.LENGTH_SHORT).show();
                        break;
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

    public void LockPassword() {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(password.getBytes("UTF-8"));
            StringBuffer hexString = new StringBuffer();

            for (int i = 0; i < hash.length; i++) {
                String hex = Integer.toHexString(0xff & hash[i]);
                if (hex.length() == 1)
                    hexString.append('0');
                hexString.append(hex);
            }
            password=hexString.toString();

        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }


    protected void requestinfo() {
        String url = "http://14.49.39.100/user.php";
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        //Toast.makeText(LoginActivity.this, "디비 연결 성공", Toast.LENGTH_SHORT).show();
                        mResult = response;
                        info();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        //Toast.makeText(LoginActivity.this, "Error : " + error.toString(), Toast.LENGTH_SHORT).show();
                    }
                }
        );
        mQueue.add(request);
    }


}