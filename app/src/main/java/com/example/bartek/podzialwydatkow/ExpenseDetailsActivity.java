package com.example.bartek.podzialwydatkow;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class ExpenseDetailsActivity extends AppCompatActivity {

    private android.support.v7.widget.Toolbar mToolbar;

    private TextView mCreatorExpenseName;
    private TextView mCreatorAmount;
    private TextView mCreatorPayer;
    private Button mDeleteExpenseBtn;
    private RecyclerView mDebtorsList;
    private DatabaseReference mExpensesDatabase;
    private DatabaseReference mUsersDatabase;
    private DatabaseReference mExpenseKeyDatabase;
    private DatabaseReference mExpensesDatabaseExDelete;
    private FirebaseAuth mAuth;

    private String mCurrent_user_id = "";
    private String expensekey = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_expense_details);

        mToolbar = findViewById(R.id.expensecreator_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Szczegóły Wydatku");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        expensekey = getIntent().getStringExtra("expensekey");
        bindUI();
        InitFirebase();
        bindOnClickListeners();
        setupDebtors();
    }


    private void InitFirebase() {
        mCurrent_user_id = FirebaseAuth.getInstance().getCurrentUser().getUid();

        mExpensesDatabase = FirebaseDatabase.getInstance().getReference()
                .child("Expenses");
        mUsersDatabase = FirebaseDatabase.getInstance().getReference()
                .child("Users");
        mExpenseKeyDatabase = FirebaseDatabase.getInstance().getReference()
                .child("Expenses")
                .child(mCurrent_user_id)
                .child("expense")
                .child(expensekey);

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


    }

    private void bindUI() {
        mCreatorExpenseName = findViewById(R.id.creator_expense_name);
        mCreatorAmount = findViewById(R.id.creator_expense_amount);
        mCreatorPayer = findViewById(R.id.creator_expense_payer);
        mDeleteExpenseBtn = findViewById(R.id.expense_details_delete);
        mDebtorsList = findViewById(R.id.recyclerView_expense_details);
        mDebtorsList.setHasFixedSize(true);
        mDebtorsList.setLayoutManager(new LinearLayoutManager(this));
    }

    private void bindOnClickListeners() {
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

    private void setupDebtors() {
        final ArrayList<Friends> listOfDebtors = new ArrayList<>();
        mDebtorsList.setAdapter(new ExpenseDetailsRecyclerViewAdapter(listOfDebtors));

        mExpenseKeyDatabase.child("debtor").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                // Pobieramy listę dłużników
                for (DataSnapshot debtorChild : dataSnapshot.getChildren()) {
                    final String debtorID = debtorChild.getKey();
                    final String debtorDebt = debtorChild.getValue().toString();

                    // Pobieramy dane każdego dłuznika z listy
                    mUsersDatabase.child(debtorID).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            String debtorName = dataSnapshot.child("name").getValue().toString();
                            String debtorEmail = dataSnapshot.child("email").getValue().toString();
                            String debtorImage = dataSnapshot.child("thumb_image").getValue().toString();

                            listOfDebtors.add(new Friends(debtorName, debtorEmail, debtorID, debtorImage, expensekey, Double.parseDouble(debtorDebt)));
                            mDebtorsList.getAdapter().notifyDataSetChanged();
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });

                }


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


    }

}
