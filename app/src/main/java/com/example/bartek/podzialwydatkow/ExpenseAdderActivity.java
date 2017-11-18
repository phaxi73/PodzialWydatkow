package com.example.bartek.podzialwydatkow;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;

import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static android.view.View.VISIBLE;

public class ExpenseAdderActivity extends AppCompatActivity {

    private android.support.v7.widget.Toolbar mToolbar;

    private TextInputLayout mExpenseName;
    private TextInputLayout mAmount;
    private Button mWhopaidBtn;
    private TextView mPayerBtn;
    private TextView mPayerTxt;
    private TextView mWhenPaidTxt;
    private Button mIpaidBtn;

    private ProgressDialog mAddProgress;

    private FirebaseAuth mAuth;
    private FirebaseUser mCurrentUser;


    private DatabaseReference mExpensesDatabase;
    private DatabaseReference mUsersDatabase;
    private DatabaseReference mCurrentUserName;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_expense_adder);

        mToolbar = findViewById(R.id.newexpense_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Nowy wydatek");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        mUsersDatabase = FirebaseDatabase.getInstance().getReference().child("Users");
        mAuth = FirebaseAuth.getInstance();
        mCurrentUser = FirebaseAuth.getInstance().getCurrentUser();
        mCurrentUserName = FirebaseDatabase.getInstance().getReference().child("Users").child(mCurrentUser.getUid()).child("name");

        mAddProgress = new ProgressDialog(this, R.style.AppCompatAlertDialogStyle);

        final String name = getIntent().getStringExtra("name");
        final String user_id = getIntent().getStringExtra("user_id");

        mExpenseName = findViewById(R.id.adder_expense_name);
        mAmount = findViewById(R.id.adder_expense_amount);
        mPayerBtn = findViewById(R.id.adder_payer_btn);


        mPayerTxt = findViewById(R.id.adder_payer_text);
        mWhenPaidTxt = findViewById(R.id.adder_whenpaid_txt);
        mPayerTxt.setVisibility(View.INVISIBLE);
        mWhenPaidTxt.setVisibility(View.INVISIBLE);

        mWhopaidBtn = findViewById(R.id.adder_expense_btn);
        mIpaidBtn = findViewById(R.id.adder_payer_me);

        mWhopaidBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String expense_name = mExpenseName.getEditText().getText().toString();
                String amount = mAmount.getEditText().getText().toString();


                if(!TextUtils.isEmpty(expense_name) && (!TextUtils.isEmpty(amount))){

                    mAddProgress.setTitle("Dodawanie wydatku");
                    mAddProgress.setMessage("Proszę czekać...");
                    mAddProgress.setCanceledOnTouchOutside(false);
                    mAddProgress.show();
                    add_expense(expense_name, amount);

                } else {

                    Toast.makeText(ExpenseAdderActivity.this, "Uzupełnij puste pola!", Toast.LENGTH_SHORT).show();

                }

            }
        });

        // --- WYBIERANIE PLACACEGO Z LISTY ZNAJOMYCH ---
        mPayerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent payer = new Intent(ExpenseAdderActivity.this, PayerActivity.class);
                startActivity(payer);

            }
        });


        // --- WYBIERANIE ZALOGOWANEWGO UZYTKOWNIKA JAKO PLACACEGO ---
        mCurrentUserName.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                final String current_user_name = dataSnapshot.getValue().toString();


                mIpaidBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        mPayerTxt.setText(current_user_name);

                        mPayerTxt.setVisibility(VISIBLE);
                        mWhenPaidTxt.setVisibility(VISIBLE);

                    }
                });

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

                //Error

            }
        });




        // --- KTO ZAPLACIL WYSWIETLANIE ---
        mPayerTxt.setText(name);

        if(!TextUtils.isEmpty(mPayerTxt.getText().toString())){

            mPayerTxt.setVisibility(VISIBLE);
            mWhenPaidTxt.setVisibility(VISIBLE);

        }




    }

    private void    add_expense(final String expensename, String amount){


        FirebaseUser current_user = FirebaseAuth.getInstance().getCurrentUser();
        String uid  = current_user.getUid();

        mExpensesDatabase = FirebaseDatabase.getInstance().getReference().child("Expenses").child(uid).child("expense").push();


        HashMap<String, String> expenseMap = new HashMap<>();
        expenseMap.put("expensename", expensename);
        expenseMap.put("amount", amount);

        mExpensesDatabase.setValue(expenseMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {

                if(task.isSuccessful()){

                    mAddProgress.dismiss();

                    Intent mainIntent = new Intent(ExpenseAdderActivity.this, MainActivity.class);
                    startActivity(mainIntent);

                }

            }
        });

    }






}
