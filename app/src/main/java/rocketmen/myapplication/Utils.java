package rocketmen.myapplication;



import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.widget.ImageView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;

/**
 * Created by luigi on 02/12/2018.
 */

public class Utils {

    public String buildStringForCreateUsers(String[] strings){

        String data = "";

        try{

            data = URLEncoder.encode("name", "UTF-8")
                    + "=" + URLEncoder.encode(strings[0], "UTF-8");

            data += "&" + URLEncoder.encode("surname", "UTF-8") + "="
                    + URLEncoder.encode(strings[1], "UTF-8");

            data += "&" + URLEncoder.encode("sex", "UTF-8") + "="
                    + URLEncoder.encode(strings[2], "UTF-8");

            data += "&" + URLEncoder.encode("birthdate", "UTF-8") + "="
                    + URLEncoder.encode(strings[3], "UTF-8");

            data += "&" + URLEncoder.encode("username", "UTF-8") + "="
                    + URLEncoder.encode(strings[4], "UTF-8");

            data += "&" + URLEncoder.encode("password", "UTF-8") + "="
                    + URLEncoder.encode(strings[5], "UTF-8");

        }catch (Exception e){
            e.printStackTrace();
        }

        return data;

    }

    public String buildStringForLogin(String[] strings){

        String data = "";

        try{

            data = URLEncoder.encode("username", "UTF-8")
                    + "=" + URLEncoder.encode(strings[0], "UTF-8");

            data += "&" + URLEncoder.encode("password", "UTF-8") + "="
                    + URLEncoder.encode(strings[1], "UTF-8");

        }catch (Exception e){
            e.printStackTrace();
        }

        return data;

    }

    public String buildStringForGetUser(String[] strings){

        String data = "";

        try{
            data = URLEncoder.encode("username", "UTF-8")
                    + "=" + URLEncoder.encode(strings[0], "UTF-8");

        }catch (Exception e){
            e.printStackTrace();
        }

        return data;

    }

    public String buildStringForAvailableServices(String[] strings){

        String data = "";

        try{
            data = URLEncoder.encode("ID", "UTF-8")
                    + "=" + URLEncoder.encode(strings[0], "UTF-8");

        }catch (Exception e){
            e.printStackTrace();
        }

        return data;

    }

    public String buildStringForSendSelectedService(String[] strings){

        String data = "";

        try{
            data = URLEncoder.encode("ID_utente", "UTF-8")
                    + "=" + URLEncoder.encode(strings[0], "UTF-8");

            data += "&" + URLEncoder.encode("service", "UTF-8") + "="
                    + URLEncoder.encode(strings[1], "UTF-8");

        }catch (Exception e){
            e.printStackTrace();
        }

        return data;
    }

    public String buildStringForIdUserService(String[] strings){

        String data = "";

        try{
            data = URLEncoder.encode("ID_User", "UTF-8")
                    + "=" + URLEncoder.encode(strings[0], "UTF-8");

            data += "&" + URLEncoder.encode("ID_Service", "UTF-8") + "="
                    + URLEncoder.encode(strings[1], "UTF-8");

        }catch (Exception e){
            e.printStackTrace();
        }

        return data;

    }

    public String buildAJsonString(String[] label, String[] value){

        String jsonString = "{";

        for(int i=0;i<label.length;i++){
            jsonString += "'"+label[i] +"'" + ":'" + value[i] +"',";
        }

        int length = jsonString.length();
        jsonString = jsonString.substring(0,length-1);
        jsonString += "}";
        return jsonString;

    }

    public String buildStringForGraphRequest(String[] strings){

        String data = "";

        try{

            data = URLEncoder.encode("username", "UTF-8")
                    + "=" + URLEncoder.encode(strings[0], "UTF-8");

            data += "&" + URLEncoder.encode("service", "UTF-8") + "="
                    + URLEncoder.encode(strings[1], "UTF-8");

        }catch (Exception e){
            e.printStackTrace();
        }

        return data;

    }

    public ArrayList<String> JsonStringBuilder(String jsonToParse, String label, String stringToDelete) throws JSONException {

        String aJsonString;
        ArrayList<String> array = new ArrayList<>();
        int num = stringToDelete.length();

        jsonToParse = jsonToParse.substring(num);

        //Retrive Name of service
        JSONObject jObject = new JSONObject(jsonToParse);
        aJsonString = jObject.getString(label);
        jObject = new JSONObject(aJsonString);

        for (int i = 0; i < jObject.length(); i++) {
            array.add(jObject.getString(label + i));
        }

        return array;
    }

    public String[] jsonDailyParser(String daily) throws JSONException {

        String[] dailyArray = new String[9];

        JSONObject jObject = new JSONObject(daily);

        dailyArray[0] = jObject.getString("id");
        dailyArray[1] = jObject.getString("age");
        dailyArray[2] = jObject.getString("altezza");
        dailyArray[3] = jObject.getString("sesso");
        dailyArray[4] = jObject.getString("peso");
        dailyArray[5] = jObject.getString("calorie");
        dailyArray[6] = jObject.getString("passi");
        dailyArray[7] = jObject.getString("acqua");
        dailyArray[8] = jObject.getString("bici");

        return  dailyArray;

    }

    public String[] jsonTipsParser(String tips) throws JSONException {

        String[] dailyArray = new String[6];

        JSONObject jObject = new JSONObject(tips);

        for(int i=0;i<6;i++){
            dailyArray[i] = jObject.getString("tip"+i);
        }


        return  dailyArray;
    }

    public Bitmap loadImageFromUrl(String imageUrl){
        Bitmap bitmap = null;

        try {
            bitmap = BitmapFactory.decodeStream((InputStream)new URL(imageUrl).getContent());
            return bitmap;
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    public static void deleteCache(Context context) {
        try {
            File dir = context.getCacheDir();
            deleteDir(dir);
        } catch (Exception e) {}

        String JSONDaily = "{'id':'luiz','age':'25','altezza':'170','peso':'0','calorie':'0','passi':'0','acqua':'0','bici':'0'}";
        String JSONTips = "{'tip0':'Nessun tip','tip1':'Nessun tip','tip2':'Nessun tip','tip3':'Nessun tip','tip4':'Nessun tip','tip5':'Nessun tip'}";
        String goal = "{'calorie':4000,'acqua':3,'pesoFormaSup':70,'pesoFormaInf':60}";

        MemoryManager memoryManager = new MemoryManager(context);

        memoryManager.writeInMemory2("goal",goal);
        memoryManager.writeInMemory2("JSONDaily",JSONDaily);
        memoryManager.writeInMemory2("JSONTips",JSONTips);
        memoryManager.writeInMemory2("penultimopeso","0");
        memoryManager.writeInMemory2("log","false");

    }
    public static boolean deleteDir(File dir) {
        if (dir != null && dir.isDirectory()) {
            String[] children = dir.list();
            for (int i = 0; i < children.length; i++) {
                boolean success = deleteDir(new File(dir, children[i]));
                if (!success) {
                    return false;
                }
            }
            return dir.delete();
        } else if(dir!= null && dir.isFile()) {
            return dir.delete();
        } else {
            return false;
        }
    }


}
