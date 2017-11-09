package com.example.bartek.podzialwydatkow;

import android.app.ProgressDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.TextureView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

public class ProfileActivity extends AppCompatActivity {

    private ImageView mProfileImage;
    private TextView mDisplayName;
    private Button mProfileInviteBtn;

    private android.support.v7.widget.Toolbar mToolbar;

    private DatabaseReference mUsersDatabase;                   //Mam user_id, więc potrzebuje referencji do bazy danych, żeby móc uzyskać resztę danych

    private ProgressDialog mProgressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        mToolbar = (android.support.v7.widget.Toolbar) findViewById(R.id.profile_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Profil użytkownika");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        String user_id = getIntent().getStringExtra("user_id");         //Pobiera user_id z UsersActivity (z populatViewHolder)

        mUsersDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(user_id);

        mProfileImage = (ImageView) findViewById(R.id.profile_image);
        mDisplayName = (TextView) findViewById(R.id.profile_display_name);
        mProfileInviteBtn = (Button) findViewById(R.id.profile_invite_btn);

        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setTitle("Wczytywanie profilu");
        mProgressDialog.setMessage("Proszę czekać...");
        mProgressDialog.setCanceledOnTouchOutside(false);
        mProgressDialog.show();


        mUsersDatabase.addValueEventListener(new ValueEventListener() {         //Odbieram dane przy użyciu data snapshot
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                String display_name = dataSnapshot.child("name").getValue().toString();
                String image = dataSnapshot.child("image").getValue().toString();

                mDisplayName.setText(display_name);                                                        //Wyswietlanie nazwy użytkownika

                Picasso.with(ProfileActivity.this).load(image).placeholder(R.drawable.account_icon_orange_wide).into(mProfileImage);   //Wyswietlanie obrazu- Placeholder, żeby wyswietlalo najpierw domyslny obraz,

                mProgressDialog.dismiss();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {


            }
        });


    }
}
