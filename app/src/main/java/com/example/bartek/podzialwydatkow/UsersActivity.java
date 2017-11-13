package com.example.bartek.podzialwydatkow;

import android.content.Context;
import android.content.Intent;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.telephony.CellIdentityCdma;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

import de.hdodenhof.circleimageview.CircleImageView;

public class UsersActivity extends AppCompatActivity {

    private android.support.v7.widget.Toolbar mToolbar;

    private RecyclerView mUsersList;

    private DatabaseReference mUsersDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_users);

        //Toolbar
        mToolbar = (android.support.v7.widget.Toolbar) findViewById(R.id.users_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Użytkownicy");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mUsersDatabase = FirebaseDatabase.getInstance().getReference().child("Users");

        mUsersList = (RecyclerView) findViewById(R.id.users_list);
        mUsersList.setHasFixedSize(true);
        mUsersList.setLayoutManager(new LinearLayoutManager(this));


    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseRecyclerAdapter<Users, UsersViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Users, UsersViewHolder>
                (Users.class,
                 R.layout.users_single_layout,
                 UsersViewHolder.class,
                 mUsersDatabase
                )
        {

            @Override
            protected void populateViewHolder(UsersViewHolder usersViewHolder, Users users, int position) {      //ViewHolder- Dane do wyświetlania na liście

                usersViewHolder.setName(users.getName());                                                 //Pobiera name z Users.java
                usersViewHolder.setUserEmail(users.getEmail());                                           //Pobiera email
                usersViewHolder.setUserImage(users.getThumb_image(), getApplicationContext());            //Pobiera thumb_image, context dla UserViewHolder (wyswietlanie na liście)

                final String user_id = getRef(position).getKey();                                               //Pozycja (gdzie user tapuje, tutaj- na layout pojedynczego usera)
                final String name = getRef(position).getKey();

                usersViewHolder.mView.setOnClickListener(new View.OnClickListener() {                     //mView definiuje cały users_single_layout
                    @Override
                    public void onClick(View view) {

                        Intent profile_intent = new Intent(UsersActivity.this, ProfileActivity.class);
                        profile_intent.putExtra("user_id", user_id);                               //Wysyłam user_id do innej strony (ProfileActivity.class)
                        profile_intent.putExtra("name", name);
                        startActivity(profile_intent);
                    }
                });

            }
        };

        mUsersList.setAdapter(firebaseRecyclerAdapter);

    }

    public static class UsersViewHolder extends RecyclerView.ViewHolder{   //Wyswietlanie danych na liscie użytkowników

        View mView;                                                        //mView definiuje cały users_single_layout

        public UsersViewHolder(View itemView) {
            super(itemView);

            mView = itemView;

        }

        public void setName(String name){

            TextView mUserNameView = (TextView) mView.findViewById(R.id.user_single_name);  //Nazwa użytkownika
            mUserNameView.setText(name);

        }

        public void setUserEmail (String email){

            TextView userEmaiView = (TextView) mView.findViewById(R.id.user_single_email);  //Hasło
            userEmaiView.setText(email);

        }

        public void setUserImage (String thumb_image, Context ctx){                         //Uzywam sub class, więc muszę podać context z populateViewHolder

            CircleImageView userImageView = (CircleImageView) mView.findViewById(R.id.user_single_image);

            Picasso.with(ctx).load(thumb_image).placeholder(R.drawable.account_icon_orange).into(userImageView);

        }

    }


}
