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

import java.net.MalformedURLException;

public class JoinActivity extends Activity {
    private Button join, overlap;
    private EditText e_id, e_password1, e_password2;
    private String id, password1, password2;
    protected JSONObject mResult = null;
    protected RequestQueue mQueue = null;
    int flag=0;
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

        overlap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                id = e_id.getText().toString();
                requestinfo();

                try {
                    JSONArray list = mResult.getJSONArray("list");
                    for (int i = 0; i < list.length(); i++) {
                        JSONObject node = list.getJSONObject(i);
                        String d_email = node.getString("email");
                        if(d_email.equals(id)) {
                            flag = 0;
                            Toast.makeText(getApplication(), "중복된 아이디가 존재", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        if(i== list.length() - 1) {
                            Toast.makeText(getApplication(), "아이디 사용 가능", Toast.LENGTH_SHORT).show();
                            flag = 1;
                        }
                    }
                } catch (JSONException | NullPointerException e) {
                    e.printStackTrace();
                    Toast.makeText(JoinActivity.this, "Error"+e.toString(), Toast.LENGTH_SHORT).show();
                    mResult = null;
                }
            }
        });

        join.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(flag==0){
                    Toast.makeText(getApplication(), "ID 중복 확인 필요", Toast.LENGTH_SHORT).show();
                }
                if(flag==1) {
                    try {
                        PHPRequest request = new PHPRequest("http://202.31.201.139/join.php");
                        id = e_id.getText().toString();
                        password1 = e_password1.getText().toString();
                        password2 = e_password2.getText().toString();
                        String result = request.PhPtest(id, password1);

                        if (password1.equals(password2)) {
                            Intent intent = new Intent(getApplicationContext(), MainActivity.class); //메인화면으로 이동
                            startActivity(intent);
                            Toast.makeText(getApplication(), "회원가입 완료", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(getApplication(), "비밀번호가 일치하지 않습니다.", Toast.LENGTH_SHORT).show();
                        }
                    } catch (MalformedURLException e) {
                        e.printStackTrace();
                    }
                }
                else {
                    Toast.makeText(getApplication(), "", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    protected void requestinfo() {
        String url = "http://202.31.201.139/user.php";
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
}
