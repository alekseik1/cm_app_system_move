package com.alex.testapp2;

import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by aleksei on 18.04.16.
 */
public class AppRecyclerViewAdapter extends RecyclerView.Adapter<AppRecyclerViewAdapter.AppViewHolder> {

    List <ApplicationInfo> apps;
    List<String> appNames = new ArrayList<>();
    PackageManager pm;
    Resources res;
    List<Double> appSize = new ArrayList<>();

    public class AppViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView appName, appWhere, appSize;
        ImageView appIcon;
        AlertDialog.Builder ab;
        AlertDialog ad;

        public AppViewHolder(View v) {
            super(v);
            v.setOnClickListener(this);
            appName = (TextView) v.findViewById(R.id.appName);
            appWhere = (TextView) v.findViewById(R.id.appWhere);
            appIcon = (ImageView) v.findViewById(R.id.appIcon);
            appSize = (TextView) v.findViewById(R.id.appSize);
        }

        @Override
        public void onClick(View v) {
            Log.d("click", String.valueOf(getPosition()));
            if(ad == null) {
                ab = new AlertDialog.Builder(v.getContext());
                final Context context = v.getContext();
                final View view = v;
                final Toolbar tv = (Toolbar) v.getRootView().findViewById(R.id.toolbar);
                final short dest = packageDest(apps.get(getPosition()));
                if(dest == 0) ab.setMessage(R.string.convert_to_user);
                else if(dest == 2) ab.setMessage(R.string.convert_to_system);
                else ab.setMessage(R.string.integrate_to_system);
                AlertDialog.OnClickListener ocl = new AlertDialog.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if(which == AlertDialog.BUTTON_POSITIVE) {
                            if(dest == 0) {
                                RootUtils.moveToUser(apps.get(getPosition()), context);
                            } else if(dest == 2) {
                                RootUtils.moveToSystem(apps.get(getPosition()), context);
                            } else {
                                RootUtils.moveToSystem(apps.get(getPosition()), context);
                            }
                            tv.setSubtitle(R.string.need_reboot);
                            tv.setSubtitleTextColor(view.getResources().getColor(R.color.orange));
                        }
                    }
                };
                ab.setPositiveButton(R.string.yes, ocl);
                ab.setNegativeButton(R.string.no, ocl);
                ad = ab.create();
            }
            try {
                ad.show();
            } catch(NullPointerException e) {
                e.printStackTrace();
            }
        }
    }

    public AppRecyclerViewAdapter(List<ApplicationInfo> apps, PackageManager pm, Resources res) {
        this.apps = apps;
        this.pm = pm;
        this.res = res;
    }

    @Override
    public AppViewHolder onCreateViewHolder(ViewGroup parent, int i) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.app_list_item, parent, false);
        for(ApplicationInfo a : apps) {
            this.appSize.add(RootUtils.getAppSize(a));
        }
        for (ApplicationInfo a : apps) {
            this.appNames.add(a.loadLabel(pm).toString());
        }
        return new AppViewHolder(v);
    }

    @Override
    public void onBindViewHolder(AppViewHolder vh, int i) {
        short d = packageDest(apps.get(i));
        vh.appName.setText(appNames.get(i));
        vh.appSize.setText(Double.toString(appSize.get(i))+ " MB");
        try {
            vh.appIcon.setImageDrawable(apps.get(i).loadIcon(pm));
        } catch(NullPointerException e) {
            e.printStackTrace();
        }
        if(d == 0) {
            vh.appWhere.setText(R.string.system_app);
            vh.appWhere.setTextColor(res.getColor(R.color.orange));
        } else if(d == 2) {
            vh.appWhere.setText(R.string.user_app);
            vh.appWhere.setTextColor(res.getColor(R.color.green));
        } else if(d == 1) {
            vh.appWhere.setText(R.string.updated_app);
            vh.appWhere.setTextColor(res.getColor(R.color.blue));
        }
    }

    public short packageDest(ApplicationInfo applicationInfo) { // 0 - system app; 1 - updated system app; 2 - user app
        if((applicationInfo.flags & ApplicationInfo.FLAG_UPDATED_SYSTEM_APP) != 0) return 1;
        if((applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) != 0) return 0;
        return 2;
    }

    @Override
    public int getItemCount() {
        return apps.size();
    }
}
