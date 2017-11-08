package com.example.bartek.podzialwydatkow;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class UsersActivity extends AppCompatActivity {

    private android.support.v7.widget.Toolbar mToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_users);

        mToolbar = (android.support.v7.widget.Toolbar) findViewById(R.id.users_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Użytkownicy");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }
}
