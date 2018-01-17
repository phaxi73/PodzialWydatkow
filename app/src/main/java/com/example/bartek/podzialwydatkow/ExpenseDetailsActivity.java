package com.example.bartek.podzialwydatkow;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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

public class ExpenseDetailsActivity extends AppCompatActivity {

    private android.support.v7.widget.Toolbar mToolbar;

    private TextView mCreatorExpenseName;
    private TextView mCreatorAmount;
    private TextView mCreatorPayer;



    private Button mDeleteExpenseBtn;

    private RecyclerView mDebtorsList;


    private DatabaseReference mExpensesDatabase;
    private DatabaseReference mUsersDatabase;
    private DatabaseReference mExpensesDebtorDatabase;
    private DatabaseReference mExpensesDatabaseExDelete;

    private FirebaseAuth mAuth;

    private String mCurrent_user_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_expense_details);

        mToolbar = findViewById(R.id.expensecreator_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Szczegóły Wydatku");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        final String expensekey = getIntent().getStringExtra("expensekey");
        //final String user_id = getIntent().getStringExtra("user_id");



        mExpensesDatabase = FirebaseDatabase.getInstance().getReference().child("Expenses");
        mUsersDatabase = FirebaseDatabase.getInstance().getReference().child("Users");



        mAuth = FirebaseAuth.getInstance();
        mCurrent_user_id = mAuth.getCurrentUser().getUid();


        mCreatorExpenseName = findViewById(R.id.creator_expense_name);
        mCreatorAmount = findViewById(R.id.creator_expense_amount);
        mCreatorPayer = findViewById(R.id.creator_expense_payer);
        mDeleteExpenseBtn = findViewById(R.id.expense_details_delete);



        mDebtorsList = findViewById(R.id.debtors_list);
        mDebtorsList.setHasFixedSize(true);
        mDebtorsList.setLayoutManager(new LinearLayoutManager(this));




        mExpensesDatabase.child(mCurrent_user_id).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if(dataSnapshot.hasChild("expense")){

                    String expense_name = dataSnapshot
                            .child("expense")
                            .child(expensekey)
                            .child("expensename").getValue().toString();

                    String amount = dataSnapshot
                            .child("expense")
                            .child(expensekey)
                            .child("amount").getValue().toString();

                    String payer = dataSnapshot
                            .child("expense")
                            .child(expensekey)
                            .child("payer").getValue().toString();




                    mCreatorExpenseName.setText(expense_name);
                    mCreatorAmount.setText(amount + " zł");
                    mCreatorPayer.setText(payer);



                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

                Toast.makeText(getApplicationContext(), "Wystąpił błąd", Toast.LENGTH_LONG).show();

            }
        });

        //USUWANIE WYDATKU PRZYCISK
        mDeleteExpenseBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                mExpensesDatabaseExDelete = FirebaseDatabase.getInstance().getReference()
                        .child("Expenses")
                        .child(mCurrent_user_id)
                        .child("expense")
                        .child(expensekey);

                mExpensesDatabaseExDelete.removeValue();
                finish();

            }
        });


    }

    @Override
    protected void onStart() {
        super.onStart();

        final String expensekey = getIntent().getStringExtra("expensekey");
        final String amount = getIntent().getStringExtra("amount");


        // POBIERA Z EXPENSES FRAGMENT I ZAMIAST USER_ID TO EXPENSEKEY, POWINNO BRAC USER_ID Z BenefList
        final String user_id = getIntent().getStringExtra("user_id");

        mExpensesDebtorDatabase = FirebaseDatabase.getInstance().getReference()
                .child("Expenses")
                .child(mCurrent_user_id)
                .child("expense")
                .child(expensekey)
                .child("debtor")
                .child(user_id);


        FirebaseRecyclerAdapter<Friends, BenefListActivity.PayerViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Friends, BenefListActivity.PayerViewHolder>

                (Friends.class,
                 R.layout.users_single_layout,
                 BenefListActivity.PayerViewHolder.class,
                 mExpensesDebtorDatabase
                )


        {

            @Override
            protected void populateViewHolder(final BenefListActivity.PayerViewHolder DebtorsViewHolder, final Friends friends, int position) {

                DebtorsViewHolder.setName(friends.getName());
                DebtorsViewHolder.setEmail(friends.getEmail());
                DebtorsViewHolder.setUserImage(friends.getThumb_image(), getApplicationContext());


                final String user_id = getRef(position).getKey();


                mUsersDatabase.child(user_id).addValueEventListener(new ValueEventListener() {       //ustawianie wartości dla konkretnego id usera na liście
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        String userName = dataSnapshot.child("name").getValue().toString();
                        String setUserImage = dataSnapshot.child("thumb_image").getValue().toString();
                        String userEmail = dataSnapshot.child("email").getValue().toString();


                        DebtorsViewHolder.setName(userName);                                           //Wyświetlanie
                        DebtorsViewHolder.setUserImage(setUserImage, getApplicationContext());         //na liście
                        DebtorsViewHolder.setEmail(userEmail);                                         //korzystajacych

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                        //Error
                    }
                });



                DebtorsViewHolder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {


                        Toast.makeText(ExpenseDetailsActivity.this, "Tap", Toast.LENGTH_SHORT).show();



                    }

                });


            }
        };

        mDebtorsList.setAdapter(firebaseRecyclerAdapter);



    }




    public static class DebtorsViewHolder extends RecyclerView.ViewHolder{

        View mView;

        public DebtorsViewHolder(View itemView){
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
