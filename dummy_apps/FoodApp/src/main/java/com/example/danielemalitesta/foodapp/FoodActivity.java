package com.example.danielemalitesta.foodapp;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Calendar;
import java.util.Random;

public class FoodActivity extends AppCompatActivity {

    public Spinner food_list;
    public Button buy;
    public Button logout;
    public EditText calories;
    public static String broker_ip = "broker.hivemq.com";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_food);
        final String clientId = "calorie" + getIntent().getStringExtra("user");
        final MqttAndroidClient client = new MqttAndroidClient(FoodActivity.this, "tcp://" + broker_ip + ":1883",
                        clientId);
        food_list = findViewById(R.id.food_list);
        buy = findViewById(R.id.buy);
        logout = findViewById(R.id.logout);
        calories = findViewById(R.id.calories);
        buy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                JSONObject json = new JSONObject();
                String calorie = calories.getText().toString();
                if (calorie.equals("")){
                    Toast.makeText(FoodActivity.this, "insert calories!", Toast.LENGTH_SHORT).show();
                }
                else {
                    try {
                        Calendar calendar = Calendar.getInstance();

                        //date
                        String date;
                        int year = calendar.get(Calendar.YEAR);
                        int month = calendar.get(Calendar.MONTH) + 1;
                        int day = calendar.get(Calendar.DAY_OF_MONTH);

                        if (month < 10 && day < 10) {
                            date = year + "-0" + month + "-0" + day;
                        } else if (month < 10) {
                            date = year + "-0" + month + "-" + day;
                        } else if (day < 10) {
                            date = year + "-" + month + "-0" + day;
                        } else {
                            date = year + "-" + month + "-" + day;
                        }

                        //time
                        String time = "";
                        int hour = calendar.get(Calendar.HOUR_OF_DAY);
                        int minute = calendar.get(Calendar.MINUTE);
                        int second = calendar.get(Calendar.SECOND);

                        if (hour < 10) {
                            time = "0" + hour + ":";
                        } else {
                            time = hour + ":";
                        }
                        if (minute < 10) {
                            time = time + "0" + minute + ":";
                        } else {
                            time = time + minute + ":";
                        }
                        if (second < 10) {
                            time = time + "0" + second;
                        } else {
                            time = time + second;
                        }

                        json.put("app", "calorie");
                        json.put("value", calorie);
                        json.put("date", date);
                        json.put("time", time);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    check_broker(client, json);
                }
            }
        });
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences sharedpreferences = getSharedPreferences("user_login", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedpreferences.edit();
                editor.clear();
                editor.commit();
                finish();
            }
        });
    }
    @Override
    public void onBackPressed()
    {
        //do nothing
    }
    public void check_broker(final MqttAndroidClient client, final JSONObject json){
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                try{
                    URL url = new URL("http://www.mqtt-dashboard.com/");
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    connection.setRequestMethod("POST");
                    if(connection.getResponseCode() == 200){
                        connect_and_publish(client, json);
                    }
                } catch(Exception e){
                    e.printStackTrace();
                }
            }
        });
        t.start();
    }
    public void connect_and_publish(final MqttAndroidClient client, final JSONObject json){
        if(!client.isConnected()){
            try {
                IMqttToken token = client.connect(getMqttConnectionOption());
                token.setActionCallback(new IMqttActionListener() {
                    @Override
                    public void onSuccess(IMqttToken asyncActionToken) {
                        String topic = "calorie/" + getIntent().getStringExtra("user");
                        byte[] encodedPayload = new byte[0];
                        try {
                            encodedPayload = json.toString().getBytes("UTF-8");
                            MqttMessage message = new MqttMessage(encodedPayload);
                            client.publish(topic, message);
                        } catch (UnsupportedEncodingException | MqttException e) {
                            e.printStackTrace();
                        }
                    }
                    @Override
                    public void onFailure(IMqttToken asyncActionToken, Throwable exception) {

                    }
                });
            } catch (MqttException e) {
                e.printStackTrace();
            }
        }
        else{
            String topic = "calorie/" + getIntent().getStringExtra("user");
            byte[] encodedPayload = new byte[0];
            try {
                encodedPayload = json.toString().getBytes("UTF-8");
                MqttMessage message = new MqttMessage(encodedPayload);
                client.publish(topic, message);
            } catch (UnsupportedEncodingException | MqttException e) {
                e.printStackTrace();
            }
        }
    }
    private MqttConnectOptions getMqttConnectionOption() {
        MqttConnectOptions mqttConnectOptions = new MqttConnectOptions();
        mqttConnectOptions.setCleanSession(false);
        return mqttConnectOptions;
    }
}