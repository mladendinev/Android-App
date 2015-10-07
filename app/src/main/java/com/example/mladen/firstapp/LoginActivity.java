package com.example.mladen.firstapp;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.method.PasswordTransformationMethod;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.CallbackManager;
import com.facebook.FacebookSdk;
import com.facebook.login.widget.LoginButton;

import database.helper.DatabaseHelper;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    Button loginButton;
    EditText etUsername, etPassword;
    private LoginButton fbLogin;
    private CallbackManager callbackManager;
    DatabaseHelper dbHelper;
    TextView registerLink;
    UserLocalStore userLocalStore;
    private SessionManager session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());
        setContentView(R.layout.activity_login);

        session = new SessionManager(getApplicationContext());

        //The SDK needs to be initialized before using any of its methods.
        if (session.isLoggedIn()) {
            // User is already logged in. Take him to main activity
            Intent intent = new Intent(LoginActivity.this, WelcomeActivity.class);
            startActivity(intent);
            finish();
        }

        callbackManager = CallbackManager.Factory.create();

        fbLogin = (LoginButton) findViewById(R.id.fb_login);

        dbHelper = new DatabaseHelper(this);


        loginButton = (Button) findViewById(R.id.loginButton);
        etUsername = (EditText) findViewById(R.id.etUsernameLog);

        etPassword = (EditText) findViewById(R.id.etPasswordLog);
        etPassword.setTypeface(Typeface.DEFAULT);
        etPassword.setTransformationMethod(new PasswordTransformationMethod());

        registerLink = (TextView) findViewById(R.id.sign_up);

        final ContentValues values = new ContentValues();
//
        loginButton.setOnClickListener(this);
        registerLink.setOnClickListener(this);
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.loginButton:
                String username = etUsername.getText().toString();
                String password = etPassword.getText().toString();

                if (username.isEmpty() || password.isEmpty()) {
                    Toast.makeText(getApplicationContext(),
                            "Please provide credentials", Toast.LENGTH_LONG)
                            .show();
                    break;
                }

                User user = new User(username, password);

                authenticate(user);
                break;

            case R.id.sign_up:
                Intent registerIntent = new Intent(LoginActivity.this, Register.class);
                startActivity(registerIntent);
                break;
        }
    }

    private void authenticate(User user) {
        ServerRequests serverRequest = new ServerRequests(this);
        serverRequest.fetchUserDataAsyncTask(user, new GetUserCallback() {
            @Override
            public void done(User returnedUser,int hui) {
                if (returnedUser == null) {
                    showErrorMessage();
                } else {
                    session.setLogin(true);
                    Intent userHistory = new Intent(LoginActivity.this, WelcomeActivity.class);
                    userHistory.putExtra("userName", returnedUser.username);
                    dbHelper.insertUser(returnedUser.username, returnedUser.email, returnedUser.password);
                    startActivity(userHistory);
                }
            }
        });
    }

    private void logUserIn(User returnedUser) {
        userLocalStore.storeUserData(returnedUser);
        userLocalStore.setUserLoggedIn(true);
        startActivity(new Intent(this, WelcomeActivity.class));
    }

    private void showErrorMessage() {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(LoginActivity.this);
        dialogBuilder.setMessage("Incorrect user details");
        dialogBuilder.setPositiveButton("Ok", null);
        dialogBuilder.show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }
}
