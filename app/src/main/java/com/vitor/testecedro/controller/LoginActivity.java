package com.vitor.testecedro.controller;

import android.app.KeyguardManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.hardware.fingerprint.FingerprintManager;
import android.os.Build;
import android.os.Bundle;
import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyPermanentlyInvalidatedException;
import android.security.keystore.KeyProperties;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.vitor.testecedro.R;
import com.vitor.testecedro.controller.fragments.FingerprintAuthenticationDialogFragment;
import com.vitor.testecedro.model.ApiResponse;
import com.vitor.testecedro.model.Person;
import com.vitor.testecedro.model.persistence.dao.PersonDAO;
import com.vitor.testecedro.rest.client.SiteClient;
import com.vitor.testecedro.rest.service.RetrofitConfig;
import com.vitor.testecedro.util.Global;
import com.vitor.testecedro.util.Verify;
import com.vitor.testecedro.util.fingerprint.FingerprintUiHelper;

import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.sql.SQLException;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;

import okhttp3.ResponseBody;

public class LoginActivity
        extends AppCompatActivity
        implements FingerprintUiHelper.Callback {
    private static final String TAG = "LoginActivity";

    private EditText edtLogin, edtPassword;
    private Button btnCreateUser, btnLogin;

    private TextView confirmationMessage;

    private ImageView fingerprintIconLogin;

    public ProgressDialog mProgressDialog;

    private SharedPreferences sharedPref;
    private SharedPreferences.Editor editor;

    private Person person;
    private PersonDAO dao;

    private KeyStore mKeyStore;
    private KeyGenerator mKeyGenerator;
    private Cipher mCipher;
    private String mKeyName;

    private FingerprintAuthenticationDialogFragment mFragment;
    private FingerprintUiHelper mFingerprintUiHelper;

    private KeyguardManager keyguardManager;
    private FingerprintManager fingerprintManager;
    private KeyGenParameterSpec.Builder builder;

    private Cipher defaultCipher;
    private FingerprintManager.CryptoObject cryptoObject;
    private SecretKey key;

    private boolean fromDialog = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        init();
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            mFingerprintUiHelper.startListening(cryptoObject);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            mFingerprintUiHelper.stopListening();
        }
    }

    private void init() {
        sharedPref = getSharedPreferences(Global.PREFERENCES_FILE, Context.MODE_PRIVATE);
        editor = sharedPref.edit();

        editor.putBoolean(Global.PREFERENCES_LOGGED_KEY, false);
        editor.commit();

        try {
            dao = new PersonDAO(this);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        edtLogin = findViewById(R.id.edtLogin);
        edtPassword = findViewById(R.id.edtPassword);

        edtLogin.setText(sharedPref.getString(Global.PREFERENCES_PERSON_EMAIL_KEY, ""));

        confirmationMessage = findViewById(R.id.confirmation_message);
        fingerprintIconLogin = findViewById(R.id.fingerprintIconLogin);

        btnCreateUser = findViewById(R.id.btnCreateUser);
        btnLogin = findViewById(R.id.btnLogin);

        btnCreateUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                registerNewUser(view);
            }
        });

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                userLogin(view);
            }
        });

        mKeyName = Global.DEFAULT_KEY_NAME;
        try {
            mKeyStore = KeyStore.getInstance(Global.KEYSTORE_PROVIDER_ANDROID_KEYSTORE);
        } catch (KeyStoreException e) {
            throw new RuntimeException("Failed to get an instance of KeyStore", e);
        }
        try {
            mKeyGenerator = KeyGenerator
                    .getInstance(KeyProperties.KEY_ALGORITHM_AES,
                            Global.KEYSTORE_PROVIDER_ANDROID_KEYSTORE);
        } catch (NoSuchAlgorithmException | NoSuchProviderException e) {
            throw new RuntimeException("Failed to get an instance of KeyGenerator", e);
        }
        try {
            defaultCipher = Cipher.getInstance(KeyProperties.KEY_ALGORITHM_AES + "/"
                    + KeyProperties.BLOCK_MODE_CBC + "/"
                    + KeyProperties.ENCRYPTION_PADDING_PKCS7);
            mCipher = defaultCipher;
        } catch (NoSuchAlgorithmException | NoSuchPaddingException e) {
            throw new RuntimeException("Failed to get an instance of Cipher", e);
        }

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            keyguardManager = getSystemService(KeyguardManager.class);
            fingerprintManager = getSystemService(FingerprintManager.class);

            if (!keyguardManager.isKeyguardSecure()) {
                // Show a message that the user hasn't set up a fingerprint or lock screen.
                Toast.makeText(this,
                        "Secure lock screen hasn't set up.\n"
                                + "Go to 'Settings -> Security -> Fingerprint' to set up a fingerprint",
                        Toast.LENGTH_LONG).show();
                return;
            }

            // Now the protection level of USE_FINGERPRINT permission
            // is normal instead of dangerous. See
            // http://developer.android.com/reference/android/Manifest.permission.html#USE_FINGERPRINT
            // The line below prevents the false positive inspection from Android Studio
            // noinspection ResourceType
            if (!fingerprintManager.hasEnrolledFingerprints()) {
                // This happens when no fingerprints are registered.
                Toast.makeText(this,
                        "Go to 'Settings -> Security -> Fingerprint' and register at least one" +
                                " fingerprint",
                        Toast.LENGTH_LONG).show();
                return;
            }
            createKey(Global.DEFAULT_KEY_NAME, true);
            createKey(Global.KEY_NAME_NOT_INVALIDATED, false);

            initFingerprintHelper();
        }
    }

    private void initFingerprintHelper() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            mFingerprintUiHelper = new FingerprintUiHelper(fingerprintManager,
                    fingerprintIconLogin,
                    confirmationMessage,
                    this);
        }
    }

    /**
     * Initialize the {@link Cipher} instance with the created key in the
     * {@link #createKey(String, boolean)} method.
     *
     * @param keyName the key name to init the cipher
     * @return {@code true} if initialization is successful, {@code false} if the lock screen has
     * been disabled or reset after the key was generated, or if a fingerprint got enrolled after
     * the key was generated.
     */
    @RequiresApi(api = Build.VERSION_CODES.M)
    private boolean initCipher(Cipher cipher, String keyName) {
        try {
            mKeyStore.load(null);
            key = (SecretKey) mKeyStore.getKey(keyName, null);
            cipher.init(Cipher.ENCRYPT_MODE, key);
            return true;
        } catch (KeyPermanentlyInvalidatedException e) {
            return false;
        } catch (KeyStoreException | CertificateException | UnrecoverableKeyException | IOException
                | NoSuchAlgorithmException | InvalidKeyException e) {
            throw new RuntimeException("Failed to init Cipher", e);
        }
    }

    /**
     * Creates a symmetric key in the Android Key Store which can only be used after the user has
     * authenticated with fingerprint.
     *
     * @param keyName the name of the key to be created
     * @param invalidatedByBiometricEnrollment if {@code false} is passed, the created key will not
     *                                         be invalidated even if a new fingerprint is enrolled.
     *                                         The default value is {@code true}, so passing
     *                                         {@code true} doesn't change the behavior
     *                                         (the key will be invalidated if a new fingerprint is
     *                                         enrolled.). Note that this parameter is only valid if
     *                                         the app works on Android N developer preview.
     *
     */
    @RequiresApi(api = Build.VERSION_CODES.M)
    public void createKey(String keyName, boolean invalidatedByBiometricEnrollment) {
        // The enrolling flow for fingerprint. This is where you ask the user to set up fingerprint
        // for your flow. Use of keys is necessary if you need to know if the set of
        // enrolled fingerprints has changed.
        try {
            mKeyStore.load(null);
            // Set the alias of the entry in Android KeyStore where the key will appear
            // and the constrains (purposes) in the constructor of the Builder

            builder = new KeyGenParameterSpec.Builder(keyName,
                    KeyProperties.PURPOSE_ENCRYPT |
                            KeyProperties.PURPOSE_DECRYPT)
                    .setBlockModes(KeyProperties.BLOCK_MODE_CBC)
                    // Require the user to authenticate with a fingerprint to authorize every use
                    // of the key
                    .setUserAuthenticationRequired(true)
                    .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_PKCS7);

            // This is a workaround to avoid crashes on devices whose API level is < 24
            // because KeyGenParameterSpec.Builder#setInvalidatedByBiometricEnrollment is only
            // visible on API level +24.
            // Ideally there should be a compat library for KeyGenParameterSpec.Builder but
            // which isn't available yet.
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                builder.setInvalidatedByBiometricEnrollment(invalidatedByBiometricEnrollment);
            }
            mKeyGenerator.init(builder.build());
            mKeyGenerator.generateKey();
        } catch (NoSuchAlgorithmException | InvalidAlgorithmParameterException
                | CertificateException | IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void userLogin(View view) {
        if(validFields()) {
            doLogin();
        }
    }

    private boolean validFields() {
        boolean isValid = true;

        if (edtLogin.getText().toString().isEmpty()) {
            edtLogin.setError("Campo não pode ser vazio.");
            isValid = false;
        }
        if (!Verify.isValidEmail(edtLogin.getText().toString())) {
            edtLogin.setError("Email não é válido.");
            isValid = false;
        }
        if (edtPassword.getText().toString().isEmpty()) {
            edtPassword.setError("Campo não pode ser vazio.");
            isValid = false;
        }

        return isValid;
    }

    public void makeLoginByDialog(String newPassword) {
        edtPassword.setText(newPassword);

        String email = edtLogin.getText().toString();
        String password = edtPassword.getText().toString();

        person = new Person();
        person.setEmail(email);
        person.setPassword(password);

        doLoginWithoutCreatePerson();
    }

    private void doLoginWithoutCreatePerson() {
        mProgressDialog = ProgressDialog.show(LoginActivity.this, "Aguarde",
                "Carregando");
        SiteClient.getInstance().doLogin(person, new RetrofitConfig.OnRestResponseListener<ApiResponse>() {
            @Override
            public void onRestSuccess(ApiResponse response) {
                mProgressDialog.dismiss();

                Global.token = response;
                Global.logged_person = person;

                editor.putBoolean(Global.PREFERENCES_LOGGED_KEY, true);
                editor.putString(Global.PREFERENCES_TOKEN_KEY, response.getToken());
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
                Toast.makeText(LoginActivity.this,
                        "Houve um erro ao realizar o login.",
                        Toast.LENGTH_LONG
                ).show();
            }
        });
    }

    private void doLogin() {
        String email = edtLogin.getText().toString();
        String password = edtPassword.getText().toString();

        person = new Person();
        person.setEmail(email);
        person.setPassword(password);

        mProgressDialog = ProgressDialog.show(LoginActivity.this, "Aguarde",
                "Carregando");
        SiteClient.getInstance().doLogin(person, new RetrofitConfig.OnRestResponseListener<ApiResponse>() {
            @Override
            public void onRestSuccess(ApiResponse response) {
                mProgressDialog.dismiss();

                if(dao.findByEmail(person.getEmail()) == null) {
                    fromDialog = true;
                    showFingerPrintFragment();
                }
                else {
                    Global.token = response;
                    Global.logged_person = person;

                    editor.putBoolean(Global.PREFERENCES_LOGGED_KEY, true);
                    editor.putString(Global.PREFERENCES_TOKEN_KEY, response.getToken());
                    editor.putString(Global.PREFERENCES_PERSON_EMAIL_KEY, person.getEmail());
                    editor.commit();

                    toMainActivity();
                }

            }

            @Override
            public void onRestError(ResponseBody body, Integer code) {
                mProgressDialog.dismiss();

                editor.putBoolean(Global.PREFERENCES_LOGGED_KEY, false);
                editor.commit();

                // TODO pegar resposta do erro e enviar como mensagem no toast
                Log.e(TAG, "Codigo de erro: " + code);
                Toast.makeText(LoginActivity.this,
                        "Houve um erro ao realizar o login.",
                        Toast.LENGTH_LONG
                ).show();
            }
        });
    }

    private void showFingerPrintFragment() {
        // Set up the crypto object for later. The object will be authenticated by use
        // of the fingerprint.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (initCipher(mCipher, mKeyName)) {

                // Show the fingerprint dialog. The user has the option to use the fingerprint with
                // crypto, or you can fall back to using a server-side verified password.

                mFragment = new FingerprintAuthenticationDialogFragment();
                cryptoObject = new FingerprintManager.CryptoObject(mCipher);
                mFragment.setCryptoObject(cryptoObject);
                mFragment.setStage(
                        FingerprintAuthenticationDialogFragment.Stage.FINGERPRINT);
                mFragment.show(getFragmentManager(), Global.DIALOG_FRAGMENT_TAG);
            } else {
                // This happens if the lock screen has been disabled or or a fingerprint got
                // enrolled. Thus show the dialog to authenticate with their password first
                // and ask the user if they want to authenticate with fingerprints in the
                // future
                mFragment = new FingerprintAuthenticationDialogFragment();
                cryptoObject = new FingerprintManager.CryptoObject(mCipher);
                mFragment.setCryptoObject(cryptoObject);
                mFragment.setStage(
                        FingerprintAuthenticationDialogFragment.Stage.NEW_FINGERPRINT_ENROLLED);
                mFragment.show(getFragmentManager(), Global.DIALOG_FRAGMENT_TAG);
            }
        }
    }

    @Override
    public void onAuthenticated() {
        String email = edtLogin.getText().toString();

        person = dao.findByEmail(email);
        if(person != null) {
            doLoginWithFingerprint();
        } else {
            if(fromDialog) {
                String password = edtPassword.getText().toString();

                person = new Person();
                person.setEmail(email);
                person.setPassword(password);

                dao.createOrUpdate(person);
                person = dao.findByEmail(email);

                doLogin();
            }
            else {
                validFieldsToFingerprint();
                firstLoginError();
            }
        }

    }

    private void firstLoginError() {
        editor.putBoolean(Global.PREFERENCES_LOGGED_KEY, false);
        editor.commit();

        Toast.makeText(this,
                "Ainda não foi cadastrada biometria nessa conta",
                Toast.LENGTH_LONG).show();
    }

    private void doLoginWithFingerprint() {
        mProgressDialog = ProgressDialog.show(LoginActivity.this, "Aguarde",
                "Carregando");
        SiteClient.getInstance().doLogin(person, new RetrofitConfig.OnRestResponseListener<ApiResponse>() {
            @Override
            public void onRestSuccess(ApiResponse response) {
                mProgressDialog.dismiss();

                Global.token = response;
                Global.logged_person = person;

                editor.putBoolean(Global.PREFERENCES_LOGGED_KEY, true);
                editor.putString(Global.PREFERENCES_TOKEN_KEY, response.getToken());
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
                Toast.makeText(LoginActivity.this,
                        "Houve um erro ao realizar o login.",
                        Toast.LENGTH_LONG
                ).show();
            }
        });
    }

    @Override
    public void onError() {
        Toast.makeText(this,
            "Erro no reconhecimento da biometria",
            Toast.LENGTH_LONG).show();
    }

    private boolean validFieldsToFingerprint() {
        boolean isValid = true;

        if (edtLogin.getText().toString().isEmpty()) {
            edtLogin.setError("Campo não pode ser vazio.");
            isValid = false;
        }
        if (!Verify.isValidEmail(edtLogin.getText().toString())) {
            edtLogin.setError("Email não é válido.");
            isValid = false;
        }

        return isValid;
    }

    private void toMainActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    private void registerNewUser(View view) {
        Intent intent = new Intent(this, NewPersonActivity.class);
        startActivity(intent);
        finish();
    }
}
