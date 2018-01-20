package com.example.bartek.podzialwydatkow;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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

import java.util.ArrayList;
import java.util.HashMap;

public class ExpenseAdderActivity extends AppCompatActivity {

    // LOCAL DATA
    private ArrayList<Friends> listOfSelectedFriends = new ArrayList<>();
    private Double amountAsDouble = 0.0;
    private String user_id;
    private Friends currentUser = null;

    // UI
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
    private RecyclerView recyclerView;

    //DATABASE
    private FirebaseAuth mAuth;
    private FirebaseUser mCurrentUser;
    private DatabaseReference mExpensesDatabase;
    private DatabaseReference mUsersDatabase;
    private DatabaseReference mCurrentUserName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_expense_adder);

        user_id = getIntent().getStringExtra("user_id");

        InitFirebaseAndCurrentUser();
        bindUI();
        bindOnClickListeners();
    }

    private void add_expense(final String expensename, final String amount, final ArrayList<Friends> listOfDebtors) {

        mAddProgress.show();
        mExpensesDatabase = FirebaseDatabase.getInstance().getReference().child("Expenses").child(user_id).child("expense").push();

        HashMap<String, String> expenseMap = new HashMap<>();
        final String key = mExpensesDatabase.getKey().toString();
        expenseMap.put("expensename", expensename);
        expenseMap.put("payer", currentUser.name);
        expenseMap.put("amount", amount);

        // Ustawiamy początkowo dane wydatku
        mExpensesDatabase.setValue(expenseMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                // Jeżeli się uda, dodajemy dłuzników
                if(task.isSuccessful()){

                    HashMap<String, Double> mapOfDebtors = new HashMap<>();

                    for (Friends debtor : listOfDebtors) {
                        mapOfDebtors.put(debtor.getUser_id(), amountAsDouble / listOfDebtors.size());
                    }

                    // Wrzucamy na Firebase
                    mExpensesDatabase.child("debtor").setValue(mapOfDebtors).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {

                            // Jak dodało dłużników to jest okej
                            if (task.isSuccessful()) {
                                mAddProgress.dismiss();
                                finish();
                            }

                        }
                    });


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

    private void bindUI() {
        // Toolbar
        mToolbar = findViewById(R.id.newexpense_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Nowy wydatek");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Other
        mAddProgress = new ProgressDialog(this, R.style.AppCompatAlertDialogStyle);
        mExpenseName = findViewById(R.id.adder_expense_name);
        mAmount = findViewById(R.id.adder_expense_amount);
        mPayerBtn = findViewById(R.id.adder_payer_btn);
        mWhopaidBtn = findViewById(R.id.adder_expense_btn);
        mIpaidBtn = findViewById(R.id.adder_payer_me);

        // RecyclerView
        recyclerView = findViewById(R.id.expense_adder_recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
    }

    private void InitFirebaseAndCurrentUser() {

        mUsersDatabase = FirebaseDatabase.getInstance().getReference().child("Users");
        mAuth = FirebaseAuth.getInstance();
        mCurrentUser = FirebaseAuth.getInstance().getCurrentUser();
        user_id = mCurrentUser.getUid();

        mUsersDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if (dataSnapshot.hasChild(user_id)) {

                    String user_name = dataSnapshot
                            .child(user_id)
                            .child("name")
                            .getValue()
                            .toString();

                    String user_email = dataSnapshot
                            .child(user_id)
                            .child("email")
                            .getValue()
                            .toString();

                    String user_image = dataSnapshot
                            .child(user_id)
                            .child("image")
                            .getValue()
                            .toString();

                    //FIXME: Nie mam pojęcia czym jest expensekey
                    currentUser = new Friends(user_name, user_email, user_id, user_image, "", amountAsDouble / listOfSelectedFriends.size());

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

                //Error

            }
        });

    }

    private void bindOnClickListeners() {

        mWhopaidBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String expense_name = mExpenseName.getEditText().getText().toString();
                String amount = mAmount.getEditText().getText().toString();
                amountAsDouble = Double.valueOf(amount);

                if (textFieldsValid()) add_expense(expense_name, amount, listOfSelectedFriends);

            }
        });


        // --- WYBIERANIE PLACACEGO Z LISTY ZNAJOMYCH --- ~ Igor
        mPayerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (textFieldsValid()) {
                    Intent payer = new Intent(ExpenseAdderActivity.this, PayerListActivity.class);

                    // Startujemy aktywność nie normalnie, tylko z oczekiwaniem wyniku ~ Igor
                    startActivityForResult(payer, 1337);
                }
            }
        });

        // ---JA PLACE--- ~ Igor
        mIpaidBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (textFieldsValid()) {

                    amountAsDouble = Double.valueOf(mAmount.getEditText().getText().toString());
                    currentUser.amount = amountAsDouble;
                    listOfSelectedFriends.clear();
                    listOfSelectedFriends.add(currentUser);
                    recyclerView.setAdapter(new ExpenseAdderRecyclerViewAdapter(listOfSelectedFriends));

                }
            }
        });


    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1337) {
            if (resultCode == RESULT_OK) {
                // Łapiemy wartość z zamkniętej aktywności
                listOfSelectedFriends = (ArrayList<Friends>) data.getExtras().getSerializable("selectedUsers");

                // Przypisujemy każdemu dług
                Double sharedPrice = Double.valueOf(mAmount.getEditText().getText().toString()) / listOfSelectedFriends.size();
                for (Friends friend : listOfSelectedFriends) {
                    friend.amount = sharedPrice;
                }

                // Aktualizujemy listę
                recyclerView.setAdapter(new ExpenseAdderRecyclerViewAdapter(listOfSelectedFriends));
                Toast.makeText(getApplicationContext(), "Wybranych: " + listOfSelectedFriends.size(), Toast.LENGTH_LONG).show();
            } else if (resultCode == RESULT_CANCELED) {
                // Opcja gdy użytkownik nic nie wybierze
            }
        }
    }

    private boolean textFieldsValid() {

        String expense_name = mExpenseName.getEditText().getText().toString();
        String amount = mAmount.getEditText().getText().toString();

        if (!TextUtils.isEmpty(expense_name)
                && (!TextUtils.isEmpty(amount))) {

            return true;


        } else {

            Toast.makeText(ExpenseAdderActivity.this, "Uzupełnij puste pola!", Toast.LENGTH_SHORT).show();
            return false;
        }
    }


}
