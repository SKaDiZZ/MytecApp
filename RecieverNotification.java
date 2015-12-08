package com.murtic.adis.techyz;

import android.app.Application;

import com.parse.Parse;

/**
 * Created by adis.murtic on 03/08/2015.
 */
public class  RecieverNotification extends Application {




    @Override
    public void onCreate() {
        super.onCreate();

        Parse.enableLocalDatastore(this);
        Parse.initialize(this, "mFrgSZ6IOUiwKY0IIi9s1YnWNLyZ9gTCjkvcEjMs", "dvqQCBPA8Itq69bqfp2VyW98gQZaJMwwe5mpKY2O");







    }
}
