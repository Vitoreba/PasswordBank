package com.vitor.testecedro.controller;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.vitor.testecedro.R;
import com.vitor.testecedro.model.ApiResponse;
import com.vitor.testecedro.model.Person;
import com.vitor.testecedro.util.Global;

public class SplashActivity extends AppCompatActivity {

    private Intent intent;

    SharedPreferences sharedPref;

    Boolean logged = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        init();

        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if(logged) {
                    intent = new Intent(SplashActivity.this
                            , MainActivity.class);
                    startActivity(intent);
                    finish();
                } else {
                    intent = new Intent(SplashActivity.this
                            , LoginActivity.class);
                    startActivity(intent);
                    finish();
                }
                finish();
            }
        }, 2000);
    }

    private void init() {
        if(!Global.FRESCO_INIT) {
            Fresco.initialize(this);
            Global.FRESCO_INIT = true;
        }

        sharedPref = getSharedPreferences(Global.PREFERENCES_FILE, Context.MODE_PRIVATE);
        logged = sharedPref.getBoolean(Global.PREFERENCES_LOGGED_KEY, false);

        ApiResponse token = new ApiResponse();
        token.setToken(sharedPref.getString(Global.PREFERENCES_TOKEN_KEY, null));

        Person person = new Person();
        person.setEmail(sharedPref.getString(Global.PREFERENCES_PERSON_EMAIL_KEY, null));
        person.setName(sharedPref.getString(Global.PREFERENCES_PERSON_NAME_KEY, null));
        Global.logged_person = person;

        Global.token = token;
    }
}
