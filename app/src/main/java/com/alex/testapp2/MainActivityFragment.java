package com.alex.testapp2;

import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment {

    static RecyclerView rv;
    SearchView searchView;

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
        }
        LinearLayoutManager llm = new LinearLayoutManager(getContext());
        AppRecyclerViewAdapter adapter = new AppRecyclerViewAdapter(apps, getContext().getPackageManager(), getResources());
        rv = (RecyclerView) getView().findViewById(R.id.rv);
        searchView = (SearchView) getActivity().findViewById(R.id.searchView);
        RecyclerView.ItemAnimator itemAnimator = new DefaultItemAnimator();
        rv.setItemAnimator(itemAnimator);
        RecyclerView.ItemDecoration itemDecoration = new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL_LIST);
        rv.setLayoutManager(llm);
        rv.setAdapter(adapter);
        rv.addItemDecoration(itemDecoration);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                newText = newText.toLowerCase();
                final List<ApplicationInfo> filteredList = new ArrayList<>();
                for(ApplicationInfo s: apps) {
                    if(getAppName(s, getContext().getPackageManager()).toLowerCase().contains(newText)) {
                        filteredList.add(s);
                    }
                }
                AppRecyclerViewAdapter adapter = new AppRecyclerViewAdapter(filteredList, getContext().getPackageManager(), getResources());
                rv.setAdapter(adapter);
                adapter.notifyDataSetChanged();
                return false;
            }
        });
    }

    private String getAppName(ApplicationInfo info, PackageManager pm) {
        return info.loadLabel(pm).toString();
    }

}
