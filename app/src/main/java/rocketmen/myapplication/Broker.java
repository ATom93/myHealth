package rocketmen.myapplication;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.json.JSONObject;

import java.io.Serializable;

import static android.content.Context.NOTIFICATION_SERVICE;

/**
 * Created by luigi on 15/12/2018.
 */

public class Broker implements Serializable {

    public static MqttAndroidClient client;
    static Context context;
    static Activity activity;
    String brokerAddress = "tcp://broker.hivemq.com:1883";


    public Broker(Context context){
        this.context = context;
    }


    public void createClient(String clientId){
        client = new MqttAndroidClient(this.context, brokerAddress, clientId);
    }

    public void connectToBroker(){

        try {
            IMqttToken token = client.connect(getMqttConnectionOption());
            token.setActionCallback(new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    // We are connected
                    Toast.makeText(context, "Connected to broker", Toast.LENGTH_SHORT).show();

                }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                    // Something went wrong e.g. connection timeout or firewall problems
                    Toast.makeText(context, "Fail (volevi)", Toast.LENGTH_SHORT).show();

                }
            });
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    public static void listenToUpdates(){
        client.setCallback(new MqttCallback() {
            @Override
            public void connectionLost(Throwable cause) {

            }

            @Override
            public void messageArrived(String topic, MqttMessage message) throws Exception {
                Notification noti = new Notification.Builder(context)
                        .setContentTitle("Update")
                        .setContentText(message.toString())
                        .setSmallIcon(R.drawable.ic_launcher_background)
                        .build();
                NotificationManager notificationManager =
                        (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);
                notificationManager.notify(0, noti);


                MemoryManager memoryManager = new MemoryManager(context);

                //Prendo dailyModel dalla memoria
                String daily = memoryManager.readInMemory2("JSONDaily");

                //lo converto in json
                JSONObject jDaily = new JSONObject(daily);

                //Converto il message in json
                JSONObject jMessage = new JSONObject(message.toString());

                //recupero nome app, value, date e time
                String appName = jMessage.getString("app");
                String value = jMessage.getString("value");
                String date = jMessage.getString("date");
                String time = jMessage.getString("time");
                String username = memoryManager.readInMemory2("username");

                System.out.println("ARRIVATA UNA: "+appName);

                String payload = "username="+username+"&app="+appName+"&value="+value+"&date="+date+"&time="+time;
                String address = "http://adaptapppoliba.altervista.org/progetto/crea_utilizzo.php";
                HttpRequester requester = new HttpRequester(address,payload,activity);
                requester.execute("");

                String oldValue = jDaily.getString(appName);

                if(!appName.equals("peso")){
                    //Sommo al valore presente nel modello, il nuovo dato
                    double dOldValue = Double.parseDouble(oldValue);
                    double dValue = Double.parseDouble(value);
                    double dNewValueOfDaily = dOldValue+dValue;

                    String newValueOfDaily = Double.toString(dNewValueOfDaily);

                    //lo scrivo nel daily model
                    jDaily.put(appName,newValueOfDaily);
                    daily = jDaily.toString();

                }
                else {
                    //Sovrascrivo il valore precedente con il nuovo peso
                    //lo scrivo nel daily model
                    jDaily.put(appName,value);
                    daily = jDaily.toString();

                    //modifico il gold standard
                    String goal = new UserHandler().calculateGoal(daily);
                    System.out.println("Da BERA: "+goal);

                    //lo rimetto in memoria
                    memoryManager.writeInMemory2("goal",goal);
                }

                //rimetto il daily model aggiornato in memoria
                memoryManager.writeInMemory2("JSONDaily",daily);

                UserHandler userHandler = new UserHandler(context);
                userHandler.updateTipsDaily(appName);
            }

            @Override
            public void deliveryComplete(IMqttDeliveryToken token) {

            }
        });
    }

    private MqttConnectOptions getMqttConnectionOption() {
        MqttConnectOptions mqttConnectOptions = new MqttConnectOptions();
        mqttConnectOptions.setCleanSession(false);
        //mqttConnectOptions.setKeepAliveInterval(60);
        //mqttConnectOptions.setConnectionTimeout(30);
        return mqttConnectOptions;
    }

    public static void subscribeToTopic(String topic){

        int qos = 1;

        try {
            IMqttToken subToken = client.subscribe(topic, qos);
            subToken.setActionCallback(new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    // The message was published

                    /*MemoryManager memoryManager = new MemoryManager(context);
                    memoryManager.writeInMemory("clientId",clientId);*/

                    Intent intent = new Intent(context,MyService.class);
                    context.startService(intent);

                    Toast.makeText(context, "Subscribed", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onFailure(IMqttToken asyncActionToken,
                                      Throwable exception) {
                    // The subscription could not be performed, maybe the user was not
                    // authorized to subscribe on the specified topic e.g. using wildcards
                    Toast.makeText(context, "Subscribing failure", Toast.LENGTH_SHORT).show();

                }
            });
        } catch (MqttException e) {
            e.printStackTrace();
        }

    }

    public static void unsubscribeFromTopic(final String topic){

        try {
            IMqttToken unsubToken = client.unsubscribe(topic);
            unsubToken.setActionCallback(new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    Toast.makeText(context, "Unsubscribed from topic "+topic, Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onFailure(IMqttToken asyncActionToken,
                                      Throwable exception) {
                    // some error occurred, this is very unlikely as even if the client
                    // did not had a subscription to the topic the unsubscribe action
                    // will be successfully
                }
            });
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }




}
