package com.example.bartek.podzialwydatkow;

/**
 * Created by Bartek on 16.11.2017.
 */

public class Friends {

    public String name;
    public String email;
    public String thumb_image;
    public String user_id;
    public String expensekey;
    public String amount;



    //public Integer intdebtorscounter;



    public Friends(){

    }

    public Friends(String name, String email, String user_id, String expensekey /*Integer intdebtorscounter*/) {  //Konstruktory

        this.name = name;
        this.email = email;
        this.thumb_image = thumb_image;
        this.user_id = user_id;
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
        return user_id;
    }
    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }


    public String getThumb_image() {
        return thumb_image;
    }
    public void setThumb_image(String thumb_image) {
        this.thumb_image = thumb_image;
    }


    public String getExpensekey() {return expensekey;}
    public void setExpensekey(String expensekey) {this.expensekey = expensekey;}







   // public Integer getIntdebtorscounter() {return intdebtorscounter;}
   // public void setIntdebtorscounter(Integer intdebtorscounter) {this.intdebtorscounter = intdebtorscounter;}

}
