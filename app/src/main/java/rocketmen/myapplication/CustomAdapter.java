package rocketmen.myapplication;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by luigi on 06/12/2018.
 */

public class CustomAdapter extends BaseAdapter {

    String[] text1;
    String[] text2;
    String[] pictureUrls;
    Activity activity;
    int listLayout;

    public CustomAdapter(String[] text1, String[] text2, Activity activity, int listLayout){
        this.text1 = text1;
        this.text2 = text2;
        this.activity = activity;
        this.listLayout = listLayout;
    }

    public CustomAdapter(String[] text1, String[] text2, String[] pictureUrls, String[] urls, Activity activity, int listLayout){
        this.text1 = text1;
        this.text2 = text2;
        this.activity = activity;
        this.listLayout = listLayout;
        this.pictureUrls = pictureUrls;
    }

    @Override
    public int getCount() {
        return text1.length;
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {

        if(listLayout!=R.layout.list_apps){
            view = activity.getLayoutInflater().inflate(listLayout,null);
            TextView text1 = (TextView) view.findViewById(R.id.text1);
            TextView text2 = (TextView) view.findViewById(R.id.text2);
            ImageView image = (ImageView) view.findViewById(R.id.picture1) ;

            switch (this.text1[i]){
                case "peso":
                    image.setBackgroundResource(R.drawable.scale);
                    break;
                case "calorie":
                    image.setBackgroundResource(R.drawable.calories);
                    break;
                case "passi":
                    image.setBackgroundResource(R.drawable.running);
                    break;
                case "acqua":
                    image.setBackgroundResource(R.drawable.water);
                    break;
                case "bici":
                    image.setBackgroundResource(R.drawable.bicycle);
                    break;
            }

            text1.setText(this.text1[i]);
            text2.setText(this.text2[i]);
        }
        else {
            view = activity.getLayoutInflater().inflate(listLayout,null);
            TextView text1 = (TextView) view.findViewById(R.id.text1);
            TextView text2 = (TextView) view.findViewById(R.id.text2);
            final ImageView image = (ImageView) view.findViewById(R.id.picture1) ;

            DownloadImageTask downloadImageTask = new DownloadImageTask(image, pictureUrls[i]);
            downloadImageTask.execute("");

            text1.setText(this.text1[i]);
            text2.setText(this.text2[i]);
        }

        return view;
    }

}
