package rocketmen.myapplication;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Created by luigi on 15/12/2018.
 */

public class Broadcast extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {

        if(intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED)){
            Intent intent1 = new Intent(context,MyService.class);
            context.startService(intent1);
        }
    }
}
