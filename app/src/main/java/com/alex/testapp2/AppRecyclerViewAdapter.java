package com.alex.testapp2;

import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.Resources;
import android.graphics.BitmapFactory;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

/**
 * Created by aleksei on 18.04.16.
 */
public class AppRecyclerViewAdapter extends RecyclerView.Adapter<AppRecyclerViewAdapter.AppViewHolder> {

    List <ApplicationInfo> apps;
    List<String> appNames;
    PackageManager pm;
    Resources res;

    public class AppViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView appName, appWhere;
        ImageView appIcon;
        AlertDialog.Builder ab;
        AlertDialog ad;

        public AppViewHolder(View v) {
            super(v);
            v.setOnClickListener(this);
            appName = (TextView) v.findViewById(R.id.appName);
            appWhere = (TextView) v.findViewById(R.id.appWhere);
            appIcon = (ImageView) v.findViewById(R.id.appIcon);
        }

        @Override
        public void onClick(View v) {
            Log.d("click", String.valueOf(getPosition()));
            if(ad == null) {
                ab = new AlertDialog.Builder(v.getContext());
                final Context context = v.getContext();
                final short dest = packageDest(apps.get(getPosition()));
                if(dest == 0 || dest == 1) ab.setMessage(R.string.convert_to_user);
                else ab.setMessage(R.string.convert_to_system);
                AlertDialog.OnClickListener ocl = new AlertDialog.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if(which == AlertDialog.BUTTON_POSITIVE) {
                            if(dest == 0 || dest == 1) {
                                RootUtils.moveToUser(apps.get(getPosition()), context);
                            } else {
                                RootUtils.moveToSystem(apps.get(getPosition()), context);
                            }
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

    public AppRecyclerViewAdapter(List apps, List<String> appNames, PackageManager pm, Resources res) {
        this.appNames = appNames;
        this.apps = apps;
        this.pm = pm;
        this.res = res;
    }

    @Override
    public AppViewHolder onCreateViewHolder(ViewGroup parent, int i) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.app_list_item, parent, false);
        return new AppViewHolder(v);
    }

    @Override
    public void onBindViewHolder(AppViewHolder vh, int i) {
        short d = packageDest(apps.get(i));
        vh.appName.setText(appNames.get(i));
        try {
            vh.appIcon.setImageDrawable(apps.get(i).loadIcon(pm));
        } catch(NullPointerException e) {
            e.printStackTrace();
        }
        if(d == 0 || d == 1) {
            vh.appWhere.setText(R.string.system_app);
            vh.appWhere.setTextColor(res.getColor(R.color.orange));
        } else {
            vh.appWhere.setText(R.string.user_app);
            vh.appWhere.setTextColor(res.getColor(R.color.green));
        }
    }

    public short packageDest(ApplicationInfo applicationInfo) { // 0 - system app; 1 - updated system app; 2 - user app
        if((applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) != 0) return 0;
        if((applicationInfo.flags & ApplicationInfo.FLAG_UPDATED_SYSTEM_APP) != 0) return 1;
        return 2;
    }

    @Override
    public int getItemCount() {
        return apps.size();
    }
}