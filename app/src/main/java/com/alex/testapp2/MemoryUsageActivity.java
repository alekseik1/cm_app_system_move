package com.alex.testapp2;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.TextView;

public class MemoryUsageActivity extends AppCompatActivity {

    TextView system_free, system_total, system_percentage, system_used, data_free, data_total, data_used, data_percentage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_memory_usage);
        ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);

        system_free = (TextView) findViewById(R.id.mem_info_system_free_tv);
        system_percentage = (TextView) findViewById(R.id.mem_info_system_percentage_tv);
        system_total = (TextView) findViewById(R.id.mem_info_system_total_tv);
        system_used = (TextView) findViewById(R.id.mem_info_system_used_tv);
        data_free = (TextView) findViewById(R.id.mem_info_data_free_tv);
        data_used = (TextView) findViewById(R.id.mem_info_data_used_tv);
        data_total = (TextView) findViewById(R.id.mem_info_data_total_tv);
        data_percentage = (TextView) findViewById(R.id.mem_info_data_percentage_tv);
        double[] a;
        a = RootUtils.getPartitionSize("/system");
        int b = Integer.parseInt(Long.toString(Math.round(a[1] / a[0] * 100)));
        if (a[3] == 0) {
            system_percentage.setText(Integer.toString(b) + "%");
            system_total.setText(Double.toString(a[0]) + "M");
            system_free.setText(Double.toString(a[2]) + "M");
            system_used.setText(Double.toString(a[1]) + "M");
        } else if (a[3] == 1) {
            system_percentage.setText(Integer.toString(b) + "%");
            system_total.setText(Double.toString(a[0]) + "G");
            system_free.setText(Double.toString(a[2]) + "G");
            system_used.setText(Double.toString(a[1]) + "G");
        }
        a = RootUtils.getPartitionSize("/data");
        b = Integer.parseInt(Long.toString(Math.round(a[1] / a[0] * 100)));
        if (a[3] == 0) {
            data_percentage.setText(Integer.toString(b) + "%");
            data_total.setText(Double.toString(a[0]) + "M");
            data_free.setText(Double.toString(a[2]) + "M");
            data_used.setText(Double.toString(a[1]) + "M");
        } else if (a[3] == 1) {
            data_percentage.setText(Integer.toString(b) + "%");
            data_total.setText(Double.toString(a[0]) + "G");
            data_free.setText(Double.toString(a[2]) + "G");
            data_used.setText(Double.toString(a[1]) + "G");
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        onBackPressed();
        return super.onOptionsItemSelected(item);
    }
}
