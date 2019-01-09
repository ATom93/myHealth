package rocketmen.myapplication;

import android.app.IntentService;
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
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class UserHandler {

    Context context;
    JSONObject TIPS;
    String result;

    public UserHandler(Context ctx){
        context=ctx;
    }

    public UserHandler(){}

    public class UserModel extends IntentService{

        String result;

        public UserModel(String name) {
            super(name);
        }

        @Override
        protected void onHandleIntent(Intent intent) {
            final String username = new MemoryManager(this).readInMemory2("username");
            final String goal = new MemoryManager(this).readInMemory2("goal");
            final String tips = new MemoryManager(this).readInMemory2("JSONTips");
            Thread thread = new Thread(new Runnable(){
                @Override
                public void run() {
                    URL url = null;
                    try {
                        url = new URL("http://adaptapppoliba.altervista.org/progetto/retrive_6model.php");
                        HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                        httpURLConnection.setRequestMethod("POST");
                        httpURLConnection.setDoOutput(true);
                        OutputStream outputStream = httpURLConnection.getOutputStream();
                        BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream));
                        String data = URLEncoder.encode("username","UTF-8") + "=" + username;
                        bufferedWriter.write(data);
                        bufferedWriter.flush();
                        bufferedWriter.close();
                        InputStream inputStream = httpURLConnection.getInputStream();
                        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                        String line="";
                        while((line=bufferedReader.readLine())!=null){
                            result+=line;
                        }
                        if(!result.equals("0 results")){
                            JSONObject models=new JSONObject(result);

                            if(models.length()>=6){
                                TIPS = new JSONObject(tips);
                                String dailyString = new MemoryManager(context).readInMemory2("JSONDaily");
                                JSONObject daily = new JSONObject(dailyString);
                                JSONObject[] jsonObjects = new JSONObject[models.length()];
                                JSONObject GOAL = new JSONObject(goal);
                                for(int i=0;i<jsonObjects.length;i++){
                                    jsonObjects[i] = new JSONObject(models.get("model" + i).toString());
                                }
                                int[] calories = new int[jsonObjects.length+1];
                                int[] waters = new int[jsonObjects.length+1];
                                for(int i=0;i<jsonObjects.length;i++){
                                    calories[i]=jsonObjects[i].getInt("cal");
                                    waters[i]=jsonObjects[i].getInt("water");
                                }
                                int calorie = daily.getInt("calorie");
                                double stepToCal = stepToCalories(daily);
                                double bicycleToCal = bicycleToCalories(daily);
                                int altezza = daily.getInt("altezza");
                                int peso = daily.getInt("peso");
                                double BMI = peso/(altezza/100*altezza/100);
                                double BMR = GOAL.getDouble("calorie");
                                double energyDifference = calorie-stepToCal-bicycleToCal-BMR;
                                if(energyDifference>=0 && BMI>=25.01){
                                    calories[calories.length-1]=0;
                                }else if(energyDifference>0 && BMI<18.51){
                                    calories[calories.length-1]=1;
                                }else if(energyDifference<0 && BMI>=25.01){
                                    calories[calories.length-1]=1;
                                }else if(energyDifference<=0 && BMI<18.51){
                                    calories[calories.length-1]=0;
                                }
                                double acqua = daily.getInt("acqua");
                                if(acqua>GOAL.getInt("acqua")){
                                    waters[waters.length-1]=1;
                                }else{
                                    waters[waters.length-1]=0;
                                }
                                int sumCal=0;
                                int sumWat=0;
                                for(int i=0;i<waters.length;i++){
                                    sumCal+=calories[i];
                                    sumWat+=waters[i];
                                }
                                if((sumCal/calories.length)>=0.5){
                                    TIPS.put("tip4","Nell'ultima settimana ti sei comportato correttamente");
                                }else{
                                    TIPS.put("tip4","Nell'ultima settimana hai ecceduto troppo, assumi meno calorie e fai più esercizio");
                                }
                                if((sumWat/waters.length)>=0.5){
                                    TIPS.put("tip5","Nell'ultima settimana hai bevuto in media ai tuoi bisogni");
                                }else {
                                    TIPS.put("tip5","Nell'ultima settimana hai bevuto meno rispetto ai tuoi bisogni");
                                }
                                SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
                                String dateString = format.format(Calendar.getInstance().getTime());
                                url=new URL ("http://adaptapppoliba.altervista.org/progetto/registra_log.php");
                                HttpURLConnection httpURLConnection1 = (HttpURLConnection) url.openConnection();
                                httpURLConnection1.setDoOutput(true);
                                httpURLConnection1.setRequestMethod("POST");
                                OutputStream outputStream1 = httpURLConnection1.getOutputStream();
                                BufferedWriter bufferedWriter1 = new BufferedWriter(new OutputStreamWriter(outputStream1));
                                String data1 = URLEncoder.encode("username","UTF-8") + "=" + username + "&"+
                                        URLEncoder.encode("movement","UTF-8") + "=" + "0" + "&"+
                                        URLEncoder.encode("cal","UTF-8") + "=" + calories[calories.length-1] + "&"+
                                        URLEncoder.encode("water","UTF-8") + "=" + waters[waters.length-1] + "&"+
                                        URLEncoder.encode("date","UTF-8") + "=" + dateString;
                                bufferedWriter1.write(data1);
                                bufferedWriter1.flush();
                                bufferedWriter.close();
                            }
                        }
                    } catch (MalformedURLException e) {
                        e.printStackTrace();
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    } catch (ProtocolException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
            thread.start();
        }
    }

    public void updateTipsDaily(String topic) throws JSONException {

        System.out.println("heyy");
        final MemoryManager memoryManager = new MemoryManager(context);

        final String userDaily = memoryManager.readInMemory2("JSONDaily");
        String tips = memoryManager.readInMemory2("JSONTips");
        TIPS = new JSONObject(tips);
        System.out.println("Daily letto da Bera: "+userDaily);
        System.out.println("TIPS letto da memoria (bera): "+TIPS);

        //JSON che rappresenta l'attività giornaliera
        final JSONObject Juser = new JSONObject(userDaily);

        //JSON che rappresenta il goal
        String goal = new MemoryManager(context).readInMemory2("goal");
        JSONObject GOAL = new JSONObject(goal);
        if(topic.equals("passi") || topic.equals("calorie") || topic.equals("bici")){
            System.out.println("qui ci arrivo");
            double stepToCal = stepToCalories(Juser);
            double bikeToCal = bicycleToCalories(Juser);
            double BMR = GOAL.getDouble("calorie");
            double BMI = Juser.getInt("peso")/(Juser.getInt("altezza")/100*Juser.getInt("altezza")/100);
            int calories = Juser.getInt("calorie");
            double energyDifference = calories-(stepToCal+bikeToCal+BMR);
            if(energyDifference >= 0 && BMI >=25.01){
                TIPS.put("tip0","Stai assumendo troppe calorie rispetto a quelle che bruci, fai più movimento");
            }else if (energyDifference > 0 && BMI<18.51){
                TIPS.put("tip0","Stai assumendo più calorie di quelle che bruci, continua così");
            }else if(energyDifference < 0 && BMI>=25.01){
                TIPS.put("tip0","Stai consumando più calorie di quelle che assumi, continua così");
            }else if(energyDifference <= 0 && BMI < 18.51){
                TIPS.put("tip0","Stai consumando più calorie di quelle che assumi, mangia di più");
            }
        }
        if(topic.equals("peso")){
            final JSONObject finalJsonObject = Juser;
            Thread thread = new Thread(new Runnable(){
                @Override
                public void run() {
                    URL url = null;
                    try {
                        url = new URL("http://adaptapppoliba.altervista.org/progetto/penultimo_peso.php");
                        HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                        httpURLConnection.setRequestMethod("POST");
                        httpURLConnection.setDoOutput(true);
                        OutputStream outputStream = httpURLConnection.getOutputStream();
                        BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream));
                        String data = URLEncoder.encode("username","UTF-8") + "=" + finalJsonObject.getString("id");
                        bufferedWriter.write(data);
                        bufferedWriter.flush();
                        bufferedWriter.close();
                        InputStream inputStream = httpURLConnection.getInputStream();
                        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                        String line="";
                        while((line=bufferedReader.readLine())!=null){
                            result +=line;
                        }
                        System.out.println("result   "+result);
                        if(!result.equals("0 results")){
                            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
                            String dateString = format.format(Calendar.getInstance().getTime());
                            double pesoAttuale = Juser.getInt("peso");
                            double penuiltmoPeso = Double.parseDouble(new MemoryManager(context).readInMemory2("penultimopeso"));
                            double diffPeso = pesoAttuale-penuiltmoPeso;
                            if(diffPeso>0){
                                TIPS.put("tip1","Hai preso "+ diffPeso + "Kg dal "+result+" al "+dateString);
                            }
                            if(diffPeso<0){
                                TIPS.put("tip1","Hai perso "+ diffPeso + "Kg dal "+result+" al "+dateString);
                            }
                            new MemoryManager(context).writeInMemory2("penultimopeso",Double.toString(pesoAttuale));
                        }
                        TIPS.put("tip1","");
                        result="";
                        // fa l'aggiornamento del goal dell'utente e aggiorna anche i tipos sul fabbisogno energetico
                        String updatedGoal = calculateGoal(userDaily);
                        memoryManager.writeInMemory2("goal",updatedGoal);
                        updateTipsDaily("calorie");
                    } catch (MalformedURLException e) {
                        e.printStackTrace();
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    } catch (ProtocolException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
            thread.start();
            if(Juser.getInt("peso")>GOAL.getInt("pesoFormaSup")){
                int differenzaDiPeso=Juser.getInt("peso")-GOAL.getInt("pesoFormaSup");
                TIPS.put("tip2","Devi perdere "+differenzaDiPeso+" kili per arrivare al tuo peso forma");
            }else{
                if (Juser.getInt("peso") < GOAL.getInt("pesoFormaInf")) {
                    int differenzaDiPeso = GOAL.getInt("pesoFormaSup") - Juser.getInt("peso");
                    TIPS.put("tip2", "Devi ingrassare di " + differenzaDiPeso + " kili per arrivare al tuo peso forma");
                }
            }
            if(Juser.getInt("peso")<=GOAL.getInt("pesoFormaSup") && Juser.getInt("peso")>=GOAL.getInt("pesoFormaInf")){
                TIPS.put("tip2","Sei nel tuo peso forma");
            }

        }
        if(topic.equals("acqua")){
            if(Juser.getInt("acqua")>GOAL.getInt("acqua")){
                TIPS.put("tip3","Hai assunto la quantità d'acqua giornaliera");
            }else{
                TIPS.put("tip3","Non hai assunto abbastanza acqua per oggi");
            }
        }

        memoryManager.writeInMemory2("JSONTips",TIPS.toString());
        System.out.println("I tips sono: "+TIPS.toString());
    }

    //calcola il goal state
    public String calculateGoal(String daily) throws JSONException {

        JSONObject goal = new JSONObject();

        JSONObject jDaily = new JSONObject(daily);

        double height = jDaily.getDouble("altezza");
        double weight = jDaily.getDouble("peso");
        String sex = jDaily.getString("sesso");
        int age = jDaily.getInt("age");

        double pesoFormaSup = height*height*25/10000;
        double pesoFormaInf = height*height*18.51/10000;
        int s;
        double water;
        if(sex.equalsIgnoreCase("M")){
            s=5;
            water = 2.9;
        }else{
            s=-161;
            water = 2.2;
        }
        double BMR = (10*weight)+(6.25*height)+(5*age)+s;
        goal.put("calorie",BMR);
        goal.put("acqua",water);
        goal.put("pesoFormaSup",pesoFormaSup);
        goal.put("pesoFormaInf",pesoFormaInf);

        System.out.println("The generated model is: "+goal.toString());

        return goal.toString();
    }

    //converte il numero di passi in calorie usando il MET e avendo come velocità media 5.5 Km/h
    public double stepToCalories(JSONObject daily) throws JSONException {
        double stepLength;
        String sex = daily.getString("sesso");
        int step = daily.getInt("passi");
        int height = daily.getInt("altezza");
        int weight = daily.getInt("peso");
        if(sex.equalsIgnoreCase("m")){
            stepLength=0.415*height;
        }else{
            stepLength=0.413*height;
        }
        double stepToKm=step*stepLength/100000;
        double time = stepToKm/5.5;
        double calories = 3.6*weight*time;
        return calories;
    }

    //converte il numero di chilomentri in bici in calorie usando il MET e avendo come velocità media 16 Km/h
    public double bicycleToCalories(JSONObject daily) throws JSONException {
        double bicycle = daily.getDouble("bici");
        int weight = daily.getInt("peso");
        double time = bicycle/16;
        double calories = 4*weight*time;
        return calories;
    }
}
