package com.vitor.testecedro.controller;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;
import android.widget.Toolbar;

import com.vitor.testecedro.R;
import com.vitor.testecedro.model.Site;
import com.vitor.testecedro.model.persistence.dao.SitesDAO;
import com.vitor.testecedro.rest.client.SiteClient;
import com.vitor.testecedro.rest.service.RetrofitConfig;
import com.vitor.testecedro.util.Global;
import com.vitor.testecedro.util.password.storage.PasswordStorageHelper;

import java.sql.SQLException;

import okhttp3.ResponseBody;

public class SiteDetailActivity extends AppCompatActivity {
    private static final String TAG = "SiteDetailActivity";

    private Bundle extras;
    private Site site;

    private ImageView imgViewSiteDetailedLogo;
    private EditText edtSiteDetailedURL, edtSiteDetailedLogin;
    private TextInputEditText edtSiteDetailedPassword;

    private ImageButton btnCopyPassword;

    private Button btnDeleteSite, btnUpdateSite;

    private SitesDAO dao;

    private PasswordStorageHelper mPasswordStorageHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_site_detail);

        init();
    }

    private void init() {
        try {
            dao = new SitesDAO(this);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        mPasswordStorageHelper = new PasswordStorageHelper(this);

        imgViewSiteDetailedLogo = findViewById(R.id.imgViewSiteDetailedLogo);

        edtSiteDetailedURL = findViewById(R.id.edtSiteDetailedURL);
        edtSiteDetailedLogin = findViewById(R.id.edtSiteDetailedLogin);

        edtSiteDetailedPassword = findViewById(R.id.edtSiteDetailedPassword);

        btnCopyPassword = findViewById(R.id.btnCopyPassword);
        btnCopyPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                copyPasswordToClipboard(view);
            }
        });

        btnDeleteSite = findViewById(R.id.btnDeleteSite);
        btnDeleteSite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deleteSite(view);
            }
        });

        btnUpdateSite = findViewById(R.id.btnUpdateSite);
        btnUpdateSite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateSite(view);
            }
        });

        extras = getIntent().getExtras();
        site = (Site) extras.get("site");

        this.getSupportActionBar().setTitle(site.getUrlSite());

        edtSiteDetailedURL.setText(site.getUrlSite());
        edtSiteDetailedLogin.setText(site.getLogin());
        edtSiteDetailedPassword.setText(
                mPasswordStorageHelper.decodedPassword(site.getPassword())
        );

        // Sem melhores alternativas aqui, já que a API retorna a imagem e não a URL dela
        SiteClient.getInstance().doGetSiteLogo(Global.token.getToken(), site.getUrlSite()
            , new RetrofitConfig.OnRestResponseListener<ResponseBody>() {
                @Override
                public void onRestSuccess(ResponseBody body) {
                    Bitmap bm = BitmapFactory.decodeStream(body.byteStream());
                    bm = Bitmap.createScaledBitmap(bm, 200, 200, true);
                    imgViewSiteDetailedLogo.setImageBitmap(bm);
                }

                @Override
                public void onRestError(ResponseBody body, Integer code) {
                    Log.e(TAG, "Codigo de erro: " + code);
                }
        });
    }

    private void copyPasswordToClipboard(View view) {
        String password = edtSiteDetailedPassword.getText().toString();

        ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText("secure_password",password);
        clipboard.setPrimaryClip(clip);

        Toast.makeText(this,
                "Senha copiada para a area de trabalho",
                Toast.LENGTH_SHORT
        ).show();
    }

    private void deleteSite(View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle("Excluir");
        builder.setMessage("Deseja mesmo excluir este site?");

        builder.setPositiveButton("Sim", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dao.delete(site);

                // User clicked OK button
                Intent intent = new Intent(SiteDetailActivity.this
                        , MainActivity.class);
                startActivity(intent);
                finish();
            }
        });
        builder.setNegativeButton("Não", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void updateSite(View view) {
        String newURLSite = edtSiteDetailedURL.getText().toString();
        String newLoginSite = edtSiteDetailedLogin.getText().toString();
        String newPassword = edtSiteDetailedPassword.getText().toString();

        site.setUrlSite(newURLSite.toLowerCase());
        site.setLogin(newLoginSite);
        site.setPassword(mPasswordStorageHelper.encodedPassword(newPassword));

        dao.update(site);

        getIntent().putExtra("site", site);

        finish();
        startActivity(getIntent());
    }

    @Override
    public void onBackPressed() {
        toMainActivity();
    }

    private void toMainActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }

}
