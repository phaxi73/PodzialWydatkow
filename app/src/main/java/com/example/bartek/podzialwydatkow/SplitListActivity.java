package com.example.bartek.podzialwydatkow;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class SplitListActivity extends AppCompatActivity {


    private android.support.v7.widget.Toolbar mToolbar;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_split_list);

        mToolbar = findViewById(R.id.splitlist_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Kto skorzystał");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

    }
}
