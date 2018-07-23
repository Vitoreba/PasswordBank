package com.vitor.testecedro.util.password.storage;

import android.content.Context;

public interface PasswordStorageInterface {

    // Initialize all necessary objects for working with AndroidKeyStore
    boolean init(Context context);
    // Set data which we want to keep in secret
    String encodedPassword(String data);
    // Get stored secret data
    String decodedPassword(String data);

}
