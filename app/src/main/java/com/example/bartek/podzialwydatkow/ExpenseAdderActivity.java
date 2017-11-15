package com.example.bartek.podzialwydatkow;

import android.app.ProgressDialog;
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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.w3c.dom.Text;

import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ExpenseAdderActivity extends AppCompatActivity {

    private android.support.v7.widget.Toolbar mToolbar;

    private TextInputLayout mExpenseName;
    private TextInputLayout mAmount;
    private Button mExpenseBtn;

    private ProgressDialog mAddProgress;

    private FirebaseAuth mAuth;

    private DatabaseReference mExpensesDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_expense_adder);

        mToolbar = findViewById(R.id.newexpense_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Nowy wydatek");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mAuth = FirebaseAuth.getInstance();

        mAddProgress = new ProgressDialog(this, R.style.AppCompatAlertDialogStyle);

        mExpenseName = findViewById(R.id.adder_expense_name);
        mAmount = findViewById(R.id.adder_expense_amount);

        mExpenseBtn = findViewById(R.id.adder_expense_btn);

        mExpenseBtn.setOnClickListener(new View.OnClickListener() {
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
