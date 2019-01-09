package com.example.danielemalitesta.waterapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

public class MainActivity extends AppCompatActivity {

    private EditText username;
    private EditText password;
    public TextView error;
    public Button login;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final SharedPreferences sharedpreferences = getSharedPreferences("user_login", Context.MODE_PRIVATE);
        if(sharedpreferences.contains("username")){
            Intent intent = new Intent(MainActivity.this, WaterActivity.class);
            intent.putExtra("user", sharedpreferences.getString("username", ""));
            startActivity(intent);
        }
        username = findViewById(R.id.username);
        password = findViewById(R.id.password);
        error = findViewById(R.id.error);
        login = findViewById(R.id.login);
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(username.getText().toString().equals("") || password.getText().toString().equals("")){
                    Toast.makeText(MainActivity.this, "fill in all fields!", Toast.LENGTH_SHORT).show();
                }
                else {
                    Thread t = new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                URL url = new URL("http://adaptapppoliba.altervista.org/progetto/login.php");
                                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                                connection.setRequestMethod("POST");
                                connection.setDoOutput(true);
                                OutputStreamWriter wr = new OutputStreamWriter(connection.getOutputStream());
                                String data = URLEncoder.encode("username", "UTF-8")
                                        + "=" + URLEncoder.encode(username.getText().toString(), "UTF-8");

                                data += "&" + URLEncoder.encode("password", "UTF-8") + "="
                                        + URLEncoder.encode(password.getText().toString(), "UTF-8");
                                wr.write(data);
                                wr.flush();
                                wr.close();
                                InputStream in = connection.getInputStream();
                                StringBuffer sb = new StringBuffer();
                                int chr;
                                while ((chr = in.read()) != -1) {
                                    sb.append((char) chr);
                                }
                                String reply = sb.toString();
                                if (reply.startsWith("2")) {
                                    System.out.println("server is on");
                                    SharedPreferences.Editor editor = sharedpreferences.edit();
                                    editor.putString("username", username.getText().toString());
                                    editor.commit();
                                    Intent intent = new Intent(MainActivity.this, WaterActivity.class);
                                    intent.putExtra("user", username.getText().toString());
                                    startActivity(intent);
                                }
                                else {
                                    error.setText("username or password not correct");
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                                System.out.println("an exception occurred!");
                            }
                        }
                    });
                    t.start();
                }
            }
        });
    }
}
