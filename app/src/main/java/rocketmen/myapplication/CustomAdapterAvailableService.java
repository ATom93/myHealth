package rocketmen.myapplication;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created by luigi on 06/12/2018.
 */

public class CustomAdapterAvailableService extends BaseAdapter {

    String[] text1;
    String[] text2;
    Activity activity;
    int listLayout;

    public CustomAdapterAvailableService(String[] text1, String[] text2, Activity activity, int listLayout){
        this.text1 = text1;
        this.text2 = text2;
        this.activity = activity;
        this.listLayout = listLayout;
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
    public View getView(final int i, View view, ViewGroup viewGroup) {

        view = activity.getLayoutInflater().inflate(listLayout,null);
        TextView text1 = (TextView) view.findViewById(R.id.text1);
        TextView text2 = (TextView) view.findViewById(R.id.text2);


        final Button button = view.findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                button.setText("Aggiunto");

            }
        });

        text1.setText(this.text1[i]);
        text2.setText(this.text2[i]);

        return view;
    }

}
