package rocketmen.myapplication;

import android.app.Activity;
import android.app.DialogFragment;
import android.content.Intent;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

public class DashBoard extends AppCompatActivity implements AdapterView.OnItemClickListener {

    String[] serviceName;
    String[] serviceValue;
    String[] tipsDescription;
    GraphView graph;
    Broker broker;

    Activity context;

    User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dash_board);

        context = this;

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        user = (User) bundle.getSerializable("user");
        System.out.println(user.getUsername());

        MemoryManager memoryManager = new MemoryManager(this);


        try {
            serviceValue = new Utils().jsonDailyParser(memoryManager.readInMemory("JSONDaily"));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        serviceName = new String[]{"peso","calorie","passi","acqua","bici"};
        serviceValue= new String[] {serviceValue[4],serviceValue[5],serviceValue[6],serviceValue[7],serviceValue[8]};

        ListView listView = (ListView)findViewById(R.id.lista1);
        CustomAdapter customAdapter = new CustomAdapter(serviceName,serviceValue,this,R.layout.list_performances);
        listView.setAdapter(customAdapter);
        listView.setOnItemClickListener(this);
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> arg0, View arg1, int pos, long id) {

                String address ="http://adaptapppoliba.altervista.org/progetto/elimina_associazione_utente_servizio.php";
                String payload = new Utils().buildStringForSendSelectedService(new String[]{user.getId(),serviceName[pos]});
                HttpRequester requester = new HttpRequester(address,payload,context);
                requester.execute("");

                TextView textView = (TextView)arg1.findViewById(R.id.text2);
                textView.setText("0");

                MemoryManager memoryManager = new MemoryManager(context);
                try {
                    serviceValue = new Utils().jsonDailyParser(memoryManager.readInMemory("JSONDaily"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                try {
                    JSONObject jTips = new JSONObject(memoryManager.readInMemory("JSONTips"));

                    //Modifica json
                    switch (serviceName[pos]){
                        case "peso":
                            serviceValue[4]="0";
                            jTips.put("tip1","No tip");
                            jTips.put("tip2","No tip");
                            break;
                        case "calorie":
                            serviceValue[5]="0";
                            jTips.put("tip0","No tip");
                            jTips.put("tip4","No tip");
                            break;
                        case "passi":
                            serviceValue[6]="0";
                            jTips.put("tip0","No tip");
                            jTips.put("tip4","No tip");
                            break;
                        case "acqua":
                            serviceValue[7]="0";
                            jTips.put("tip3","No tip");
                            jTips.put("tip5","No tip");
                            break;
                        case "bici":
                            serviceValue[8]="0";
                            jTips.put("tip0","No tip");
                            jTips.put("tip4","No tip");
                            break;
                    }

                    memoryManager.writeInMemory("JSONTips",jTips.toString());

                } catch (JSONException e) {
                    e.printStackTrace();
                }


                broker.unsubscribeFromTopic(serviceName[pos]+"/"+user.getUsername());

                String JSONDaily = new Utils().buildAJsonString(new String[]{"id","age","altezza","sesso","peso","calorie","passi","acqua","bici"},serviceValue);
                memoryManager.writeInMemory("JSONDaily",JSONDaily);

                Toast.makeText(DashBoard.this, serviceName[pos]+" disassociato", Toast.LENGTH_SHORT).show();
                return true;
            }

        });

        try {
            tipsDescription = new Utils().jsonTipsParser(memoryManager.readInMemory("JSONTips"));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        String[] tipsName = new String[tipsDescription.length];

        for(int i=0;i<tipsDescription.length;i++){
            tipsName[i] = "tip "+ Integer.toString(i);
        }

        ListView listView2 = (ListView)findViewById(R.id.lista2);
        CustomAdapter customAdapter2 = new CustomAdapter(tipsName,tipsDescription,this,R.layout.list_tips);
        listView2.setAdapter(customAdapter2);
        listView2.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
                                           int pos, long id) {

                Toast.makeText(DashBoard.this, tipsDescription[pos], Toast.LENGTH_SHORT).show();

                return true;
            }
        });

    }

    public void openServices(View v){
        //Passa l'utente all' activity AvailableServices
        Intent intent = new Intent(this,AvailableServices.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable("user",user);
        intent.putExtras(bundle);
        this.startActivity(intent);
        this.finish();
    }

    public void onItemClick(AdapterView<?> l, View v, int position, long id) {

        Toast.makeText(DashBoard.this, "servizio: "+serviceName[position], Toast.LENGTH_SHORT).show();

        LineGraphSeries<DataPoint> series = null;
        try {

            DataPoint[] points = retriveDataForGraph(serviceName[position]);
            series = new LineGraphSeries<>(points);

        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }
        graph = (GraphView) findViewById(R.id.graph);
        graph.removeAllSeries();
        graph.addSeries(series);
    }

    public DataPoint[] retriveDataForGraph(String serviceName) throws ExecutionException, InterruptedException {

        DataPoint[] points = new DataPoint[2];
        ArrayList<String> values;
        String[] valuesArray;
        double[] doubleValues;

        String payload = new Utils().buildStringForGraphRequest(new String[]{user.getUsername(),serviceName});
        String address = "http://adaptapppoliba.altervista.org/progetto/grafico.php";
        HttpRequester requester = new HttpRequester(address,payload,context);
        requester.execute("");
        String result = requester.get();

        System.out.println(result);

        try {
            values = new Utils().JsonStringBuilder(result,"Value","valori");
            valuesArray = values.toArray(new String[values.size()]);
            doubleValues = new double[valuesArray.length];

            for(int i=0;i<valuesArray.length;i++){
                doubleValues[i] = Double.parseDouble(valuesArray[i]);
            }

            points = new DataPoint[doubleValues.length];

            for(int i=0;i<points.length;i++){
                points[i] = new DataPoint(i,doubleValues[i]);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return points;

    }

    /*

    Slide dinamiche, variopinte, con immagini
    Sommario
    conclusioni e sviluppi futuri1



     */

}
