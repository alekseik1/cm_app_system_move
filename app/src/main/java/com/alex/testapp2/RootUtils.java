package com.alex.testapp2;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.os.Build;
import android.support.v7.app.AlertDialog;
import android.util.Log;

import com.stericson.RootShell.execution.Command;
import com.stericson.RootShell.execution.Shell;
import com.stericson.RootTools.RootTools;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * Created by aleksei on 21.04.16.
 */
public class RootUtils {

    public static void moveToSystem(ApplicationInfo app, Context context) {
        StringBuilder sb = new StringBuilder();
        String appDir = null;
        String appOldDir = null;
        appOldDir = sb.append("/data" + app.sourceDir.substring(app.sourceDir.indexOf("/", 1))).substring(0, sb.indexOf("base.apk"));
        sb.delete(0,sb.length());
        if(Integer.valueOf(Build.VERSION.SDK) >= 21) {
            sb.append("/system" + app.sourceDir.substring(app.sourceDir.indexOf("/", 1)));
            sb.replace(sb.indexOf("/app"), sb.indexOf("/app", 0) + "/app".length(), "/priv-app");
            appDir = sb.substring(0, sb.indexOf("base.apk"));
        } else {
            sb.append("/system" + app.sourceDir.substring(app.sourceDir.indexOf("/", 1)));
            appDir = sb.substring(0, sb.indexOf("base.apk"));
        }
        Log.d("rootUtils", appDir);

        Shell shell = null;
        try {
            shell = RootTools.getShell(true);
            shell.add(new Command(0, "su cp -r " + appOldDir+ " " + appDir));
        } catch(IOException e) {

        } catch(TimeoutException | com.stericson.RootShell.exceptions.RootDeniedException e) {
            AlertDialog.Builder ab = new AlertDialog.Builder(context);
            ab.setTitle(R.string.no_root);
            ab.setMessage(R.string.no_root_description);
            ab.setPositiveButton(R.string.ok, null);
            ab.create().show();
        }
    }

    public static void moveToUser(ApplicationInfo app, Context context) {
        String appOldDir = app.sourceDir.substring(0, app.sourceDir.indexOf("base.apk", 0));
        StringBuilder sb = new StringBuilder();
        if(appOldDir.indexOf("/system/priv-app") != -1) {   // Проверяем, лежит ли в priv-app
            sb.append("/data" + appOldDir.substring("/system/priv-app".length()));
        } else {        // Значит, лежжит в /system/app
            sb.append("/data" + appOldDir.substring("/system/app".length()));
        }
        String appDir = sb.toString();
        Log.d("rootUtils", sb.toString());
        Shell shell = null;
        try {
            shell = RootTools.getShell(true);
        } catch(IOException e) {

        } catch(TimeoutException | com.stericson.RootShell.exceptions.RootDeniedException e) {
            AlertDialog.Builder ab = new AlertDialog.Builder(context);
            ab.setTitle(R.string.no_root);
            ab.setMessage(R.string.no_root_description);
            ab.setPositiveButton(R.string.ok, null);
            ab.create().show();
        }
    }
}
