package com.vitor.testecedro.util.password.storage;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Base64;

import com.vitor.testecedro.util.Global;

import java.nio.charset.Charset;

public class PasswordStorageHelperSDK16 implements PasswordStorageInterface {
    private static final String LOG_TAG = PasswordStorageHelperSDK16.class.getSimpleName();

    @Override
    public boolean init(Context context) {
        // Nothing to do here
        return true;
    }

    @Override
    public String encodedPassword(String data) {
        if (data == null)
            return null;
        return Base64.encodeToString(data.getBytes(), Base64.DEFAULT);
    }

    @Override
    public String decodedPassword(String res) {
        if (res == null)
            return null;
        return new String(Base64.decode(res, Base64.DEFAULT), Charset.forName("UTF-8"));
    }
}
