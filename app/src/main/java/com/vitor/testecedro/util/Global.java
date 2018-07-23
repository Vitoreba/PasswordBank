package com.vitor.testecedro.util;

import com.vitor.testecedro.model.Person;
import com.vitor.testecedro.model.ApiResponse;

public class Global {

    public static Boolean FRESCO_INIT = false;

    public static ApiResponse token = null;

    public final static String PREFERENCES_FILE = "preferencias";
    public final static String PREFERENCES_LOGGED_KEY = "logged";
    public final static String PREFERENCES_TOKEN_KEY = "token";
    public final static String PREFERENCES_PERSON_NAME_KEY = "person_name";
    public final static String PREFERENCES_PERSON_EMAIL_KEY = "person_email";

    public static Person logged_person = null;

    public static final String KEYSTORE_PROVIDER_ANDROID_KEYSTORE = "AndroidKeyStore";
    public static final String DIALOG_FRAGMENT_TAG = "myFragment";
    public static final String SECRET_MESSAGE = "Very secret message";
    public static final String KEY_NAME_NOT_INVALIDATED = "key_not_invalidated";
    public static final String DEFAULT_KEY_NAME = "default_key";

}
