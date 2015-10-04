package com.example.mladen.firstapp;

/**
 * Created by mladen on 10/2/15.
 */
public class User {

    String email, username, password;

    public User(String email, String username, String password) {
        this.email = email;
        this.password = password;
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public String getUsername() {return username;}

    public User(String username, String password) {
        this("", username, password);
    }
}
