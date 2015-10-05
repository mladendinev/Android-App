package com.example.mladen.firstapp;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

/**
 * Created by mladen on 10/3/15.
 */
public class Register extends AppCompatActivity implements View.OnClickListener {

    EditText etEmail, etUsername, etPassword;
    Button registerButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register_layout);

        etEmail = (EditText) findViewById(R.id.etEmail);
        etUsername = (EditText) findViewById(R.id.etUsernameReg);

        etPassword = (EditText) findViewById(R.id.etPasswordReg);
        etPassword.setTypeface(Typeface.DEFAULT);
        etPassword.setTransformationMethod(new PasswordTransformationMethod());

        registerButton = (Button) findViewById(R.id.registerButton);
        registerButton.setOnClickListener(this);


    }
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.registerButton:
                String email = etEmail.getText().toString();
                String username = etUsername.getText().toString();
                String password = etPassword.getText().toString();

                if (username.isEmpty() || password.isEmpty() || email.isEmpty()) {
                    Toast.makeText(getApplicationContext(),
                            "Please provide credentials", Toast.LENGTH_LONG)
                            .show();
                    break;
                }
                User user = new User(email, username, password);
                registerUser(user);
                break;
        }
    }


        private void registerUser(User user) {

            ServerRequests serverRequest = new ServerRequests(this);
            serverRequest.storeUserDataInBackground(user, new GetUserCallback() {
                @Override
                public void done(User returnedUser) {
                    AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(Register.this);

                    Intent loginIntent = new Intent(Register.this, LoginActivity.class);
                    startActivity(loginIntent);
                }
            });
        }
}
