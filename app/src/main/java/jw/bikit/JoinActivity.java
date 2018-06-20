package jw.bikit;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
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

import java.net.MalformedURLException;
import java.security.MessageDigest;

public class JoinActivity extends BaseActivity {
    private Button join, overlap;
    private EditText e_id, e_password1, e_password2;
    private String id, password1, password2;
    protected JSONObject mResult = null;
    protected RequestQueue mQueue = null;
    boolean flag=false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.join);
        mQueue = Volley.newRequestQueue(this);
        NetworkUtil.setNetworkPolicy();
        join = (Button)findViewById(R.id.join);
        overlap = (Button)findViewById(R.id.overlap);
        e_id = (EditText)findViewById(R.id.id);
        e_password1=(EditText)findViewById(R.id.password1);
        e_password2=(EditText)findViewById(R.id.password2);

        requestinfo();

        join.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(!flag){
                    Toast.makeText(getApplication(), "ID 중복 확인 필요", Toast.LENGTH_SHORT).show();
                }
                else {
                    try {
                        PHPRequest request = new PHPRequest("http://14.49.39.100/join.php");
                        id = e_id.getText().toString();
                        password1 = e_password1.getText().toString();
                        password2 = e_password2.getText().toString();

                        if (password1.equals(password2)) {
                            if(password1.length() <8 || password1.length() >16) {
                                Toast.makeText(getApplication(), "8자 이상 16자 이하로 입력해 주세요.", Toast.LENGTH_SHORT).show();
                                return;
                            }
                            LockPassword();
                            String result = request.PhPtest(id, password1);

                            Intent intent = new Intent(getApplicationContext(), LoginActivity.class); //메인화면으로 이동
                            startActivity(intent);
                            flag=false;
                            Toast.makeText(getApplication(), "회원가입 완료", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(getApplication(), "비밀번호가 일치하지 않습니다.", Toast.LENGTH_SHORT).show();
                        }
                    } catch (MalformedURLException e) {
                        e.printStackTrace();
                    }
                }
            }
        });


        overlap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                id = e_id.getText().toString();

                if(id.equals("")) {
                    Toast.makeText(JoinActivity.this, "아이디를 입력해 주세요." , Toast.LENGTH_SHORT).show();
                    return;
                }
                else {
                    for(int i=0; i<id.length(); i++) {
                        if(id.charAt(i) == '@') {
                            break;
                        }
                        else if(i == id.length()-1) {
                            Toast.makeText(JoinActivity.this, "이메일 형식으로 입력해 주세요." , Toast.LENGTH_SHORT).show();
                            return;
                        }
                    }
                }

                try {
                    JSONArray list = mResult.getJSONArray("list");
                    for (int i = 0; i < list.length(); i++) {
                        JSONObject node = list.getJSONObject(i);
                        String d_email = node.getString("email");
                        if(d_email.equals(id)) {
                            flag = false;
                            Toast.makeText(getApplication(), "중복된 아이디가 존재", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        if(i== list.length() - 1) {
                            Toast.makeText(getApplication(), "아이디 사용 가능", Toast.LENGTH_SHORT).show();
                            flag = true;
                        }
                    }
                } catch (JSONException | NullPointerException e) {
                    e.printStackTrace();
                    Toast.makeText(JoinActivity.this, "Error"+"여기??", Toast.LENGTH_SHORT).show();
                    mResult = null;
                }
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
                        mResult = response;
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                }
        );
        mQueue.add(request);
    }

    public void LockPassword() {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(password1.getBytes("UTF-8"));
            StringBuffer hexString = new StringBuffer();

            for (int i = 0; i < hash.length; i++) {
                String hex = Integer.toHexString(0xff & hash[i]);
                if (hex.length() == 1)
                    hexString.append('0');
                hexString.append(hex);
            }
            password1=hexString.toString();
            //Toast.makeText(JoinActivity.this, hexString.toString(), Toast.LENGTH_SHORT).show();
            //System.out.println(hexString.toString());
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

}