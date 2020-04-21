package com.infosys.touchless.claims;

import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class LoginActivity extends AppCompatActivity {
    public static EditText username,password;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        TextView login;
        final ActionBar actionBar = ((AppCompatActivity) this).getSupportActionBar();
        actionBar.setDisplayShowHomeEnabled(false);
        actionBar.setTitle("Insurance Agent");
        actionBar.setBackgroundDrawable(getDrawable(R.color.colorPrimaryDark));

        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setDisplayShowCustomEnabled(true);


        username=findViewById(R.id.username);

        password=findViewById(R.id.password);

        login=findViewById(R.id.log_in);

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Log_In(username.getText().toString(),password.getText().toString()).execute();
            }
        });


    }
    public class  Log_In extends AsyncTask<Void,Void, JSONObject> {
        String user_name,pass_word;


        public Log_In(String username,String password){
            this.user_name=username;
            this.pass_word=password;
        }


        @Override
        protected JSONObject doInBackground(Void... voids) {

            try {
                OkHttpClient client = new OkHttpClient();
                RequestBody requestBody = new MultipartBody.Builder()
                        .setType(MultipartBody.FORM)
                        .addFormDataPart("username", user_name)
                        .addFormDataPart("password", pass_word)
                        .build();


                Request request = new Request.Builder()
                        .url(LoginActivity.this.getResources().getString(R.string.api_server)+"agent_login")
                        .post(requestBody)
                        .build();

                Log.e("Request","sent");

                Response response = client.newCall(request).execute();
                Log.e("Response",response.message()+"      "+response.body());
                InputStream inputStream = response.body().byteStream();
                InputStreamReader isReader = new InputStreamReader(inputStream);
                BufferedReader reader = new BufferedReader(isReader);
                StringBuffer sb = new StringBuffer();
                String str;
                while((str = reader.readLine())!= null){
                    sb.append(str);
                }
                Log.e("Response",sb.toString());

                JSONObject myresponse= new JSONObject(sb.toString());
                return  myresponse;

            } catch (IOException e) {
                Log.e("IO",e.getMessage());
            } catch (JSONException e) {
                Log.e("JSONEXCEPTION",e.getMessage());
            }
            catch (Exception e) {
                Log.e("EXCEPTION",e.getMessage());
            }
            return null;
        }


        @Override
        protected void onPostExecute(JSONObject s) {
            try{
                if(s.getString("status").equals("true")) {
                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    intent.putExtra("agent_name", s.getString("agent_name"));
                    startActivity(intent);
                    finish();
                }
                else{
                    Toast.makeText(LoginActivity.this,"Wrong username or password",Toast.LENGTH_LONG).show();

                    username.setText("");
                    password.setText("");
                }

            }
            catch(Exception e){
                Log.e("Exception",e.getMessage());

            }

            Log.e("Log_IN","Post");

            super.onPostExecute(s);
        }
    }


}
