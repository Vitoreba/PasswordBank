package com.vitor.testecedro.controller;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.vitor.testecedro.R;
import com.vitor.testecedro.model.ApiResponse;
import com.vitor.testecedro.model.Person;
import com.vitor.testecedro.model.persistence.dao.PersonDAO;
import com.vitor.testecedro.rest.client.SiteClient;
import com.vitor.testecedro.rest.service.RetrofitConfig.OnRestResponseListener;
import com.vitor.testecedro.util.Global;
import com.vitor.testecedro.util.Verify;

import java.sql.SQLException;

import okhttp3.ResponseBody;

public class NewPersonActivity extends AppCompatActivity {
    private static final String TAG = "NewUserActivity";

    EditText edtNewName, edtNewUser, edtPassword, edtConfirmPassword;
    Button btnCreate, btnCleanFields;

    public ProgressDialog mProgressDialog;

    SharedPreferences sharedPref;
    SharedPreferences.Editor editor;

    Person person;
    PersonDAO dao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_person);

        init();
    }

    private void init() {
        sharedPref = getSharedPreferences(Global.PREFERENCES_FILE, Context.MODE_PRIVATE);
        editor = sharedPref.edit();

        try {
            dao = new PersonDAO(this);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        edtNewName = findViewById(R.id.edtNewName);
        edtNewUser = findViewById(R.id.edtNewUser);
        edtPassword = findViewById(R.id.edtNewPassword);
        edtConfirmPassword = findViewById(R.id.edtNewPasswordConfirm);

        btnCreate = findViewById(R.id.btnCreateNewUser);
        btnCleanFields = findViewById(R.id.btnCleanFields);

        btnCleanFields.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cleanFields(view);
            }
        });

        btnCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createNewUser(view);
            }
        });
    }

    private void createNewUser(View view) {
        if(validFields()) {
            registerNewUser();
        }
    }

    private boolean validFields() {
        boolean isValid = true;

        if(edtNewName.getText().toString().isEmpty()){
            edtPassword.setError("O campo nome não deve ficar vazio.");
            isValid = false;
        }
        String email = edtNewUser.getText().toString();
        if(!Verify.isValidEmail(email)) {
            edtNewUser.setError("O email digitado não é válido.");
            isValid = false;
        }
        if(edtPassword.getText().toString().length() < 10){
            edtPassword.setError("A senha deve conter pelo menos 10 digitos.");
            isValid = false;
        }
        else if(!Verify.isValidPassword(edtPassword.getText().toString())){
            edtPassword.setError("Senha fraca! A senha deve conter pelo menos 1 letra, 1 número e um caracter especial.");
            isValid = false;
        }
        boolean equalPasswords = edtPassword.getText().toString().equals(edtConfirmPassword.getText().toString());
        if(!equalPasswords) {
            edtConfirmPassword.setError("As senhas não são iguais.");
            isValid = false;
        }

        return isValid;
    }

    private void registerNewUser() {
        String name = edtNewName.getText().toString();
        String email = edtNewUser.getText().toString();
        String password = edtPassword.getText().toString();

        person = new Person();
        person.setName(name);
        person.setEmail(email);
        person.setPassword(password);

        mProgressDialog = ProgressDialog.show(NewPersonActivity.this, "Aguarde",
                "Carregando");
        // Fazer cadastro
        SiteClient.getInstance().doRegisterPerson(person, new OnRestResponseListener<ApiResponse>() {
            @Override
            public void onRestSuccess(ApiResponse response) {
                mProgressDialog.dismiss();

                Global.token = response;
                Global.logged_person = person;

                editor.putBoolean(Global.PREFERENCES_LOGGED_KEY, true);
                editor.putString(Global.PREFERENCES_TOKEN_KEY, response.getToken());
                editor.putString(Global.PREFERENCES_PERSON_NAME_KEY, person.getName());
                editor.putString(Global.PREFERENCES_PERSON_EMAIL_KEY, person.getEmail());
                editor.commit();

                toMainActivity();
            }

            @Override
            public void onRestError(ResponseBody body, Integer code) {
                mProgressDialog.dismiss();

                editor.putBoolean(Global.PREFERENCES_LOGGED_KEY, false);
                editor.commit();

                // TODO pegar resposta do erro e enviar como mensagem no toast
                Log.e(TAG, "Codigo de erro: " + code);
                Toast.makeText(NewPersonActivity.this,
                        "Houve um erro ao realizar o cadastro.",
                        Toast.LENGTH_LONG
                ).show();
            }
        });
    }

    private void toMainActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    private void cleanFields(View view) {
        edtNewUser.setText("");
        edtPassword.setText("");
        edtConfirmPassword.setText("");
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
        finish();
    }
}
