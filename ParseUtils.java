package com.murtic.adis.techyz;

import android.app.Activity;
import android.content.Context;
import android.text.TextUtils;
import android.widget.Toast;

import com.parse.Parse;
import com.parse.ParseInstallation;

/**
 * Created by adis.murtic on 03/08/2015.
 */
public class ParseUtils {


    private static String TAG = ParseUtils.class.getSimpleName();

    public static void verifyParseConfiguration(Context context) {
        if (TextUtils.isEmpty(MainActivity.AppConfig.PARSE_APPLICATION_ID) || TextUtils.isEmpty(MainActivity.AppConfig.PARSE_CLIENT_KEY)) {
            Toast.makeText(context, "Please configure your Parse Application ID and Client Key in AppConfig.java", Toast.LENGTH_LONG).show();
            ((Activity) context).finish();
        }
    }

    public static void registerParse(Context context) {
        // initializing parse library
        Parse.initialize(context, MainActivity.AppConfig.PARSE_APPLICATION_ID, MainActivity.AppConfig.PARSE_CLIENT_KEY);
        ParseInstallation.getCurrentInstallation().saveInBackground();





    }

}
