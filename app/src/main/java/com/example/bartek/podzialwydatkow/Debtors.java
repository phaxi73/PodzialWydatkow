package com.example.bartek.podzialwydatkow;

/**
 * Created by Bartek on 02.12.2017.
 */

public class Debtors {

    public String name;
    public String email;
    public String thumb_image;
    public String debtor_id;
    public String expensekey;
    public String amount;


    //public Integer intdebtorscounter;


    public Debtors() {

    }

    public Debtors(String name, String email, String user_id, String expensekey /*Integer intdebtorscounter*/) {  //Konstruktory

        this.name = name;
        this.email = email;
        this.thumb_image = thumb_image;
        this.debtor_id = debtor_id;
        this.expensekey = expensekey;
        //this.intdebtorscounter = intdebtorscounter;

    }


    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }


    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }


    public String getUser_id() {
        return debtor_id;
    }
    public void setUser_id(String user_id) {
        this.debtor_id = user_id;
    }


    public String getThumb_image() {
        return thumb_image;
    }
    public void setThumb_image(String thumb_image) {
        this.thumb_image = thumb_image;
    }


    public String getExpensekey() {return expensekey;}
    public void setExpensekey(String expensekey) {this.expensekey = expensekey;}


    public String getAmount() {
        return amount;
    }
    public void setAmount(String amount) {
        this.amount = amount;
    }

}