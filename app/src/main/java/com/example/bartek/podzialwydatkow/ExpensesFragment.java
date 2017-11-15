package com.example.bartek.podzialwydatkow;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


public class ExpensesFragment extends Fragment {


    private RecyclerView mExpensesList;

    private DatabaseReference mExpensesDatabase;
    private FirebaseAuth mAuth;

    private String mCurrent_user_id;

    private View mMainView;

    private Button mExpensesAdderBtn;

    public ExpensesFragment() {  // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        mMainView = inflater.inflate(R.layout.fragment_expenses, container, false);

        mExpensesAdderBtn = mMainView.findViewById(R.id.expenses_new_expense);

        mExpensesList = mMainView.findViewById(R.id.expenses_list);
        mAuth = FirebaseAuth.getInstance();

        mCurrent_user_id = mAuth.getCurrentUser().getUid();

        mExpensesDatabase = FirebaseDatabase.getInstance().getReference().child("Expenses").child(mCurrent_user_id).child("expense");

        mExpensesList.setHasFixedSize(true);
        mExpensesList.setLayoutManager(new LinearLayoutManager(getContext()));

        mExpensesAdderBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent adderintent = new Intent(getActivity(), ExpenseAdderActivity.class);
                startActivity(adderintent);

            }
        });

        return mMainView; //Inflate the layout for this fragment
    }

    public void onStart(){
        super.onStart();


        FirebaseRecyclerAdapter<Expenses, ExpensesViewHolder> expensesRecyclerViewAdapter = new FirebaseRecyclerAdapter<Expenses, ExpensesViewHolder>
                (Expenses.class,
                 R.layout.expenses_single_layout,
                 ExpensesViewHolder.class,
                 mExpensesDatabase
                )

        {
            @Override
            protected void populateViewHolder(ExpensesViewHolder expensesViewHolder, Expenses expenses, int position) {


                expensesViewHolder.setExpenseName(expenses.getExpensename());           //Bierze Expensename z klasy Expenses.java
                //expensesViewHolder.setExpenseKey(expenses.getExpensekey());
                //expensesViewHolder.setUser_id(expenses.getUser_id());

                final String expensename = getRef(position).getKey();
                final String expensekey = getRef(position).getKey();
                final String user_id = getRef(position).getKey();

                expensesViewHolder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        Intent expensecreator_intent = new Intent(getActivity(), ExpenseCreatorActivity.class);
                        expensecreator_intent.putExtra("expensemame", expensename);
                        expensecreator_intent.putExtra("expensekey", expensekey);
                        expensecreator_intent.putExtra("user_id", user_id);
                        startActivity(expensecreator_intent);

                    }
                });

            }

        };

        mExpensesList.setAdapter(expensesRecyclerViewAdapter);

    }

    public static class ExpensesViewHolder extends RecyclerView.ViewHolder{   //Wyswietlanie danych na liscie expenses

        View mView;                                                           //mView definiuje cały expenses_single_layout


        public ExpensesViewHolder(View itemView) {
            super(itemView);

            mView = itemView;

        }


        public void setExpenseName(String expensename){

            TextView mExpenseNameView = mView.findViewById(R.id.expenses_single_expense);  //Nazwa wydatku
            mExpenseNameView.setText("   •" + expensename);

        }



    }


}
