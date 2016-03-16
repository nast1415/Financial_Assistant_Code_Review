package ru.spbau.mit.starlab.financialassistant;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class UserValidation {
    public static final Pattern VALID_EMAIL_ADDRESS_REGEX =
            Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE);

    public static boolean isEmailValid(String emailStr) {
        Matcher matcher = VALID_EMAIL_ADDRESS_REGEX.matcher(emailStr);
        return matcher.find();
    }

    static boolean isPasswordValid(String password) {
        return password.length() > 4;
    }
}
