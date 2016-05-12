package com.alex.testapp2;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Build;
import android.support.v7.app.AlertDialog;
import android.util.Log;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import eu.chainfire.libsuperuser.Shell;


public class RootUtils {

    public static final String LIST_FILE_NAME = "/system/addon.d/cm-saved-files.txt";
    private static final String LOG_TAG = "rootUtils";

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
            cmd.execute("cp -r " + appOldDir + " " + appDir, "rm -rf "+appOldDir);
            if(!cmd.get().isEmpty()) Log.d(LOG_TAG, "Error moving to /system");
            makeScript(getFiles(appDir));
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    public static List<String> getFiles(String path) {
        ExecCommand cmd1 = new ExecCommand();
        List<String> result = new ArrayList<>();
        try {
            result = cmd1.execute("busybox find " + path + " -type f ").get();
        } catch(Exception e) {
            e.printStackTrace();
        }
        Log.d(LOG_TAG, path);
        return result;
    }

    private static boolean isPathAlreadyInBackupListFile(String path) throws IllegalStateException {
        ExecCommand cmd = new ExecCommand();
        try {
            List<String> s = cmd.execute("if busybox grep -q '" + path + "' " + LIST_FILE_NAME + "; then echo 0; else echo 1; fi").get();
            return s.get(0).equals("0");
        } catch(Exception e) {
            e.printStackTrace();
        }
        throw new IllegalStateException();
    }

    private static String makeScript(List<String> paths) {
        ExecCommand cmd = new ExecCommand();
        StringBuilder sb1 = new StringBuilder();
        boolean oldAndroid = true;
        if(Build.VERSION.SDK_INT >= 21) oldAndroid = false;
        for(String a : paths) {
            if (!isPathAlreadyInBackupListFile(a)) {
                if (oldAndroid)
                    sb1.append(a.substring(a.indexOf("/system/priv-app") + "/system/".length() + 1)).append("\n");
                else
                    sb1.append(a.substring(a.indexOf("/system/app") + "/system/".length() + 1)).append("\n");
            }
        }
        List<String> realPaths = null;
        try {
            cmd.execute("echo '" + sb1.toString() + "' >> " + LIST_FILE_NAME).get();
            realPaths = (new ExecCommand().execute("cat " + LIST_FILE_NAME).get());
        } catch (Exception e) {
            e.printStackTrace();
        }
        sb1.delete(0, sb1.length());
        if (realPaths != null) {
            for(String a : realPaths) {
                sb1.append(a).append("\n");
            }
        }
        try {
            List<String> res = (new ExecCommand().execute("echo '#!/sbin/sh\n" +
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
                    "esac' > /system/addon.d/60-"+ "cmSave" +".sh").get());
            //List<String> res = cmd.execute("sed '1,/EOF/s//" + sb1.toString() + "\nEOF/' /system/addon.d/99-save.sh").get();
            Log.d(LOG_TAG, "sed result: " + res);
        } catch(Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static double getAppSize(ApplicationInfo app) {
        //return getTotalFilesSize(getFiles(app.sourceDir.
        //       substring(0, app.sourceDir.lastIndexOf("/", app.sourceDir.length()))));
        String folder = app.sourceDir.substring(0, app.sourceDir.lastIndexOf("/", app.sourceDir.length()));
        double a = 0;
        for(File f : listFilesForFolder(folder)) {
            a+=f.length();
        }
        return ((double) Math.round(a/1024/1024*10))/10;
    }

    public static List<File> listFilesForFolder(String path) {
        return listFilesForFolder(new File(path));
    }

    // Here some magic is used. Be careful not to change anything!
    public static List<File> listFilesForFolder(final File folder) {
        List<File> res = new ArrayList<>();
        if(folder.listFiles() != null) {
            for (final File fileEntry : folder.listFiles()) {
                if (fileEntry.isDirectory()) {
                    List<File> q = listFilesForFolder(fileEntry);
                    for (int i = 0; i < q.size(); i++) {
                        File w = q.get(i);
                        res.add(w);
                    }
                } else {
                    res.add(fileEntry);
                }
            }
        }
        return res;
    }

    public static void moveToUser(ApplicationInfo app, Context context) {
        String appOldDir = app.sourceDir.substring(0, app.sourceDir.indexOf("base.apk", 0));
        StringBuilder sb = new StringBuilder();
        if (appOldDir.contains("/system/priv-app"))   // Проверяем, лежит ли в priv-app
            sb.append("/data/app").append(appOldDir.substring("/system/priv-app".length()));
         else        // Значит, лежжит в /system/app
            sb.append("/data/app").append(appOldDir.substring("/system/app".length()));
        String appDir = sb.toString();
        Log.d(LOG_TAG, sb.toString());
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
            if(!cmd.get().isEmpty()) Log.d(LOG_TAG, "Error moving to user");
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    public static void makeSystemRWState(boolean makeRW) {
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

    public static boolean isBusyboxAvailable() {
        try {
            List<String> busybox = (new RootUtils.ExecCommand()).execute("busybox > /dev/null && echo $?").get();
            if (busybox.get(0).equals("0")) return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public static String getAppName(ApplicationInfo info, PackageManager pm) {
        return info.loadLabel(pm).toString();
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
