package com.example.bartek.podzialwydatkow;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
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

import java.util.HashMap;

import static android.view.View.VISIBLE;

public class ExpenseAdderActivity extends AppCompatActivity {

    private android.support.v7.widget.Toolbar mToolbar;

    private TextInputLayout mExpenseName;
    private TextInputLayout mAmount;
    private Button mWhopaidBtn;
    private TextView mPayerBtn;
    private TextView mPayerTxt;
    private TextView mWhenPaidTxt;
    private TextView mPayerChosenTxt;
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

        final String name = getIntent().getStringExtra("name");
        final String user_id = getIntent().getStringExtra("user_id");

        mUsersDatabase = FirebaseDatabase.getInstance().getReference().child("Users");
        mAuth = FirebaseAuth.getInstance();
        mCurrentUser = FirebaseAuth.getInstance().getCurrentUser();
        mCurrentUserName = FirebaseDatabase.getInstance().getReference().child("Users").child(mCurrentUser.getUid()).child("name");

        mAddProgress = new ProgressDialog(this, R.style.AppCompatAlertDialogStyle);

        mExpenseName = findViewById(R.id.adder_expense_name);
        mAmount = findViewById(R.id.adder_expense_amount);
        mPayerBtn = findViewById(R.id.adder_payer_btn);

        mPayerTxt = findViewById(R.id.adder_payer_text);
        mWhenPaidTxt = findViewById(R.id.adder_whenpaid_txt);
        mPayerTxt.setVisibility(View.INVISIBLE);
        mWhenPaidTxt.setVisibility(View.INVISIBLE);
        mPayerChosenTxt = findViewById(R.id.adder_whenpaid_txt);

        mWhopaidBtn = findViewById(R.id.adder_expense_btn);
        mIpaidBtn = findViewById(R.id.adder_payer_me);

        mWhopaidBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String expense_name = mExpenseName.getEditText().getText().toString();
                String amount = mAmount.getEditText().getText().toString();
                String user_name = mPayerTxt.getText().toString();


                if(!TextUtils.isEmpty(expense_name) &&
                  (!TextUtils.isEmpty(amount) &&
                  (mPayerChosenTxt.getVisibility() == View.VISIBLE))){

                   // mAddProgress.setTitle("Dodawanie wydatku");
                    //mAddProgress.setMessage("Proszę czekać...");
                    //mAddProgress.setCanceledOnTouchOutside(false);
                    //mAddProgress.show();
                    add_expense(expense_name, amount, user_name);


                } else {

                    Toast.makeText(ExpenseAdderActivity.this, "Uzupełnij puste pola!", Toast.LENGTH_SHORT).show();

                }

            }
        });



        // --- WYBIERANIE PLACACEGO Z LISTY ZNAJOMYCH ---
        mPayerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent payer = new Intent(ExpenseAdderActivity.this, PayerListActivity.class);

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
        if(user_id != null) {

            mUsersDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    if(dataSnapshot.hasChild(user_id)){

                    String user_name = dataSnapshot
                            .child(user_id)
                            .child("name").getValue().toString();

                    mPayerTxt.setText(user_name);

                    }

                    if (!TextUtils.isEmpty(mPayerTxt.getText().toString())) {

                        mPayerTxt.setVisibility(VISIBLE);
                        mWhenPaidTxt.setVisibility(VISIBLE);

                    }

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                    //Error

                }
            });

        }



    }



    private void add_expense (final String expensename, final String amount, String user_name) {


        FirebaseUser current_user = FirebaseAuth.getInstance().getCurrentUser();
        final String uid  = current_user.getUid();

        mExpensesDatabase = FirebaseDatabase.getInstance().getReference().child("Expenses").child(uid).child("expense").push();



        HashMap<String, String> expenseMap = new HashMap<>();
        final String key = mExpensesDatabase.getKey().toString();
        String testname = expenseMap.put("expensename", expensename);
        String testamount = expenseMap.put("amount", amount);
        String testpayer = expenseMap.put("payer", user_name);

        mExpensesDatabase.setValue(expenseMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {

                if(task.isSuccessful()){

                    //mAddProgress.dismiss();

                    Intent benefactivity = new Intent(ExpenseAdderActivity.this, BenefListActivity.class);
                    benefactivity.putExtra("expensename", expensename);
                    benefactivity.putExtra("expensekey", key);
                    benefactivity.putExtra("userid", uid);
                    benefactivity.putExtra("amount", amount);
                    startActivity(benefactivity);

                }

            }
        });

    }

    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(this).setTitle("Powrót")
                .setIcon(R.drawable.ic_warning_purple_48dp)
                .setMessage("Czy na pewno chcesz wrócić? Zmiany nie zostaną zapisane.")
                .setNegativeButton("Tak", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        finish();
                    }
                }).setPositiveButton("Nie", null).show();
    }


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


                                Intent gohome = new Intent(ExpenseAdderActivity.this, MainActivity.class);
                                startActivity(gohome);

                            }
                        }).setPositiveButton("Nie", null).show();



                break;
        }
        return true;
    }




}
