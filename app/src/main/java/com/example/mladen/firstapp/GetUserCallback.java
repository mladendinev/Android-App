package com.example.mladen.firstapp;

import com.example.mladen.firstapp.User;

/**
 * Created by mladen on 10/3/15.
 */
interface GetUserCallback {
    public abstract void done(User returnedUser,int hui);
}
