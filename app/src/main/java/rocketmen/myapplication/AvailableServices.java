package rocketmen.myapplication;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.ExecutionException;

public class AvailableServices extends AppCompatActivity implements AdapterView.OnItemClickListener {

    Broker broker;

    String[] names;
    String[] scores;
    String[] pictureUrls;
    String[] urls;
    String globalAddress = "http://adaptapppoliba.altervista.org/";
    Activity context;
    String topic;
    String categories = "{'1':'ANDROID_WEAR','2':'LIFESTYLE','3':'FOOD_AND_DRINK','4':'HEALTH_AND_FITNESS','5':'LIFESTYLE','6':'WEATHER','7':'MEDICAL','8':'MAPS_AND_NAVIGATION','9':'FOOD_AND_DRINK'}";

    User user;

    ArrayList<String> selected;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.available_services);
        selected = new ArrayList<>();
        context = this;

        retriveUser();
        retriveAndShowListOfServices();
        retriveAndShowListOfApps();
    }

    public void retriveAndShowListOfServices(){

        String address = globalAddress + "progetto/servizi_disponibili.php";
        String payload = new Utils().buildStringForAvailableServices(new String[]{user.getId()});

        HttpRequester requester = new HttpRequester(address,payload,this);
        requester.execute("");

        String result;

        try {
            result = requester.get();

            ArrayList<String> name;
            ArrayList<String> description;

            try {
                name = new Utils().JsonStringBuilder(result,"Name","servizi");
                description = new Utils().JsonStringBuilder(result,"Description","servizi");

                names = name.toArray(new String[name.size()]);
                scores = description.toArray(new String[description.size()]);

                ListView listView = (ListView)findViewById(R.id.service_list);
                CustomAdapter customAdapter = new CustomAdapter(names, scores,this,R.layout.list_performances);
                listView.setAdapter(customAdapter);
                listView.setOnItemClickListener(this);

                if(names.length==0){
                    Toast.makeText(this, "Nessun servizio da aggiungere", Toast.LENGTH_SHORT).show();
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }

        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }

    }

    public void retriveAndShowListOfApps(){

        /*
interesting CATEGORIES:
ANDROID_WEAR
AUTO_AND_VEHICLES
FOOD_AND_DRINK
HEALTH_AND_FITNESS
LIFESTYLE
MAPS_AND_NAVIGATION
MEDICAL
SPORTS
WEATHER
*/

        Random rand = new Random();

        int n = rand.nextInt(9) + 1;
        String category ="";

        try {

            JSONObject jCategory = new JSONObject(categories);
            category = jCategory.getString(Integer.toString(n));
            System.out.println("CATEGORIE: "+category);

            String address = "http://192.168.42.157:8080";
            String payload = "{\"category\":\""+category+"\"}";
            System.out.println("PAYLOAD: "+payload);

            HttpRequester requester = new HttpRequester(address,payload,this);
            requester.execute("");

            String result;

            result = requester.get();

            JSONArray jsonArray = new JSONArray(result);

            names = new String[5];
            scores = new String[5];
            pictureUrls = new String[5];
            urls = new String[5];


            for(int i=0;i<5;i++){

                JSONObject jApps = new JSONObject(jsonArray.getString(i));

                names[i] = jApps.getString("title");
                urls[i] = jApps.getString("url");
                scores[i] = jApps.getString("score");
                pictureUrls[i] = jApps.getString("icon");

            }

            ListView listView = (ListView)findViewById(R.id.app_list);
            CustomAdapter customAdapter = new CustomAdapter(names, scores, pictureUrls,urls,this,R.layout.list_apps);
            listView.setAdapter(customAdapter);
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    Uri uri = Uri.parse(urls[i]); // missing 'http://' will cause crashed
                    Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                    startActivity(intent);
                }
            });

        } catch (JSONException | InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }




    }

    public void retriveUser(){
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        user = (User) bundle.getSerializable("user");
        System.out.println("Nome utente da Available services: "+user.getId()+": "+user.getUsername());
    }

    public void onItemClick(AdapterView<?> l, View v, int position, long id) {

            Toast.makeText(this, names[position]+ " aggiunto", Toast.LENGTH_SHORT).show();

            String userId = user.getId();

            String payload = new Utils().buildStringForSendSelectedService(new String[]{userId,names[position]});
            String address = globalAddress + "progetto/crea_associazione_utente_servizio.php";

            HttpRequester requester = new HttpRequester(address,payload,this);
            requester.execute("");


            topic = names[position] +"/"+ user.getUsername();
            broker.subscribeToTopic(topic);

            String response = null;

            try {
                response = requester.get();
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }

            System.out.println(response);

            retriveAndShowListOfServices();




    }

    public void backToDashBoard(View v) throws ExecutionException, InterruptedException {

        Intent intent = new Intent(this,DashBoard.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable("user",user);
        intent.putExtras(bundle);
        this.startActivity(intent);
        this.finish();
    }

    public void logout(View v){

        new Utils().deleteCache(context);
        resetCache();
        this.finish();
    }

    public void resetCache(){
        String JSONDaily = "{'id':'luiz','age':'0','altezza':'0','sesso':'M','peso':'0','calorie':'0','passi':'0','acqua':'0','bici':'0'}";
        String JSONTips = "{'tip0':'Nessun tip','tip1':'Nessun tip','tip2':'Nessun tip','tip3':'Nessun tip','tip4':'Nessun tip','tip5':'Nessun tip'}";
        String goal = "{'calorie':4000,'acqua':3,'pesoFormaSup':70,'pesoFormaInf':60}";

        MemoryManager memoryManager = new MemoryManager(this);
        memoryManager.writeInMemory("goal",goal);
        memoryManager.writeInMemory("JSONDaily",JSONDaily);
        memoryManager.writeInMemory("JSONTips",JSONTips);
        memoryManager.writeInMemory("penultimopeso","0");
        // memoryManager.writeInMemory("log","false");
    }


    /*

    PER fare il retrive delle prime 5 app devo fare una POST a http...
    con un JSON {"category":""}
    le possibili categorie sono:
    ANDROID_WEAR
AUTO_AND_VEHICLES
FOOD_AND_DRINK
HEALTH_AND_FITNESS
LIFESTYLE
MAPS_AND_NAVIGATION
MEDICAL
SPORTS
WEATHER

La risposta è un JSON:


[
    {
        "title": "McDonald's",
        "score": 3.6,
        "icon": "https://lh3.googleusercontent.com/J5eTDcbu46o_ffTG39aXkuO2BKDtVMln570u7xn_z0MeR_UHtAW7YVUbpix1aJYe4g=w340"
    },
    {
        "title": "Just Eat - Ordina pranzo e cena a Domicilio",
        "score": 4.4,
        "icon": "https://lh3.googleusercontent.com/Sj-1d6c8k_DuG9H8DG_cRlziYBKkTStxNsNFp83l8CxtMPi_djyEoJzQGOXMn909Nw=w340"
    },
    {
        "title": "Deliveroo - Cibo a Domicilio",
        "score": 4.2,
        "icon": "https://lh3.googleusercontent.com/wOXMYWKuFhYHLt9KQCYKZXe6Xxl3XL3r4UZZO5z5DCdCXUd5YgXtxxZKG7L5D4NY7Gs=w340"
    },
    {
        "title": "TheFork – Prenotazione ristoranti e offerte",
        "score": 4.3,
        "icon": "https://lh3.googleusercontent.com/s0q2SLTN0OsDmUEKTgBAae-I4RHIo2VihuF63AWVSqeJ10BomU0cXGUI4r93vESGeg=w340"
    },
    {
        "title": "Vivino: Acquista il vino ideale",
        "score": 4.4,
        "icon": "https://lh6.ggpht.com/GKiWMdswOSY4yIpATeLnIwt-1ORvZDr-c-nO479FqyFk4AdcKqX4Lb5UlOjZALTA6v8=w340"
    }
]


     */



}
