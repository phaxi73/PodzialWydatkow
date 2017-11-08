package com.example.bartek.podzialwydatkow;

/**
 * Created by Bartek on 08.11.2017.
 */

public class Users {

    public String name;
    public String image;
    public String email;
    public String thumb_image;

    public Users (){

    }

    public Users(String name, String image, String email) {  //Konstruktory
        this.name = name;
        this.image = image;
        this.email = email;
        this.thumb_image = thumb_image;
    }

    //Gettery i settery

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getThumb_image() {
        return thumb_image;
    }

    public void setThumb_image(String thumb_image) {
        this.thumb_image = thumb_image;
    }
}
