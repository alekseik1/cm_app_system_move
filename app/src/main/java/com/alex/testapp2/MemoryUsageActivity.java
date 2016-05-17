package com.alex.testapp2;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.SeekBar;
import android.widget.TextView;

public class MemoryUsageActivity extends AppCompatActivity {

    SeekBar sb1, sb2;
    TextView tv1, tv2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_memory_usage);

        sb1 = (SeekBar) findViewById(R.id.seekBar);
        sb2 = (SeekBar) findViewById(R.id.seekBar2);
        tv1 = (TextView) findViewById(R.id.mem_usage_system_textview);
        tv2 = (TextView) findViewById(R.id.mem_info_data_textview);
        double[] a;
        a = RootUtils.getPartitionSize("/system");
        int b = Integer.parseInt(Long.toString(Math.round(a[1] / a[0] * 100)));
        sb1.setProgress(b);
        tv1.setText("/system  " + b + "%");
        a = RootUtils.getPartitionSize("/data");
        b = Integer.parseInt(Long.toString(Math.round(a[1] / a[0] * 100)));
        sb2.setProgress(b);
        tv2.setText("/data  " + b + "%");
    }
}
