package rocketmen.myapplication;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by luigi on 12/12/2018.
 */

public class MemoryManager {

    Activity context;
    Context context2;
    static String MY_PREFS_NAME = "adaptapp";

    public MemoryManager(Activity context){
        this.context = context;
    }

    public MemoryManager(Context context2){
        this.context2 = context2;
    }

    public void writeInMemory(String variableName,String variable){
        SharedPreferences.Editor editor = context.getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE).edit();
        editor.putString(variableName, variable);
        editor.apply();

    }

    public String readInMemory(String variableName){
        String name="null";
        SharedPreferences prefs = context.getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE);
        String restoredText = prefs.getString(variableName, null);
        if (restoredText != null) {
            name = prefs.getString(variableName, "No name defined");//"No name defined" is the default value.
        }

        return name;
    }

    public void writeInMemory2(String variableName,String variable){
        SharedPreferences.Editor editor = context2.getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE).edit();
        editor.putString(variableName, variable);
        editor.apply();

    }

    public String readInMemory2(String variableName){
        String name="null";
        SharedPreferences prefs = context2.getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE);
        String restoredText = prefs.getString(variableName, null);
        if (restoredText != null) {
            name = prefs.getString(variableName, "No name defined");//"No name defined" is the default value.
        }

        return name;
    }

}
