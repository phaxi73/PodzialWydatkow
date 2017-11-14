package com.example.bartek.podzialwydatkow;

/**
 * Created by Bartek on 12.11.2017.
 */

public class Expenses {

    public String expensename;
    public String expensekey;
    public String user_id;


    public Expenses (){

    }

    public Expenses(String expensename, String expensekey, String user_id) {  //Konstruktory

        this.expensename = expensename;
        this.expensekey = expensekey;
        this.user_id = user_id;

    }

    //Gettery i settery

    public String getExpensename() {return expensename;}
    public void setExpensename(String expensename) {this.expensename = expensename;}


    public String getExpensekey() {return expensekey;}
    public void setExpensekey(String expensekey) {this.expensekey = expensekey;}


    public String getUser_id() {return user_id;}
    public void setUser_id(String user_id) {this.user_id = user_id;}


}
