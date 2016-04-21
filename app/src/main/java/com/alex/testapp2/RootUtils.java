package com.alex.testapp2;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.util.Log;

import com.stericson.RootTools.RootTools;

/**
 * Created by aleksei on 21.04.16.
 */
public class RootUtils {

    public static void moveToSystem(ApplicationInfo app, Context context) {
        Log.d("rootUtils", app.dataDir);

    }

    public static void moveToUser(ApplicationInfo app, Context context) {

    }
}
