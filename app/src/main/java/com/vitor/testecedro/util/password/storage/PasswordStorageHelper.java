package com.vitor.testecedro.util.password.storage;

import android.content.Context;
import android.util.Log;

public class PasswordStorageHelper {

    private static final String LOG_TAG = PasswordStorageHelper.class.getSimpleName();

    private PasswordStorageInterface passwordStorage = null;

    public PasswordStorageHelper(Context context) {
        if (android.os.Build.VERSION.SDK_INT < 18) {
            passwordStorage = new PasswordStorageHelperSDK16();
        } else {
            passwordStorage = new PasswordStorageHelperSDK18();
        }

        boolean isInitialized = false;

        try {
            isInitialized = passwordStorage.init(context);
        } catch (Exception ex) {
            Log.e(LOG_TAG, "PasswordStorage initialisation error:" + ex.getMessage(), ex);
        }

        if (!isInitialized && passwordStorage instanceof PasswordStorageHelperSDK18) {
            passwordStorage = new PasswordStorageHelperSDK16();
            passwordStorage.init(context);
        }
    }

    public String encodedPassword(String data) {
        return passwordStorage.encodedPassword(data);
    }

    public String decodedPassword(String data) {
        return passwordStorage.decodedPassword(data);
    }
}

