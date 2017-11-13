package com.example.bartek.podzialwydatkow;

/**
 * Created by Bartek on 12.11.2017.
 */

public class Expenses {

    public String expensename;

    public Expenses (){

    }

    public Expenses(String expensename) {  //Konstruktory
        this.expensename = expensename;

    }

    //Gettery i settery

    public String getExpensename()
    {return expensename;}

    public void setExpensename(String expensename)
    {this.expensename = expensename;}


}
