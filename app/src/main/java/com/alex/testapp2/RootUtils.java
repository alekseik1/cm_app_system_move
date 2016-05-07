package com.alex.testapp2;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.os.AsyncTask;
import android.os.Build;
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
        String scriptName = app.loadLabel(context.getPackageManager()).toString().toLowerCase().replace(" ", "");
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
            cmd.execute("cp -r " + appOldDir + " " + appDir, "rm -rf "+appOldDir);
            if(!cmd.get().isEmpty()) Log.d("rootUtils", "Error moving to /system");
            makeScript(getFiles(appDir), context, scriptName);
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    private static List<String> getFiles(String path) {
        ExecCommand cmd1 = new ExecCommand();
        List<String> result = new ArrayList<>();
        try {
            result = cmd1.execute("busybox find " + path + " -type f ").get();
        } catch(Exception e) {
            e.printStackTrace();
        }
        Log.d("rootUtils", path);
        return result;
    }

    private static String makeScript(List<String> paths, Context context, String scriptName) {
        ExecCommand cmd = new ExecCommand();
        StringBuilder sb1 = new StringBuilder();
        boolean oldAndroid = true;
        if(Build.VERSION.SDK_INT >= 21) oldAndroid = false;
        for(String a : paths) {
            if(oldAndroid) sb1.append(a.substring(a.indexOf("/system/priv-app")+"/system/".length()+1)).append("\n");
            else sb1.append(a.substring(a.indexOf("/system/app")+"/system/".length()+1)).append("\n");
        }
        //sb1.append("/");
        try {
            List<String> res = cmd.execute("echo '#!/sbin/sh\n" +
                    ". /tmp/backuptool.functions\n" +
                    "list_files() {\n" +
                    "cat <<EOF\n" + sb1.toString() +
                    "EOF\n" +
                    "}\n" +
                    "case \"$1\" in\n" +
                    "  backup)\n" +
                    "    list_files | while read FILE DUMMY; do\n" +
                    "      backup_file $S/\"$FILE\"\n" +
                    "    done\n" +
                    "  ;;\n" +
                    "  restore)\n" +
                    "    list_files | while read FILE REPLACEMENT; do\n" +
                    "      R=\"\"\n" +
                    "      [ -n \"$REPLACEMENT\" ] && R=\"$S/$REPLACEMENT\"\n" +
                    "      [ -f \"$C/$S/$FILE\" ] && restore_file $S/\"$FILE\" \"$R\"\n" +
                    "    done\n" +
                    "  ;;\n" +
                    "  pre-backup)\n" +
                    "    # Stub\n" +
                    "  ;;\n" +
                    "  post-backup)\n" +
                    "    # Stub\n" +
                    "  ;;\n" +
                    "  pre-restore)\n" +
                    "    # Stub\n" +
                    "  ;;\n" +
                    "  post-restore)\n" +
                    "    # Stub\n" +
                    "  ;;\n" +
                    "esac' >> /system/addon.d/99-"+ scriptName +".sh").get();
            //List<String> res = cmd.execute("sed '1,/EOF/s//" + sb1.toString() + "\nEOF/' /system/addon.d/99-save.sh").get();
            Log.d("rootUtils", "sed result: " + res);
        } catch(Exception e) {
            e.printStackTrace();
        }
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
