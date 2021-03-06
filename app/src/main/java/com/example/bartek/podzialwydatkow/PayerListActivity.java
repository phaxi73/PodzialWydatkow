package com.example.bartek.podzialwydatkow;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class PayerListActivity extends AppCompatActivity {

    private android.support.v7.widget.Toolbar mToolbar;

    private RecyclerView mPayerList;
    private DatabaseReference mUsersDatabase;
    private DatabaseReference mUserIdDatabase;

    private DatabaseReference mFriendsDatabase;
    private FirebaseAuth mAuth;

    private String mCurrent_user_id;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payer);



        //Toolbar
        mToolbar = findViewById(R.id.users_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Kto zapłacił?");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        mAuth = FirebaseAuth.getInstance();

        mCurrent_user_id = mAuth.getCurrentUser().getUid();
        mFriendsDatabase = FirebaseDatabase.getInstance().getReference().child("Friends").child(mCurrent_user_id);
        mUsersDatabase = FirebaseDatabase.getInstance().getReference().child("Users");

        mPayerList = findViewById(R.id.payer_list);
        mPayerList.setHasFixedSize(true);
        mPayerList.setLayoutManager(new LinearLayoutManager(this));




    }


    @Override
    protected void onStart() {
        super.onStart();

        FirebaseRecyclerAdapter<Friends, PayerViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Friends, PayerViewHolder>

                (Friends.class,
                 R.layout.users_single_layout,
                 PayerViewHolder.class,
                 mFriendsDatabase
                )

        {
            @Override
            protected void populateViewHolder(final PayerViewHolder PayerViewHolder, Friends friends, int position) {

                PayerViewHolder.setName(friends.getName());
                PayerViewHolder.setEmail(friends.getEmail());
                PayerViewHolder.setUserImage(friends.getThumb_image(), getApplicationContext());

                final String user_id = getRef(position).getKey();
                final String name = getRef(position).getKey();


                mUsersDatabase.child(user_id).addValueEventListener(new ValueEventListener() {       //ustawianie wartości dla konkretnego id usera na liście
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        String userName = dataSnapshot.child("name").getValue().toString();
                        String setUserImage = dataSnapshot.child("thumb_image").getValue().toString();
                        String userEmail = dataSnapshot.child("email").getValue().toString();

                        PayerViewHolder.setName(userName);                                           //Wyświetlanie
                        PayerViewHolder.setUserImage(setUserImage, getApplicationContext());         //na liście
                        PayerViewHolder.setEmail(userEmail);                                         //wyboru płacącego

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                        //Error
                    }
                });


                PayerViewHolder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        Intent expense_adder = new Intent (PayerListActivity.this, ExpenseAdderActivity.class);
                        expense_adder.putExtra("user_id", user_id);
                        expense_adder.putExtra("name", name);
                        startActivity(expense_adder);


                    }
                });

            }
        };

        mPayerList.setAdapter(firebaseRecyclerAdapter);
    }


    public static class PayerViewHolder extends RecyclerView.ViewHolder{

        View mView;

        public PayerViewHolder(View itemView){
            super(itemView);

            mView = itemView;

        }

        public void setEmail(String email){

            TextView userNameView = mView.findViewById(R.id.user_single_email);
            userNameView.setText(email);

        }


        public void setName(String name){

            TextView userNameView = mView.findViewById(R.id.user_single_name);
            userNameView.setText(name);
        }

        public void setUserImage (String thumb_image, Context ctx){

            CircleImageView userImageView = mView.findViewById(R.id.user_single_image);

            Picasso.with(ctx).load(thumb_image).placeholder(R.drawable.account_icon_orange).into(userImageView);

        }

    }

}
