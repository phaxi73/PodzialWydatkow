package com.example.bartek.podzialwydatkow;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.TextureView;
import android.widget.TextView;

public class ProfileActivity extends AppCompatActivity {

    private TextView mDisplayID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        String user_id = getIntent().getStringExtra("user_id");         //Pobiera user_id z UsersActivity (z populatViewHolder)

        mDisplayID = (TextView) findViewById(R.id.profile_display_name);
        mDisplayID.setText(user_id);

    }
}
