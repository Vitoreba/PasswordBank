package com.vitor.testecedro.util;

import android.text.TextUtils;

import java.util.regex.Pattern;

public class Verify {

    private final static String EXPRESSAO_REGULAR_SENHA_FORTE = "^(?=.*[0-9])(?=.*[a-zA-Z])(?=.*[!@#$%^&+=])(?=\\S+$).{10,}$";

    public final static boolean isValidEmail(CharSequence target) {
        if (TextUtils.isEmpty(target)) {
            return false;
        } else {
            return android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
        }
    }

    public final static boolean isValidPassword(CharSequence target) {
        if (TextUtils.isEmpty(target)) {
            return false;
        } else {
            Pattern pattern = Pattern.compile (EXPRESSAO_REGULAR_SENHA_FORTE);
            return pattern.matcher(target).matches();
        }
    }
}
