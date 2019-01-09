package rocketmen.myapplication;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.widget.ImageView;

import java.io.InputStream;

/**
 * Created by luigi on 23/12/2018.
 */

public class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {

    ImageView bmImage;
    String url;

    public DownloadImageTask(ImageView bmImage, String url) {
        this.bmImage = bmImage;
        this.url = url;
    }

    protected Bitmap doInBackground(String... urls) {

        Bitmap icon = new Utils().loadImageFromUrl(url);
        return icon;
    }

    protected void onPostExecute(Bitmap result) {
        bmImage.setImageBitmap(result);
    }

}
