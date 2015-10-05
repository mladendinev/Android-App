package com.example.mladen.firstapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import database.helper.DatabaseHelper;

public class WelcomeActivity extends AppCompatActivity implements View.OnClickListener {

    TextView email;
    String username;
    SessionManager session;
    DatabaseHelper db;
    Button logoutButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        db = new DatabaseHelper(this);
        session = new SessionManager(getApplicationContext());

        if (!session.isLoggedIn()) {
            logoutUser();
        }


        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            username = extras.getString("userName");
        }

        email = (TextView) findViewById(R.id.name_user);
        email.setText(username);

        logoutButton = (Button) findViewById(R.id.logout);
        logoutButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.logout:
                logoutUser();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_welcome, menu);
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

    private void logoutUser() {
        session.setLogin(false);
        db.deleteUserData();
        // Launching the login activity
        Intent intent = new Intent(WelcomeActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }
}
