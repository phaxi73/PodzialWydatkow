package com.example.bartek.podzialwydatkow;

import android.app.Application;

import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.OkHttpDownloader;
import com.squareup.picasso.Picasso;

/**
 * Created by Bartek on 12.11.2017.
 */

public class PodzialWydatkow extends Application {

    public void onCreate() {
        super.onCreate();

        FirebaseDatabase.getInstance().setPersistenceEnabled(true);                     //Włączanie możliwości offline Firebase

        Picasso.Builder builder = new Picasso.Builder(this);                    //Włączanie możliwości offline Picasso
        builder.downloader(new OkHttpDownloader(this, Integer.MAX_VALUE));      //
        Picasso build = builder.build();                                                //
        build.setIndicatorsEnabled(true);                                               //
        build.setLoggingEnabled(true);                                                  //
        Picasso.setSingletonInstance(build);                                            //


    }

}
