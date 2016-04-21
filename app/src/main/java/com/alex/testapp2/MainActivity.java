package com.alex.testapp2;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;

import com.stericson.RootShell.execution.Command;
import com.stericson.RootShell.execution.Shell;
import com.stericson.RootTools.RootTools;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        MainActivityFragment f1 = new MainActivityFragment();
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.fragment, f1);
        ft.commit();

        if(!RootTools.isRootAvailable()) {
            AlertDialog.Builder ab = new AlertDialog.Builder(this);
            ab.setTitle(R.string.no_root);
            ab.setMessage(R.string.no_root_description);
            ab.setPositiveButton(R.string.ok, null);
            ab.create().show();
        }


        Shell shell = null;
        try {
            shell = RootTools.getShell(true);
        } catch(IOException e) {

        } catch(TimeoutException | com.stericson.RootShell.exceptions.RootDeniedException e) {
            AlertDialog.Builder ab = new AlertDialog.Builder(this);
            ab.setTitle(R.string.no_root);
            ab.setMessage(R.string.no_root_description);
            ab.setPositiveButton(R.string.ok, null);
            ab.create().show();
        }
        if(shell != null) {
            try {
                shell.add(new Command(0, "echo Test_1", "echo Test_2"));
            } catch(IOException e) {

            }
        }

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
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
}
