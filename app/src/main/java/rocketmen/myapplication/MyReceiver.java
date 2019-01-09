package rocketmen.myapplication;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.net.URLEncoder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class MyReceiver extends BroadcastReceiver {

    String result;

    @Override
    public void onReceive(final Context context, Intent intent) {
        if(intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED)){
            MemoryManager memoryManager = new MemoryManager(context);
            String daily = memoryManager.readInMemory2("JSONDaily");
            JSONObject jsonObject=null;
            try {
                jsonObject = new JSONObject(daily);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            String username=null;
            try {
                username = jsonObject.getString("id");
            } catch (JSONException e) {
                e.printStackTrace();
            }
            final String finalUsername = username;
            Thread thread = new Thread(new Runnable(){
                @Override
                public void run() {
                    URL url = null;
                    try {
                        url = new URL("http://adaptapppoliba.altervista.org/progetto/retrive_data_ultimo_modello.php");
                        HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                        httpURLConnection.setRequestMethod("POST");
                        httpURLConnection.setDoOutput(true);
                        OutputStream outputStream = httpURLConnection.getOutputStream();
                        BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream));
                        String data = URLEncoder.encode("name","UTF-8") + "=" + finalUsername;
                        bufferedWriter.write(data);
                        bufferedWriter.flush();
                        bufferedWriter.close();
                        InputStream inputStream = httpURLConnection.getInputStream();
                        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                        String line="";
                        while((line=bufferedReader.readLine())!=null){
                            result+=line;
                        }
                        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
                        String dateString = format.format(Calendar.getInstance().getTime());
                        if(!result.equals(dateString)){
                            UserHandler user = new UserHandler(context);
                            Intent intent1 = new Intent(context,UserHandler.UserModel.class);
                            context.startService(intent1);
                        }
                    } catch (MalformedURLException e) {
                        e.printStackTrace();
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    } catch (ProtocolException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });
            thread.start();
            Calendar cur_cal = Calendar.getInstance();
            cur_cal.set(Calendar.HOUR_OF_DAY,0);
            cur_cal.set(Calendar.MINUTE,0);
            System.out.println("Current time " + cur_cal.getTime());
            Intent intent2 = new Intent(context, UserHandler.UserModel.class);
            System.out.println("intent created");
            PendingIntent pi = PendingIntent.getService(context, 0, intent2, 0);
            AlarmManager alarm_manager = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
            alarm_manager.setInexactRepeating(AlarmManager.RTC_WAKEUP,cur_cal.getTimeInMillis(),AlarmManager.INTERVAL_DAY,pi);
        }
    }
}
