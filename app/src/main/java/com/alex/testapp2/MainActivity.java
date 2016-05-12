package com.alex.testapp2;

import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Parcelable;
import android.os.PersistableBundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import eu.chainfire.libsuperuser.Shell;

public class MainActivity extends AppCompatActivity {

    RecyclerView rv;
    Parcelable parcelable;
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        rv = (RecyclerView) findViewById(R.id.rv);
        setSupportActionBar(toolbar);

        MainActivityFragment f1 = new MainActivityFragment();
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.fragment, f1);
        ft.commit();

        if(!Shell.SU.available()) {
            AlertDialog.Builder ab = new AlertDialog.Builder(this);
            ab.setTitle(R.string.no_root);
            ab.setMessage(R.string.no_root_description);
            ab.setPositiveButton(R.string.ok, null);
            ab.create().show();
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        //if(ad != null) ad.show();
        //ad = null;
    }

    @Override
    protected void onResumeFragments() {
        super.onResumeFragments();
        if(parcelable != null)
        rv.getLayoutManager().onRestoreInstanceState(parcelable);
    }

    private AlertDialog noBusybox() {
        AlertDialog.Builder ab = new AlertDialog.Builder(getApplicationContext());
        ab.setTitle(R.string.no_busybox_title);
        ab.setMessage(R.string.no_busybox_message);
        ab.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                android.os.Process.killProcess(android.os.Process.myPid());
            }
        });
        return ab.create();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
        super.onSaveInstanceState(outState, outPersistentState);
        outState.putParcelable("rvState", rv.getLayoutManager().onSaveInstanceState());
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        parcelable = savedInstanceState.getParcelable("rvState");
    }

}
