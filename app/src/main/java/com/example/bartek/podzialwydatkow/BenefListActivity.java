package com.example.bartek.podzialwydatkow;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.math.BigDecimal;
import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

import static java.lang.Math.toIntExact;

public class BenefListActivity extends AppCompatActivity {

    private android.support.v7.widget.Toolbar mToolbar;

    private RecyclerView mPayerList;
    private DatabaseReference mUsersDatabase;
    private DatabaseReference mUserIdDatabase;

    private DatabaseReference mFriendsDatabase;
    private DatabaseReference mExpensesDatabase;
    private DatabaseReference mExpensesDatabaseExName;
    private DatabaseReference mExpensesDatabaseCheck;
    private DatabaseReference mExpensesDatabaseUserId;
    private FirebaseAuth mAuth;

    private String mCurrent_user_id;
    private Button mHome_btn;



    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_beneflist);



        //Toolbar
        mToolbar = findViewById(R.id.users_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Kto skorzystał?");


        mAuth = FirebaseAuth.getInstance();

        mHome_btn = findViewById(R.id.beneflist_home_btn);

        mCurrent_user_id = mAuth.getCurrentUser().getUid();
        mFriendsDatabase = FirebaseDatabase.getInstance().getReference().child("Friends").child(mCurrent_user_id);
        mUsersDatabase = FirebaseDatabase.getInstance().getReference().child("Users");


        mPayerList = findViewById(R.id.payer_list);
        mPayerList.setHasFixedSize(true);
        mPayerList.setLayoutManager(new LinearLayoutManager(this));



        mHome_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                /*
                final String expensename = getIntent().getStringExtra("expensename");
                final String expensekey = getIntent().getStringExtra("expensekey");
                final String amount = getIntent().getStringExtra("amount");


                mExpensesDatabaseCheck = FirebaseDatabase.getInstance().getReference()
                        .child("Expenses")
                        .child(uid)
                        .child("expense")
                        .child(expensekey)
                        .child("debtor");

                if(--- W DEBTOR COS JEST, TO WYKONAJ)
                */


                //if(mExpensesDatabaseUserId != null) {


                    Intent homeactivity = new Intent(BenefListActivity.this, MainActivity.class);
                    startActivity(homeactivity);

                    //JESLI NIE, TOAST, ŻE TRZEBA DODAC KORZYSTAJACYCH

                //}

            }
        });


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
            protected void populateViewHolder(final PayerViewHolder PayerViewHolder, final Friends friends, int position) {

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

                        final String expensekey = getIntent().getStringExtra("expensekey");
                        final String amount = getIntent().getStringExtra("amount");

                        FirebaseUser current_user = FirebaseAuth.getInstance().getCurrentUser();
                        final String uid = current_user.getUid();


                        if(mExpensesDatabase != null) {
                            mExpensesDatabase.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {

                                    long debtorscounter = dataSnapshot.getChildrenCount();
                                    int intdebtorscounter = new BigDecimal(debtorscounter).intValueExact();    //Ilość korzystyjących (int)
                                    intdebtorscounter = intdebtorscounter + 1;


                                    Intent counterpass = new Intent(BenefListActivity.this, ExpenseDetailsActivity.class);
                                    counterpass.putExtra("debotrscounter", intdebtorscounter);

                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                    //Error

                                }
                            });
                        }


                            int intamount = Integer.parseInt(amount);


                            mExpensesDatabase = FirebaseDatabase.getInstance().getReference()
                                    .child("Expenses")
                                    .child(uid)
                                    .child("expense")
                                    .child(expensekey)
                                    .child("debtor");



                            HashMap<String, Object> debtorsMap = new HashMap<>();
                            debtorsMap.put(user_id, intamount);


                            mExpensesDatabase.updateChildren(debtorsMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {


                                    if (task.isSuccessful()) {

                                       //

                                    }

                                }
                            });

                            PayerViewHolder.mView.setBackgroundColor(Color.rgb(255,201,71));

                        Intent expensedetails = new Intent(BenefListActivity.this, ExpenseDetailsActivity.class);
                        expensedetails.putExtra("user_id", user_id);
                        expensedetails.putExtra("amount", amount);



                            mExpensesDatabaseUserId = FirebaseDatabase.getInstance().getReference()
                                .child("Expenses")
                                .child(uid)
                                .child("expense")
                                .child(expensekey)
                                .child("debtor")
                                .child(user_id);



                    }

                });


                PayerViewHolder.mView.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View view) {

                        mExpensesDatabase.child(user_id).removeValue();

                        PayerViewHolder.mView.setBackgroundColor(Color.rgb(63, 81, 181));

                        return true;
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

    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(this).setTitle("Powrót")
                .setIcon(R.drawable.ic_warning_purple_48dp)
                .setMessage("Czy na pewno chcesz wrócić? Zmiany nie zostaną zapisane.")
                .setNegativeButton("Tak", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        final String expensename = getIntent().getStringExtra("expensename");
                        final String expensekey = getIntent().getStringExtra("expensekey");
                        final String uid = getIntent().getStringExtra("userid");
                        final String amount = getIntent().getStringExtra("amount");

                        mExpensesDatabaseExName = FirebaseDatabase.getInstance().getReference()
                                .child("Expenses")
                                .child(uid)
                                .child("expense")
                                .child(expensekey)
                                .child(amount);


                        mExpensesDatabaseExName.getParent().removeValue();


                        Intent gohome = new Intent(BenefListActivity.this, MainActivity.class);
                        startActivity(gohome);

                    }
                }).setPositiveButton("Nie", null).show();
    }

    /*
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                new AlertDialog.Builder(this).setTitle("Powrót")
                        .setIcon(R.drawable.ic_warning_purple_48dp)
                        .setMessage("Czy na pewno chcesz wrócić? Zmiany nie zostaną zapisane.")
                        .setNegativeButton("Tak", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                //finish();

                                mExpensesDatabaseExName = FirebaseDatabase.getInstance().getReference()
                                        .child("Expenses")
                                        .child(uid)
                                        .child("expense")
                                        .child(expensekey)
                                        .child("expensename");

                                mExpensesDatabaseExName.getParent().removeValue();

                                Intent gohome = new Intent(BenefListActivity.this, MainActivity.class);
                                startActivity(gohome);

                            }
                        }).setPositiveButton("Nie", null).show();



                break;
        }
        return true;
    }
    */


}
