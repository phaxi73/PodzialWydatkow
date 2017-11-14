package com.example.bartek.podzialwydatkow;

import android.app.ProgressDialog;
import android.content.Intent;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.w3c.dom.Text;

import java.util.HashMap;

public class ExpenseAdderActivity extends AppCompatActivity {

    private android.support.v7.widget.Toolbar mToolbar;

    private TextInputLayout mExpenseName;
    private Button mExpenseBtn;

    private ProgressDialog mAddProgress;

    private FirebaseAuth mAuth;

    private DatabaseReference mExpensesDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_expense_adder);

        mToolbar = (android.support.v7.widget.Toolbar) findViewById(R.id.newexpense_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Nowy wydatek");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mAuth = FirebaseAuth.getInstance();

        mAddProgress = new ProgressDialog(this, R.style.AppCompatAlertDialogStyle);

        mExpenseName = (TextInputLayout) findViewById(R.id.adder_expense_name);
        mExpenseBtn = (Button) findViewById(R.id.adder_expense_btn);

        mExpenseBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String expense_name = mExpenseName.getEditText().getText().toString();

                if(!TextUtils.isEmpty(expense_name)){

                    mAddProgress.setTitle("Dodawanie wydatku");
                    mAddProgress.setMessage("Proszę czekać...");
                    mAddProgress.setCanceledOnTouchOutside(false);
                    mAddProgress.show();
                    add_expense(expense_name);

                }

            }
        });

    }

    private void    add_expense(final String expensename){

        FirebaseUser current_user = FirebaseAuth.getInstance().getCurrentUser();
        String uid  = current_user.getUid();

        mExpensesDatabase = FirebaseDatabase.getInstance().getReference().child("Expenses").child(uid).child("expense").push();


        HashMap<String, String> expenseMap = new HashMap<>();
        expenseMap.put("expensename", expensename);

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
