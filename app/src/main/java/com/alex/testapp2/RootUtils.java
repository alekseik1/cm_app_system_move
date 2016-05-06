package com.alex.testapp2;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import java.util.ArrayList;
import java.util.List;
import eu.chainfire.libsuperuser.Shell;

/**
 * Created by aleksei on 21.04.16.
 */
public class RootUtils {

    public static void moveToSystem(ApplicationInfo app, Context context) {
        StringBuilder sb = new StringBuilder();
        String appDir;
        String appOldDir;
        appOldDir = sb.append("/data").append(app.sourceDir.substring(app.sourceDir.indexOf("/", 1))).substring(0, sb.indexOf("base.apk"));
        sb.delete(0, sb.length());
        //noinspection deprecation
        if (Integer.valueOf(Build.VERSION.SDK) >= 21) {
            sb.append("/system").append(app.sourceDir.substring(app.sourceDir.indexOf("/", 1)));
            sb.replace(sb.indexOf("/app"), sb.indexOf("/app", 0) + "/app".length(), "/priv-app");
            appDir = sb.substring(0, sb.indexOf("base.apk"));
        } else {
            sb.append("/system").append(app.sourceDir.substring(app.sourceDir.indexOf("/", 1)));
            appDir = sb.substring(0, sb.indexOf("base.apk"));
        }
        Log.d("rootUtils", appDir);

        if(!Shell.SU.available()) {
            AlertDialog.Builder ab = new AlertDialog.Builder(context);
            ab.setTitle(R.string.no_root);
            ab.setMessage(R.string.no_root_description);
            ab.setPositiveButton(R.string.ok, null);
            ab.create().show();
        }

        ExecCommand cmd = new ExecCommand();
        try {
            makeSystemRWState(true);
            cmd.execute("cp -r " + appOldDir
                    + " " + appDir, "touch /system/addon.d/99-cmmoveapps.sh", "echo "
                    + genScript(getFiles(appDir)) + " > /system/addon.d/99-cmmoveapps.sh",
                    "rm -rf " + appOldDir);
            if(!cmd.get().isEmpty()) Log.d("rootUtils", "Error moving to /system");
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    private static List<String> getFiles(String path) {
        ExecCommand cmd1 = new ExecCommand();
        List<String> result = new ArrayList<>();
        try {
            result = cmd1.execute("busybox find " + path + " -type f " +
                    "> " + Environment.getExternalStorageDirectory() + "/.cmAppsCache").get();
        } catch(Exception e) {
            e.printStackTrace();
        }
        Log.d("rootUtils", path);
        return result;
    }

    private static String genScript(List<String> paths) {
        return null;
    }

    public static void moveToUser(ApplicationInfo app, Context context) {
        String appOldDir = app.sourceDir.substring(0, app.sourceDir.indexOf("base.apk", 0));
        StringBuilder sb = new StringBuilder();
        if (appOldDir.contains("/system/priv-app"))   // Проверяем, лежит ли в priv-app
            sb.append("/data/app").append(appOldDir.substring("/system/priv-app".length()));
         else        // Значит, лежжит в /system/app
            sb.append("/data/app").append(appOldDir.substring("/system/app".length()));
        String appDir = sb.toString();
        Log.d("rootUtils", sb.toString());
        if(!Shell.SU.available()) {
            AlertDialog.Builder ab = new AlertDialog.Builder(context);
            ab.setTitle(R.string.no_root);
            ab.setMessage(R.string.no_root_description);
            ab.setPositiveButton(R.string.ok, null);
            ab.create().show();
        }
        try {
            makeSystemRWState(true);
            ExecCommand cmd = new ExecCommand();
            cmd.execute("cp -r " + appOldDir + " " + appDir, "rm -rf " + appOldDir);
            if(!cmd.get().isEmpty()) Log.d("rootUtils", "Error moving to user");
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    protected static void makeSystemRWState(boolean makeRW) {
        try {
            ExecCommand cmd = new ExecCommand();
            if(makeRW) {
                cmd.execute("busybox mount -o remount,rw /system");
            } else {
                cmd.execute("busybox mount -o remount,ro /system");
            }
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    public static class ExecCommand extends AsyncTask<String, Void, List<String>> {

        List<String> output = new ArrayList<>();

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected List<String> doInBackground(String... params) {
            List<String> list = new ArrayList<>();
            for (String param : params) list.add(param);
            output = Shell.SU.run(list);
            return output;
        }

        @Override
        protected void onPostExecute(List<String> aBoolean) {
            super.onPostExecute(aBoolean);
        }
    }

}
