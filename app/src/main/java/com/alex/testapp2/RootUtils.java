package com.alex.testapp2;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.support.v7.app.AlertDialog;
import android.util.Log;

import com.stericson.RootShell.execution.Shell;
import com.stericson.RootTools.RootTools;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * Created by aleksei on 21.04.16.
 */
public class RootUtils {

    public static void moveToSystem(ApplicationInfo app, Context context) {
        Log.d("rootUtils", app.dataDir);

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

    public static void moveToUser(ApplicationInfo app, Context context) {
        Log.d("rootUtils", app.dataDir);
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
