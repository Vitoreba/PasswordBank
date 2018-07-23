package com.vitor.testecedro.controller;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.vitor.testecedro.R;
import com.vitor.testecedro.model.Site;
import com.vitor.testecedro.model.persistence.dao.SitesDAO;
import com.vitor.testecedro.util.password.storage.PasswordStorageHelper;

import java.sql.SQLException;

public class NewSiteActivity extends AppCompatActivity {

    private EditText edtURLNewSite, edtEmailNewSite
            , edtPasswordNewSite, edtPasswordNewSiteConfirm;
    private Button btnCreateNewSite, btnCleanFields;

    private PasswordStorageHelper mPasswordStorageHelper;

    private SitesDAO dao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_site);

        init();
    }

    private void init() {
        this.getSupportActionBar().setTitle("Cadastrar novo site");

        edtURLNewSite = findViewById(R.id.edtURLNewSite);
        edtEmailNewSite = findViewById(R.id.edtEmailNewSite);
        edtPasswordNewSite = findViewById(R.id.edtPasswordNewSite);
        edtPasswordNewSiteConfirm = findViewById(R.id.edtPasswordNewSiteConfirm);

        mPasswordStorageHelper = new PasswordStorageHelper(this);

        try {
            dao = new SitesDAO(this);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        btnCreateNewSite = findViewById(R.id.btnCreateNewSite);
        btnCreateNewSite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createNewSite(view);
            }
        });

        btnCleanFields = findViewById(R.id.btnCleanFields);
        btnCleanFields.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cleanFields(view);
            }
        });
    }

    private void createNewSite(View view) {
        if(verify()) {
            String urlSite = edtURLNewSite.getText().toString().toLowerCase();
            String email = edtEmailNewSite.getText().toString();
            String password = edtPasswordNewSite.getText().toString();

            Site site = new Site();
            site.setUrlSite(urlSite);
            site.setLogin(email);
            site.setPassword(mPasswordStorageHelper.encodedPassword(password));

            dao.create(site);

            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            finish();
        }
    }

    private boolean verify() {
        boolean fieldsOk = true;

        if(edtURLNewSite.getText().toString().isEmpty()) {
            edtURLNewSite.setError("Campo não pode ser vazio");
            fieldsOk = false;
        }
        if(edtEmailNewSite.getText().toString().isEmpty()) {
            edtEmailNewSite.setError("Campo não pode ser vazio");
            fieldsOk = false;
        }
        if(edtPasswordNewSite.getText().toString().isEmpty()) {
            edtPasswordNewSite.setError("Campo não pode ser vazio");
            fieldsOk = false;
        } else if(!edtPasswordNewSite.getText().toString().equals(
                edtPasswordNewSiteConfirm.getText().toString()
        )) {
            edtPasswordNewSiteConfirm.setError("Senhas diferentes");
            fieldsOk = false;
        }

        return fieldsOk;
    }

    private void cleanFields(View view) {
        edtURLNewSite.setText("");
        edtEmailNewSite.setText("");
        edtPasswordNewSite.setText("");
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}
