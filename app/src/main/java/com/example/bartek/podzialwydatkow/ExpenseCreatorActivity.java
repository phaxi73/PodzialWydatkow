package com.example.bartek.podzialwydatkow;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ExpenseCreatorActivity extends AppCompatActivity {

    private android.support.v7.widget.Toolbar mToolbar;

    private TextView mCreatorExpenseName;
    private TextView mCreatorAmount;

    private DatabaseReference mExpensesDatabase;

    private FirebaseAuth mAuth;

    private String mCurrent_user_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_expense_creator);

        mToolbar = findViewById(R.id.expensecreator_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Edytuj Wydatek");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mExpensesDatabase = FirebaseDatabase.getInstance().getReference().child("Expenses");

        mAuth = FirebaseAuth.getInstance();
        mCurrent_user_id = mAuth.getCurrentUser().getUid();


        mCreatorExpenseName = findViewById(R.id.creator_expense_name);
        mCreatorAmount = findViewById(R.id.creator_expense_amount);

        final String expensekey = getIntent().getStringExtra("expensekey");
        final String user_id = getIntent().getStringExtra("user_id");


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

                    mCreatorExpenseName.setText(expense_name);
                    mCreatorAmount.setText(amount + " zł");


                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

                Toast.makeText(getApplicationContext(), "Wystąpił błąd", Toast.LENGTH_LONG).show();

            }
        });



    }
}
