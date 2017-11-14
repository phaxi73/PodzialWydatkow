package com.example.bartek.podzialwydatkow;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ExpenseCreatorActivity extends AppCompatActivity {

    private TextView mCreatorExpenseName;

    private DatabaseReference mExpensesDatabase;

    private FirebaseAuth mAuth;

    private String mCurrent_user_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_expense_creator);

        mExpensesDatabase = FirebaseDatabase.getInstance().getReference().child("Expenses");

        mAuth = FirebaseAuth.getInstance();
        mCurrent_user_id = mAuth.getCurrentUser().getUid();

        mCreatorExpenseName = (TextView) findViewById(R.id.creator_expense_name);
        final String expensekey = getIntent().getStringExtra("expense");


        mExpensesDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                String expensename = dataSnapshot.child("Expenses").child(mCurrent_user_id).child("expense").child(expensekey).child("expensename").toString();
                mCreatorExpenseName.setText(expensename);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });



    }
}
