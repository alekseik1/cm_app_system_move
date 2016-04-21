package com.alex.testapp2;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment {

    RecyclerView rv;
    AlertDialog ad;

    public class App {
        String appName;
        String appWhere;
        Bitmap appIcon;
        public App(String name, String path, int res) {
            this.appName = name;
            this.appWhere = path;
            this.appIcon = BitmapFactory.decodeResource(getResources(), res);
        }
    }

    public MainActivityFragment() {
    }

    public List<ApplicationInfo> apps;
    public List<String> appNames;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_main, container, false);
    }

    @Override
    public void onResume() {
        super.onResume();
        if(appNames == null) {
            appNames = new ArrayList<>();
            PackageManager pm = getContext().getPackageManager();
            apps = pm.getInstalledApplications(PackageManager.GET_META_DATA);
            for (int i = 0; i < apps.size(); i++) {
                appNames.add(apps.get(i).loadLabel(pm).toString());
            }
            rv = (RecyclerView) getView().findViewById(R.id.rv);
            RecyclerView.ItemAnimator itemAnimator = new DefaultItemAnimator();
            rv.setItemAnimator(itemAnimator);
            RecyclerView.ItemDecoration itemDecoration = new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL_LIST);
            rv.setLayoutManager(new LinearLayoutManager(getContext()));
            rv.setAdapter(new AppRecyclerViewAdapter(apps, appNames, getContext().getPackageManager(), getResources()));
            rv.addItemDecoration(itemDecoration);
        }
    }

}
