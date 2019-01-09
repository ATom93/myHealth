package rocketmen.myapplication;


import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;


public class MyService extends Service {

    Intent intent;
    Broker broker;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        this.intent = intent;
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onCreate() {
        broker = new Broker(this);
        MemoryManager memoryManager = new MemoryManager(this);
        String clientId = memoryManager.readInMemory2("clientId");
        broker.createClient(clientId);
        broker.connectToBroker();
        broker.listenToUpdates();

        /* final Handler handler = new Handler();
         final int delay = 10000;
        handler.postDelayed(new Runnable(){
            public void run(){
                broker.connectToBroker();
                broker.listenToUpdates();
                System.out.println("Service executed!");
                handler.postDelayed(this, delay);
            }
        }, delay);*/
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }



}