package rocketmen.myapplication;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;

/**
 * Created by luigi on 02/12/2018.
 */

public class HttpRequester extends AsyncTask<String, String, String> {


    public String payload;
    public String address;
    public String text;
    public Activity context;
    public User user;



    public HttpRequester(String address, String payload,Activity context) {
        this.payload = payload;
        this.address = address;
        this.context = context;
    }

    public HttpRequester(String address, String payload,Activity context, User user) {
        this.payload = payload;
        this.address = address;
        this.context = context;
        this.user = user;
    }

    @Override
    protected String doInBackground(String... params) {

        String data="no";

        try{
            data = payload;

        }catch (Exception e)
        {
            System.out.print("mi pianto qui1");
            e.printStackTrace();

        }

        BufferedReader reader=null;
        try
        {
            URL url = new URL(address);

            URLConnection conn = url.openConnection();
            conn.setDoOutput(true);
            OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
            wr.write(data);
            wr.flush();

            System.out.print("Post effettuata");

            reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            StringBuilder sb = new StringBuilder();
            String line = null;


            while((line = reader.readLine()) != null)
            {
                sb.append(line + "\n");
            }

            text = sb.toString();
        }
        catch(Exception ex)
        {
            System.out.print("mi pianto qui2");
            ex.printStackTrace();
        }
        finally
        {
            try
            {
                reader.close();
            }

            catch(Exception ex) {
                ex.printStackTrace();
                System.out.print("mi pianto qui3");
            }
        }
        System.out.println(text);
        this.publishProgress(text);

        return text;
    }

    @Override
    protected void onProgressUpdate(String... values) {
        super.onProgressUpdate(values);
    }


}
